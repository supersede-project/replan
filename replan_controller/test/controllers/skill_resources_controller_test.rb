require 'test_helper'

class SkillResourcesControllerTest < ActionDispatch::IntegrationTest
  setup do
    @skill_resource = skill_resources(:one)
  end

  test "should get index" do
    get skill_resources_url, as: :json
    assert_response :success
  end

  test "should create skill_resource" do
    assert_difference('SkillResource.count') do
      post skill_resources_url, params: { skill_resource: { resource_id: @skill_resource.resource_id, skill_id: @skill_resource.skill_id } }, as: :json
    end

    assert_response 201
  end

  test "should show skill_resource" do
    get skill_resource_url(@skill_resource), as: :json
    assert_response :success
  end

  test "should update skill_resource" do
    patch skill_resource_url(@skill_resource), params: { skill_resource: { resource_id: @skill_resource.resource_id, skill_id: @skill_resource.skill_id } }, as: :json
    assert_response 200
  end

  test "should destroy skill_resource" do
    assert_difference('SkillResource.count', -1) do
      delete skill_resource_url(@skill_resource), as: :json
    end

    assert_response 204
  end
end
