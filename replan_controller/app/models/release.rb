class Release < ApplicationRecord
  belongs_to :project
  has_many :features, dependent: :nullify
  has_and_belongs_to_many :resources
  has_one :plan, dependent: :destroy
  validates :project_id, presence: true
  validates :starts_at, presence: true
  validates :deadline, presence: true
  validate :start_must_be_before_deadline
  
  def deprecate_plan
    self.plan.deprecate unless self.plan.nil?
  end
  
  def num_weeks
    ((deadline - starts_at)/(7*24*60*60)).round
  end
  
  private

  def start_must_be_before_deadline
    errors.add(:starts_at, "must be before deadline") unless
        starts_at < deadline
  end 
end
