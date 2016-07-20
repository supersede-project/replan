class ResourceSerializer < ActiveModel::Serializer
  attributes :id, :name, :description, :availability, :skills
  
  def skills
    object
      .skills
      .map { |x| SkillSerializer.new(x).as_json }
  end
end
