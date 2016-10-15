class ValentinPlanner
    include ActiveModel::Model
    
  def self.plan(release, project)
    # Your code here
    response = RestClient.post "https://elena-mock-carlesf.c9users.io/api/v1/replan", 
                 self.build_payload(release, project), 
                 {content_type: :json, accept: :json}
    #return JSON.parse(response.body)["jobs"]
    self.build_plan(release, JSON.parse(response.body)["jobs"])
  end
  
  def self.build_payload(release, project)
    nrp = Hash.new
    nrp[:nbWeeks] = 5 # to change
    nrp[:hoursPerWeek] = 40 # to change
    nrp[:features] = release.features.map do |f|
      { name: f.id.to_s,
        duration: f.effort * project.hours_per_effort_unit,
        priority: { level: f.priority, score: f.priority },
        required_skills: f.required_skills.map {|s| {name: s.id.to_s} },
        depends_on: f.depends_on.map {|d| d.id.to_s } }
    end
    nrp[:resources] = project.resources.map do |r|
      { name: r.id.to_s, 
        weekAvailability: r.availability,
        skills: r.skills.map {|s| {name: s.id.to_s} } }
    end
    nrp.to_json
  end
  
  def self.build_plan(release, vjobs)
    plan = Plan.create(release: release)
    date = Date.tomorrow
    vjobs.each do |j|
      Job.create(starts: date.advance(hours: j["beginHour"].to_d), 
                 ends: date.advance(hours: j["endHour"].to_d),
                 feature: Feature.find(j["feature"]["name"]), 
                 resource: Resource.find(j["resource"]["name"]),
                 plan: plan)
    end
    return plan
  end

  #attr_accessor :code, :message, :fields
end