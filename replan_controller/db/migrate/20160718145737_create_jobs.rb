class CreateJobs < ActiveRecord::Migration[5.0]
  def change
    create_table :jobs do |t|
      t.date :starts
      t.date :ends
      t.references :resource, foreign_key: true
      t.references :feature, foreign_key: true

      t.timestamps
    end
  end
end
