class CreatePlans < ActiveRecord::Migration[5.0]
  def change
    create_table :plans do |t|
      t.references :release, foreign_key: true

      t.timestamps
    end
  end
end
