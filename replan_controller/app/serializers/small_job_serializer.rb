class SmallJobSerializer < ActiveModel::Serializer
  attributes :id, :starts, :ends, :feature_id, :resource_id
  
  def required_skills
    object
      .required_skills
      .map { |x| SkillSerializer.new(x).as_json }
  end
  
  def feature_id
    object.feature.id
  end
  
  def resource_id
    object.resource.id
  end
end
