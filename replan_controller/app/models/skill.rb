class Skill < ApplicationRecord
  belongs_to :project
  validates :project_id, presence: true
end
