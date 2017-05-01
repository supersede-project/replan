class FakePlanner
    include ActiveModel::Model
    
  def self.plan(release)
    # Your code here
    project = release.project
    plan = Plan.replan(release)
    date = Date.tomorrow
    release.features.each do |f|
      r = project.resources.order("RANDOM()").limit(1).first
      Job.create(starts: date, ends: date+2, feature: f, resource: r, plan: plan)
      date = date+3
    end
  end

  #attr_accessor :code, :message, :fields
end