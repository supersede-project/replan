class ValentinPlanner
    include ActiveModel::Model
    
  MAX_TIME = 5 # seconds
  MAX_ITERATIONS = 5
    
  def self.plan(release)
    
    uri_P = "http://platform.supersede.eu:8280/replan_optimizer/replan"
    # uri_P = "http://localhost:8280/replan_optimizer/replan"
    uri_D = "http://62.14.219.13:8280/replan_optimizer/replan"
    uri_D2 = "http://62.14.219.13:8280/replan_optimizer_v2/replan"
    
    uris = [uri_P, uri_D, uri_D2]
    
    payload = self.build_payload(release)
    puts "\nCalling replan_optimizer with payload = #{payload}\n"
    numFeatures = release.features.count
    numJobs = 0
    response = "";
    sol = Array.new
    ftime = 0
    mutex = Mutex.new
    threads = []
    uris.each do |uri|
      threads << Thread.new do
        ttime = 0
        it = 0
        until it == MAX_ITERATIONS || ttime > MAX_TIME || numJobs == numFeatures
          jobArray = Array.new
          time = Benchmark.realtime do
            begin
              response = RestClient::Request.execute(method: :post, url: uri, payload: payload,  timeout: MAX_TIME, headers: {content_type: :json, accept: :json})
              #response = RestClient.post uri, payload,  {content_type: :json, accept: :json}
              jobArray = JSON.parse(response.body)["jobs"]
            rescue RestClient::Exceptions::ReadTimeout
            rescue RestClient::InternalServerError
            end
          end
          jobCount = jobArray.count
          puts "#{it+1}# #{uri} -> Num jobs/Num features: #{jobCount}/#{numFeatures} in #{time} seconds"
          if numJobs < jobCount
            mutex.synchronize do
              sol = jobArray
              numJobs = jobCount
            end
          end
          it += 1
          ttime += time
        end
        mutex.synchronize do
          ftime += ttime
        end
      end
    end
    threads.map(&:join)
    puts "FINAL -> Num jobs/Num features: #{numJobs}/#{numFeatures} in #{ftime} seconds"
    self.build_plan(release, sol)
  end
  
  private
  
    def self.build_payload(release)
      project = release.project
      nrp = Hash.new
      nrp[:nbWeeks] = release.num_weeks
      nrp[:hoursPerWeek] = project.hours_per_week_and_full_time_resource
      nrp[:features] = release.features.map {|f| self.build_feature(f) }
      nrp[:resources] = release.resources.map do |r|
        { name: r.id.to_s, 
          availability: r.available_hours_per_week,
          skills: r.skills.map {|s| {name: s.id.to_s} } }
      end
      nrp.to_json
    end
    
    def self.build_feature(feature)
      { name: feature.id.to_s,
        duration: feature.effort_hours,
        priority: { level: feature.priority, score: feature.priority },
        required_skills: feature.required_skills.map {|s| {name: s.id.to_s} },
        depends_on: feature.depends_on.map {|d| self.build_feature(d) unless d.release.nil? || d.release != feature.release}.compact
      }
    end
    
    def self.build_plan(release, vjobs)
      plan = Plan.replan(release)
      vjobs.each do |j|
        Job.create(starts: j["beginHour"].to_i.business_hours.after(release.starts_at), 
                   ends: j["endHour"].to_i.business_hours.after(release.starts_at),
                   feature: Feature.find(j["feature"]["name"]), 
                   resource: Resource.find(j["resource"]["name"]),
                   plan: plan)
      end
    end
end