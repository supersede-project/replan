class JobSerializer < ActiveModel::Serializer
  attributes :id, :starts, :ends, :feature, :resource, :depends_on
  
  def depends_on
    object
      .feature
      .depends_on
      .map { |x| SmallJobSerializer.new(x.job).as_json unless x.job.nil? || x.release.nil? || x.release != object.plan.release}.compact
  end
  
  def feature
    SmallFeatureSerializer.new(object.feature).as_json
  end
  
  def resource
    ResourceSerializer.new(object.resource).as_json
  end
end
