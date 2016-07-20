require 'test_helper'

class ProjectsControllerTest < ActionDispatch::IntegrationTest
  setup do
    @project = projects(:one)
  end

  test "should get index" do
    get projects_url, as: :json
    assert_response :success
  end

  test "should create project" do
    assert_difference('Project.count') do
      post projects_url, params: { project: { description: @project.description, effort_unit: @project.effort_unit, hours_per_effort_unit: @project.hours_per_effort_unit, hours_per_week_and_full_time_resource: @project.hours_per_week_and_full_time_resource, name: @project.name } }, as: :json
    end

    assert_response 201
  end

  test "should show project" do
    get project_url(@project), as: :json
    assert_response :success
  end

  test "should update project" do
    patch project_url(@project), params: { project: { description: @project.description, effort_unit: @project.effort_unit, hours_per_effort_unit: @project.hours_per_effort_unit, hours_per_week_and_full_time_resource: @project.hours_per_week_and_full_time_resource, name: @project.name } }, as: :json
    assert_response 200
  end

  test "should destroy project" do
    assert_difference('Project.count', -1) do
      delete project_url(@project), as: :json
    end

    assert_response 204
  end
end
