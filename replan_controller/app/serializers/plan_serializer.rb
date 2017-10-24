class PlanSerializer < ActiveModel::Serializer
  attributes :id, :created_at, :release_id, :num_features, :num_jobs, :jobs, :resource_usage
  
  def num_features
    object.release.features.count
  end
  
  def num_jobs
    object.jobs.count
  end
  
  def jobs
    object
      .jobs.joins(:resource).order('resources.name')
      .map { |x| JobSerializer.new(x).as_json  }
  end
  
  def resource_usage
    nbwks = object.release.num_weeks
    object
      .release.resources.order(:name)
      .map { |r| {"resource_id" => r.id,
                  "resource_name" => r.name,
                  "total_available_hours" => (r.available_hours_per_week * nbwks).to_f,
                  "total_used_hours" => (object.jobs.map{ |j| j.resource.id == r.id ? j.feature.effort_hours : 0}.sum).to_f,
                  "skills" => r.skills.map { |x| SkillSerializer.new(x) }}
      }.as_json
  end
end
