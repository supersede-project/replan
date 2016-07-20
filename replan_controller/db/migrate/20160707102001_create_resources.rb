class CreateResources < ActiveRecord::Migration[5.0]
  def change
    create_table :resources do |t|
      t.string :name
      t.string :description
      t.decimal :availability
      t.references :project, foreign_key: true

      t.timestamps
    end
  end
end
