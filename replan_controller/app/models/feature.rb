class Feature < ApplicationRecord
  belongs_to :project
  belongs_to :release, optional: true
  has_many :jobs
  has_and_belongs_to_many :depends_on, 
              class_name: "Feature", 
              join_table: :dependencies, 
              foreign_key: :feature_id, 
              association_foreign_key: :depending_id
  has_and_belongs_to_many :required_skills,
              class_name: "Skill", 
              join_table: :features_skills, 
              foreign_key: :feature_id, 
              association_foreign_key: :skill_id
  validates :project_id, presence: true
  
  def effort_hours
    effort * project.hours_per_effort_unit
  end
  
  def current_job
    jobs.select{ |j| !j.plan.release.nil?}.first
  end
end
