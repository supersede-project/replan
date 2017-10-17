class AddJiraUrlToFeatures < ActiveRecord::Migration[5.1]
  def change
    add_column :features, :jira_url, :string
  end
end
