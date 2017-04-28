class RemoveReleaseFromPlans < ActiveRecord::Migration[5.0]
  def change
    remove_reference :plans, :release, foreign_key: true
  end
end
