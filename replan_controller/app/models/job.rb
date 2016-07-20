class Job < ApplicationRecord
  belongs_to :resource
  belongs_to :feature
  belongs_to :plan
  validates :plan_id, presence: true
end
