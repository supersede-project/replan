class JobSerializer < ActiveModel::Serializer
  attributes :starts, :ends, :feature, :resource
  
  def required_skills
    object
      .required_skills
      .map { |x| SkillSerializer.new(x).as_json }
  end
  
  def feature
    SmallFeatureSerializer.new(object.feature).as_json
  end
  
  def resource
    ResourceSerializer.new(object.resource).as_json
  end
end
