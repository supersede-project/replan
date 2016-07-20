class Resource < ApplicationRecord
  belongs_to :project
  has_and_belongs_to_many :releases
  has_and_belongs_to_many :skills
  validates :project_id, presence: true
end
