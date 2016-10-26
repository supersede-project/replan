class AdaptDbToOptimizer < ActiveRecord::Migration[5.0]
  def change
    change_column :jobs, :starts, :datetime
    change_column :jobs, :ends, :datetime
    change_column :releases, :deadline, :datetime
    add_column :releases, :starts_at, :datetime
  end
end
