class Plan < ApplicationRecord
  belongs_to :release, optional: true
  belongs_to :prev_plan, class_name: "Plan", foreign_key: "plan_id", optional: true, dependent: :destroy
  has_many :jobs, dependent: :destroy
  
  def self.get_plan(release, force_new)
    if force_new
      if release.starts_at.nil?
        FakePlanner.plan(release)
      else
        ValentinPlanner.plan(release)
      end
    end
    return release.plan
  end
  
  def self.replan(release)
    pplan = release.plan
    plan = Plan.new(release: release)
    unless pplan.nil? 
      pplan.release = nil
      pplan.save
      plan.prev_plan = pplan
    end
    plan.save
    return plan
  end
  
  def deprecate
    self.destroy
  end
  
  def cancel
    unless self.prev_plan.nil? 
      self.prev_plan.release = self.release
      self.prev_plan.save
    end
    self.destroy
  end
end
