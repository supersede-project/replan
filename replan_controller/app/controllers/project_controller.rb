=begin
SUPERSEDE ReleasePlanner API to UI

OpenAPI spec version: 1.0.0

Generated by: https://github.com/swagger-api/swagger-codegen.git

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end
class ProjectController < ApplicationController

  # New in v.2
  def get_projects 
    render json: Project.all
  end
  
  def create_project
    @project = Project.new(project_params)

    if @project.save
      render json: @project
    else
      render json: @project.errors, status: :unprocessable_entity
    end
  end

  # ---
  def get_project
    render json: @project
  end

  def modify_project
    if @project.update(project_params)
      render json: @project
    else
      render json: @project.errors, status: :unprocessable_entity
    end
  end
  
  private
    # Only allow a trusted parameter "white list" through.
    def project_params
      params.require(:project).permit(:name, :description, :effort_unit, :hours_per_effort_unit, :hours_per_week_and_full_time_resource)
    end
end
