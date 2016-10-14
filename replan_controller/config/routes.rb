Rails.application.routes.draw do
=begin
  namespace :admin do
    resources :releases
    resources :skill_resources
    resources :skills
    resources :resources
    resources :projects
  end
=end 
  def add_swagger_route http_method, path, opts = {}
    full_path = path.gsub(/{(.*?)}/, ':\1')
    match full_path, to: "#{opts.fetch(:controller_name)}##{opts[:action_name]}", via: http_method
  end
  
  add_swagger_route 'POST', '/api/wp3/v1/projects/{projectId}/features', controller_name: 'features', action_name: 'receive_wp3_features'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/features', controller_name: 'releases', action_name: 'add_features_to_release'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/features/{featureId}', controller_name: 'features', action_name: 'get_feature'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/features', controller_name: 'features', action_name: 'get_features'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/features', controller_name: 'releases', action_name: 'get_release_features'
  add_swagger_route 'PUT', '/api/ui/v1/projects/{projectId}/features/{featureId}', controller_name: 'features', action_name: 'modify_feature'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/features', controller_name: 'releases', action_name: 'remove_features_from_release'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}', controller_name: 'project', action_name: 'get_project'
  add_swagger_route 'PUT', '/api/ui/v1/projects/{projectId}', controller_name: 'project', action_name: 'modify_project'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/features', controller_name: 'releases', action_name: 'add_feature_to_release'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/releases', controller_name: 'releases', action_name: 'add_new_release_to_project'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/plan', controller_name: 'releases', action_name: 'cancel_last_release_plan'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/releases/{releaseId}', controller_name: 'releases', action_name: 'delete_release'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/features', controller_name: 'releases', action_name: 'get_release_features'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/plan', controller_name: 'releases', action_name: 'get_release_plan'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/releases', controller_name: 'releases', action_name: 'get_releases'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/releases/{releaseId}', controller_name: 'releases', action_name: 'get_release'
  add_swagger_route 'PUT', '/api/ui/v1/projects/{projectId}/releases/{releaseId}', controller_name: 'releases', action_name: 'modify_release'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/features/{featureId}', controller_name: 'releases', action_name: 'remove_feature_from_release'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/resources', controller_name: 'resources', action_name: 'add_new_resource_to_project'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/resources/{resourceId}', controller_name: 'resources', action_name: 'delete_resource'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/resources', controller_name: 'resources', action_name: 'get_project_resources'
  add_swagger_route 'PUT', '/api/ui/v1/projects/{projectId}/resources/{resourceId}', controller_name: 'resources', action_name: 'modify_resource'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/skills', controller_name: 'skills', action_name: 'add_new_skill_to_project'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/skills/{skillId}', controller_name: 'skills', action_name: 'delete_skill'
  add_swagger_route 'GET', '/api/ui/v1/projects/{projectId}/skills', controller_name: 'skills', action_name: 'get_project_skills'
  add_swagger_route 'PUT', '/api/ui/v1/projects/{projectId}/skills/{skillId}', controller_name: 'skills', action_name: 'modify_skill' 
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/resources/{resourceId}/skills', controller_name: 'resources', action_name: 'delete_skills_from_resource'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/resources/{resourceId}/skills', controller_name: 'resources', action_name: 'add_skills_to_resource'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/resources', controller_name: 'releases', action_name: 'add_resources_to_release'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/releases/{releaseId}/resources', controller_name: 'releases', action_name: 'delete_resources_from_release'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/features/{featureId}/skills', controller_name: 'features', action_name: 'add_skills_to_feature'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/features/{featureId}/skills', controller_name: 'features', action_name: 'delete_skills_from_feature'
  add_swagger_route 'POST', '/api/ui/v1/projects/{projectId}/features/{featureId}/dependencies', controller_name: 'features', action_name: 'add_dependencies_to_feature'
  add_swagger_route 'DELETE', '/api/ui/v1/projects/{projectId}/features/{featureId}/dependencies', controller_name: 'features', action_name: 'delete_dependencies_from_feature'
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
