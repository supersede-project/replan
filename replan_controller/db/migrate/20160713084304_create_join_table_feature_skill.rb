class CreateJoinTableFeatureSkill < ActiveRecord::Migration[5.0]
  def change
    create_join_table :features, :skills do |t|
      # t.index [:feature_id, :skill_id]
      # t.index [:skill_id, :feature_id]
    end
  end
end
