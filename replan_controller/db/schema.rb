# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20171017132802) do

  create_table "dependencies", id: false, force: :cascade do |t|
    t.integer "feature_id"
    t.integer "depending_id"
    t.index ["depending_id", "feature_id"], name: "index_dependencies_on_depending_id_and_feature_id", unique: true
    t.index ["feature_id", "depending_id"], name: "index_dependencies_on_feature_id_and_depending_id", unique: true
  end

  create_table "features", force: :cascade do |t|
    t.integer "code"
    t.string "name"
    t.string "description"
    t.decimal "effort"
    t.date "deadline"
    t.integer "priority"
    t.integer "project_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.integer "release_id"
    t.string "jira_url"
    t.index ["project_id"], name: "index_features_on_project_id"
    t.index ["release_id"], name: "index_features_on_release_id"
  end

  create_table "features_skills", id: false, force: :cascade do |t|
    t.integer "feature_id", null: false
    t.integer "skill_id", null: false
  end

  create_table "jobs", force: :cascade do |t|
    t.datetime "starts"
    t.datetime "ends"
    t.integer "resource_id"
    t.integer "feature_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.integer "plan_id"
    t.index ["feature_id"], name: "index_jobs_on_feature_id"
    t.index ["plan_id"], name: "index_jobs_on_plan_id"
    t.index ["resource_id"], name: "index_jobs_on_resource_id"
  end

  create_table "plans", force: :cascade do |t|
    t.integer "release_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.integer "plan_id"
    t.index ["plan_id"], name: "index_plans_on_plan_id"
    t.index ["release_id"], name: "index_plans_on_release_id"
  end

  create_table "projects", force: :cascade do |t|
    t.string "name"
    t.string "description"
    t.string "effort_unit"
    t.decimal "hours_per_effort_unit"
    t.decimal "hours_per_week_and_full_time_resource"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  create_table "releases", force: :cascade do |t|
    t.string "name"
    t.string "description"
    t.integer "project_id"
    t.datetime "deadline"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.datetime "starts_at"
    t.index ["project_id"], name: "index_releases_on_project_id"
  end

  create_table "releases_resources", id: false, force: :cascade do |t|
    t.integer "release_id", null: false
    t.integer "resource_id", null: false
    t.index ["release_id", "resource_id"], name: "index_releases_resources_on_release_id_and_resource_id"
    t.index ["resource_id", "release_id"], name: "index_releases_resources_on_resource_id_and_release_id"
  end

  create_table "resources", force: :cascade do |t|
    t.string "name"
    t.string "description"
    t.decimal "availability"
    t.integer "project_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["project_id"], name: "index_resources_on_project_id"
  end

  create_table "resources_skills", id: false, force: :cascade do |t|
    t.integer "skill_id", null: false
    t.integer "resource_id", null: false
  end

  create_table "skills", force: :cascade do |t|
    t.string "name"
    t.string "description"
    t.integer "project_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["project_id"], name: "index_skills_on_project_id"
  end

end
