class FeatureSerializer < ActiveModel::Serializer
  attributes :id, :code, :name, :description, :effort, :deadline, :priority, :required_skills, :depends_on, :release
  
  def required_skills
    object
      .required_skills
      .map { |x| SkillSerializer.new(x).as_json }
  end
  
  def depends_on
    object
      .depends_on
      .map { |x| SmallFeatureSerializer.new(x).as_json }
  end
  
  def release
    if not object.release.nil?
      {"release_id" => object.release.id}.as_json
    else
      "pending"
    end
  end
end
