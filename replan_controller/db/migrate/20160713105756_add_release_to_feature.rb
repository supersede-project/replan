class AddReleaseToFeature < ActiveRecord::Migration[5.0]
  def change
    add_reference :features, :release, foreign_key: true
  end
end
