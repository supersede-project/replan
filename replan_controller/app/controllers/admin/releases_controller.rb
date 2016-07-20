class Admin::ReleasesController < Admin::AdminController
  before_action :set_release, only: [:show, :update, :destroy]

  # GET /admin/releases
  def index
    @releases = Admin::Release.all

    render json: @releases
  end

  # GET /admin/releases/1
  def show
    render json: @release
  end

  # POST /admin/releases
  def create
    @release = Admin::Release.new(release_params)

    if @release.save
      render json: @release, status: :created, location: @release
    else
      render json: @release.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /admin/releases/1
  def update
    if @release.update(release_params)
      render json: @release
    else
      render json: @release.errors, status: :unprocessable_entity
    end
  end

  # DELETE /admin/releases/1
  def destroy
    @release.destroy
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_release
      @release = Admin::Release.find(params[:id])
    end

    # Only allow a trusted parameter "white list" through.
    def release_params
      params.require(:release).permit(:name, :description, :project_id, :deadline)
    end
end
