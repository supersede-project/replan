class Resource < ApplicationRecord
  belongs_to :project
  has_and_belongs_to_many :releases
  has_and_belongs_to_many :skills
  validates :project_id, presence: true
  
  def available_hours_per_week
    availability*0.01*project.hours_per_week_and_full_time_resource 
  end
  
end
