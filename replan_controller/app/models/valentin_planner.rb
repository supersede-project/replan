class ValentinPlanner
    include ActiveModel::Model
    
  def self.plan(release, project)
    # Your code here
    plan = Plan.create(release: release)
    date = Date.tomorrow
    release.features.each do |f|
      r = project.resources.order("RANDOM()").limit(1).first
      Job.create(starts: date, ends: date+2, feature: f, resource: r, plan: plan)
      date = date+3
    end
    return plan
  end
  
  def self.build_payload(release, project)
    nrp = Hash.new
    nrp[:nbWeeks] = 5 # to change
    nrp[:hoursPerWeek] = 40 # to change
    features = Array.new
    release.features.each do |f|
      feat = Hash.new
      feat[:name] = f.id.to_s
     # feat[:duration] = f.
      
    end
  end

  #attr_accessor :code, :message, :fields
end