class CreateJoinTableSkillResource < ActiveRecord::Migration[5.0]
  def change
    create_join_table :skills, :resources do |t|
      # t.index [:skill_id, :resource_id]
      # t.index [:resource_id, :skill_id]
    end
  end
end
