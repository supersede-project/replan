class ProjectSerializer < ActiveModel::Serializer
  attributes :id, :name, :description, :effort_unit, :hours_per_effort_unit, :hours_per_week_and_full_time_resource, :resources
  
  def resources
    object
      .resources
      .map { |x| ResourceSerializer.new(x).as_json }
  end
end
