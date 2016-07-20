class CreateJoinTableReleaseResource < ActiveRecord::Migration[5.0]
  def change
    create_join_table :releases, :resources do |t|
      t.index [:release_id, :resource_id]
      t.index [:resource_id, :release_id]
    end
  end
end
