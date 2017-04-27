class AddPreviousPlanRefToPlans < ActiveRecord::Migration[5.0]
  def change
    add_reference :plans, :plan, foreign_key: true
  end
end
