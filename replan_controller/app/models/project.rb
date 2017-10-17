class Project < ApplicationRecord
    has_many :skills, dependent: :destroy
    has_many :resources, dependent: :destroy
    has_many :releases, dependent: :destroy
    has_many :features, dependent: :destroy
end
