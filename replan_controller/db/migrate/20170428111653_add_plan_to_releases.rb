class AddPlanToReleases < ActiveRecord::Migration[5.0]
  def change
    add_reference :releases, :plan, foreign_key: true
  end
end
