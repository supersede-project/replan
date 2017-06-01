class JobSerializer < ActiveModel::Serializer
  attributes :id, :starts, :ends, :feature, :resource, :depends_on
  
  def depends_on
    object
      .feature
      .depends_on
      .map { |x| SmallJobSerializer.new(x.jobs.find_by(plan_id: object.plan_id)).as_json unless x.jobs.find_by(plan_id: object.plan_id).nil?}.compact
  end
  
  def feature
    SmallFeatureSerializer.new(object.feature).as_json
  end
  
  def resource
    ResourceSerializer.new(object.resource).as_json
  end
end
