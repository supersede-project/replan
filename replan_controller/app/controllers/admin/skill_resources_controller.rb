class Admin::SkillResourcesController < Admin::AdminController
  before_action :set_skill_resource, only: [:show, :update, :destroy]

  # GET /skill_resources
  def index
    @skill_resources = SkillResource.all

    render json: @skill_resources
  end

  # GET /skill_resources/1
  def show
    render json: @skill_resource
  end

  # POST /skill_resources
  def create
    @skill_resource = SkillResource.new(skill_resource_params)

    if @skill_resource.save
      render json: @skill_resource, status: :created, location: @skill_resource
    else
      render json: @skill_resource.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /skill_resources/1
  def update
    if @skill_resource.update(skill_resource_params)
      render json: @skill_resource
    else
      render json: @skill_resource.errors, status: :unprocessable_entity
    end
  end

  # DELETE /skill_resources/1
  def destroy
    @skill_resource.destroy
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_skill_resource
      @skill_resource = SkillResource.find(params[:id])
    end

    # Only allow a trusted parameter "white list" through.
    def skill_resource_params
      params.require(:skill_resource).permit(:skill_id, :resource_id)
    end
end
