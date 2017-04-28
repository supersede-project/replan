class PlanSerializer < ActiveModel::Serializer
  attributes :id, :created_at, :release_id, :jobs
  
  def release_id
    object.release.id
  end
  
  def jobs
    object
      .jobs
      .map { |x| JobSerializer.new(x).as_json  }
  end
end
