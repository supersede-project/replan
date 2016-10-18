class ValentinPlanner
    include ActiveModel::Model
    
  def self.plan(release, project)
    # Your code here
    uri = "http://ec2-52-57-161-221.eu-central-1.compute.amazonaws.com:8080/api/v1/replan"
  # uri = "https://elena-mock-carlesf.c9users.io/api/v1/replan"
    response = RestClient.post uri, self.build_payload(release, project),  {content_type: :json, accept: :json}
    self.build_plan(release, JSON.parse(response.body)["jobs"])
  end
  
  def self.build_payload(release, project)
    nrp = Hash.new
    nrp[:nbWeeks] = ((release.deadline - release.starts_at)/(7*24*60*60)).round
    nrp[:hoursPerWeek] = project.hours_per_week_and_full_time_resource
    nrp[:features] = release.features.map do |f|
      { name: f.id.to_s,
        duration: f.effort * project.hours_per_effort_unit,
        priority: { level: f.priority, score: f.priority },
        requiredSkills: f.required_skills.map {|s| {name: s.id.to_s} },
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
    vjobs.each do |j|
      Job.create(starts: j["beginHour"].to_i.business_hours.after(release.starts_at), 
                 ends: j["endHour"].to_i.business_hours.after(release.starts_at),
                 feature: Feature.find(j["feature"]["name"]), 
                 resource: Resource.find(j["resource"]["name"]),
                 plan: plan)
    end
    return plan
  end

  #attr_accessor :code, :message, :fields
end