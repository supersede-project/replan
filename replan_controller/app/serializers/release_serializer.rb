class ReleaseSerializer < ActiveModel::Serializer
  attributes :id, :name, :description, :deadline, :resources
  
  def resources
    object
      .resources
      .map { |x| ResourceSerializer.new(x).as_json }
  end
end
