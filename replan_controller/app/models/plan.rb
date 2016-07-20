class Plan < ApplicationRecord
  belongs_to :release
  has_many :jobs, :dependent => :destroy
end
