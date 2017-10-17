class FeatureSerializer < ActiveModel::Serializer
  attributes :id, :code, :name, :jira_url, :description, :effort, :deadline, :priority, :required_skills, :depends_on, :release
  
  def effort
    object.effort.to_f
  end
  
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
      release_info = {"release_id" => object.release.id}
      if not object.current_job.nil?
        job = object.current_job
        aux = Hash.new
        aux["resource_id"] = job.resource_id
        aux["resource_name"] = job.resource.name
        aux["starts_at"] = job.starts
        aux["ends_at"] = job.ends
        release_info["assigned_to"] = aux
      end
      release_info.as_json
    else
      "pending"
    end
  end
end
