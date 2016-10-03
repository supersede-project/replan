class AddIndexToFeatures < ActiveRecord::Migration[5.0]
  def change
    add_index :features, :code, unique: true
  end
end
