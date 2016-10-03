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
class FeaturesController < ApplicationController
  before_action :set_feature, only: [:get_feature, :modify_feature, 
                                     :remove_feature_from_release,
                                     :add_skills_to_feature,
                                     :delete_skills_from_feature,
                                     :add_dependencies_to_feature,
                                     :delete_dependencies_from_feature]

  
  # WP3 API method
  def receive_wp3_features

    params[:features].each do |f|
      begin
        @project.features.create(feature_params(f))
      rescue ActiveRecord::RecordNotUnique
        error = Error.new(code:400,
                message: "Already exists a feature with code = #{f[:code]}", 
                fields: "feature.code")
        render json: error, status: 400 and return
        exit
      end
    end
    test = Array.new
    params[:features].each do |f|
      feature = @project.features.find_by(code: f[:code])
      test.push(feature)
        f[:hard_dependencies].each do |d|
          f2 = @project.features.find_by(code: d)
          if f2 and not feature.depends_on.exists?(code: d) \
                     and feature.code != d
            feature.depends_on << f2
          end
        end
    end
    render json: @project.features

  end
  
  # ---------------------------------------------------------------------------
  # UI API methods
  def get_feature
    render json: @feature
  end

  def get_features
    filter = params[:status]
    if not filter.nil?
      case filter
      when "any"
        @features = @project.features
      when "pending" 
         @features = @project.features.where("release_id is NULL")
      when "scheduled" 
        @features = @project.features.where("release_id is not NULL")
      else
        render(status: :bad_request) and return
      end
    else
      @features = @project.features
    end
    render json: @features
  end

  def modify_feature
    if @feature.update(feature_params)
      render json: @feature
    else
      render json: @feature.errors, status: :unprocessable_entity
    end
  end
  
  def add_skills_to_feature
    params[:_json].each do |s|
        skill = @project.skills.find_by(id: s[:skill_id])
          if skill and not @feature.required_skills.exists?(id: s[:skill_id])
            @feature.required_skills << skill
          end
    end
    render json: @feature
  end

  def delete_skills_from_feature
    params[:skill_id].each do |i|
        skill = @feature.required_skills.find_by(id: i)
          if skill
            @feature.required_skills.delete(skill)
          end
      end
    render json: @feature
  end

  def add_dependencies_to_feature
    params[:_json].each do |f|
        feature = @project.features.find_by(id: f[:feature_id])
          if feature and not @feature.depends_on.exists?(id: f[:feature_id]) \
                     and @feature.id != feature.id
            @feature.depends_on << feature
          end
    end
    render json: @feature
  end

  def delete_dependencies_from_feature
    params[:feature_id].split(",").each do |i|
        feature = @feature.depends_on.find_by(id: i)
          if feature
            @feature.depends_on.delete(feature)
          end
      end
    render json: @feature
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_feature
      @feature = @project.features.find(params[:featureId])
    end
    
    def feature_params(jf)
      # The 'id' from WP3 becomes 'code'
      jf[:code] = jf.delete :id
      
      # Only two properties are taken into accound: 'description' & 'deadline'
      if jf[:properties].any?
        jf[:description] = jf[:properties].find{|tl| tl[:key] == 'description'}[:value]
        jf[:deadline] = jf[:properties].find{|tl| tl[:key] == 'deadline'}[:value]
      end
      
      jf.permit(:code, :name, :effort, :priority, :description, :deadline)
    end
end
