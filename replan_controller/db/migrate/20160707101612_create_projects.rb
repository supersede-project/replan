class CreateProjects < ActiveRecord::Migration[5.0]
  def change
    create_table :projects do |t|
      t.string :name
      t.string :description
      t.string :effort_unit
      t.decimal :hours_per_effort_unit
      t.decimal :hours_per_week_and_full_time_resource

      t.timestamps
    end
  end
end
