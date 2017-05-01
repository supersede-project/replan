class Plan < ApplicationRecord
  belongs_to :release, optional: true
  belongs_to :prev_plan, class_name: "Plan", foreign_key: "plan_id", optional: true
  has_many :jobs, :dependent => :destroy
  
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
      if pplan.deprecated?
        pplan.destroy
      else
        pplan.update_columns(release_id: nil)
        plan.prev_plan = pplan
      end
    end
    plan.save
    return plan
  end
  
  def deprecate
    self.updated_at = DateTime.now
    self.save
  end
  
  def deprecated?
    self.created_at != self.updated_at
  end
  
  def cancel
    unless self.prev_plan.nil? 
      self.prev_plan.update_columns(release_id: self.release.id)
    end
    self.destroy
  end
end
