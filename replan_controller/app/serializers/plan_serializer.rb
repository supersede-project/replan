class PlanSerializer < ActiveModel::Serializer
  attributes :id, :created_at, :release_id, :num_features, :num_jobs, :jobs
  
  def num_features
    object.release.features.count
  end
  
  def num_jobs
    object.jobs.count
  end
  
  def jobs
    object
      .jobs
      .map { |x| JobSerializer.new(x).as_json  }
  end
end
