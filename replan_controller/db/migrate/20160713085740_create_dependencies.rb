class CreateDependencies < ActiveRecord::Migration[5.0]
  def change
    create_table :dependencies, id: false do |t|
      t.integer :feature_id
      t.integer :depending_id
    end

    add_index(:dependencies, [:feature_id, :depending_id], :unique => true)
    add_index(:dependencies, [:depending_id, :feature_id], :unique => true)
  end
end
