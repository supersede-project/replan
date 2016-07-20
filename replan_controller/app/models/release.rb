class Release < ApplicationRecord
  belongs_to :project
  has_many :features, dependent: :nullify
  has_and_belongs_to_many :resources
  has_one :plan
  validates :project_id, presence: true
end
