class Project < ApplicationRecord
    has_many :skills
    has_many :resources
    has_many :releases
    has_many :features
end
