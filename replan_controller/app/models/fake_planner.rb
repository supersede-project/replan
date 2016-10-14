class FakePlanner
    include ActiveModel::Model
    
  def self.plan(release, resources)
    # Your code here
    plan = Plan.create(release: release)
    date = Date.tomorrow
    release.features.each do |f|
      r = resources.order("RANDOM()").limit(1).first
      Job.create(starts: date, ends: date+2, feature: f, resource: r, plan: plan)
      date = date+3
    end
    return plan
  end

  #attr_accessor :code, :message, :fields
end