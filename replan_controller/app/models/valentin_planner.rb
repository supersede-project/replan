class ValentinPlanner
    include ActiveModel::Model
    
  def self.plan(release)
    # Your code here
    uri = "http://62.14.219.13:8280/replan_optimizer/replan"
  # uri = "http://localhost:8280/replan_optimizer/replan"
    response = RestClient.post uri, self.build_payload(release),  {content_type: :json, accept: :json}
    self.build_plan(release, JSON.parse(response.body)["jobs"])
  end
  
  private
  
    def self.build_payload(release)
      project = release.project
      nrp = Hash.new
      nrp[:nbWeeks] = ((release.deadline - release.starts_at)/(7*24*60*60)).round
      nrp[:hoursPerWeek] = project.hours_per_week_and_full_time_resource
      nrp[:features] = release.features.map {|f| self.build_feature(f) }
      nrp[:resources] = release.resources.map do |r|
        { name: r.id.to_s, 
          availability: r.availability,
          skills: r.skills.map {|s| {name: s.id.to_s} } }
      end
      nrp.to_json
    end
    
    def self.build_feature(feature)
      { name: feature.id.to_s,
        duration: feature.effort * feature.project.hours_per_effort_unit,
        priority: { level: feature.priority, score: feature.priority },
        required_skills: feature.required_skills.map {|s| {name: s.id.to_s} },
        depends_on: feature.depends_on.map {|d| self.build_feature(d) }
      }
    end
    
    def self.build_plan(release, vjobs)
      release.plan.destroy if !release.plan.nil?
      plan = Plan.create(release: release)
      vjobs.each do |j|
        Job.create(starts: j["beginHour"].to_i.business_hours.after(release.starts_at), 
                   ends: j["endHour"].to_i.business_hours.after(release.starts_at),
                   feature: Feature.find(j["feature"]["name"]), 
                   resource: Resource.find(j["resource"]["name"]),
                   plan: plan)
      end
    end
end