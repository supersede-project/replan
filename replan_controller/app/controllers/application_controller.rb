class ApplicationController < ActionController::API
  
  before_action :set_project
  
  def set_project
      @project = Project.find(params[:projectId])
  end
  
end