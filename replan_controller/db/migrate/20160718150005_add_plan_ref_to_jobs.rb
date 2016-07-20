class AddPlanRefToJobs < ActiveRecord::Migration[5.0]
  def change
    add_reference :jobs, :plan, foreign_key: true
  end
end
