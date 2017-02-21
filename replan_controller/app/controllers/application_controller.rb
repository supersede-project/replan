class ApplicationController < ActionController::API
  
  before_action :set_project
  
  def set_project
      @project = Project.find(params[:projectId]) if params.has_key?(:projectId)
  end
  
end