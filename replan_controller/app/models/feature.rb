class Feature < ApplicationRecord
  belongs_to :project
  belongs_to :release, optional: true
  has_one :job
  has_one :plan, through: :job
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
end
