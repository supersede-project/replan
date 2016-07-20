class CreateFeatures < ActiveRecord::Migration[5.0]
  def change
    create_table :features do |t|
      t.integer :code
      t.string :name
      t.string :description
      t.decimal :effort
      t.date :deadline
      t.integer :priority
      t.references :project, foreign_key: true

      t.timestamps
    end
  end
end
