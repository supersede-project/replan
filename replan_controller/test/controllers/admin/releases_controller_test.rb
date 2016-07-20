require 'test_helper'

class Admin::ReleasesControllerTest < ActionDispatch::IntegrationTest
  setup do
    @admin_release = admin_releases(:one)
  end

  test "should get index" do
    get admin_releases_url, as: :json
    assert_response :success
  end

  test "should create admin_release" do
    assert_difference('Admin::Release.count') do
      post admin_releases_url, params: { admin_release: { deadline: @admin_release.deadline, description: @admin_release.description, name: @admin_release.name, project_id: @admin_release.project_id } }, as: :json
    end

    assert_response 201
  end

  test "should show admin_release" do
    get admin_release_url(@admin_release), as: :json
    assert_response :success
  end

  test "should update admin_release" do
    patch admin_release_url(@admin_release), params: { admin_release: { deadline: @admin_release.deadline, description: @admin_release.description, name: @admin_release.name, project_id: @admin_release.project_id } }, as: :json
    assert_response 200
  end

  test "should destroy admin_release" do
    assert_difference('Admin::Release.count', -1) do
      delete admin_release_url(@admin_release), as: :json
    end

    assert_response 204
  end
end
