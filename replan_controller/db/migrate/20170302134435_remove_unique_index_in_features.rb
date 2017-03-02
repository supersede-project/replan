class RemoveUniqueIndexInFeatures < ActiveRecord::Migration[5.0]
  def change
    remove_index :features, :code
  end
end
