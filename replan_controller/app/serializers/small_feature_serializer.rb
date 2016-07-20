class SmallFeatureSerializer < ActiveModel::Serializer
  attributes :id, :code, :name, :description, :effort, :deadline, :priority, :release
  
  def release
    if not object.release.nil?
      {"release_id" => object.release.id}.as_json
    else
      "pending"
    end
  end
end
