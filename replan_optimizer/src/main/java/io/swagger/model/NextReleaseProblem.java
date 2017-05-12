package io.swagger.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;




/**
 * NextReleaseProblem
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

public class NextReleaseProblem   {

  // This one is not @Generated, actually
  private PlanningSolution previousPlan;

  private Integer nbWeeks = null;

  private Double hoursPerWeek = null;

  private List<Feature> features = new ArrayList<Feature>();

  private List<Resource> resources = new ArrayList<Resource>();

    public NextReleaseProblem() {}

    public NextReleaseProblem(Integer nbWeeks, Double hoursPerWeek, List<Feature> features, List<Resource> resources) {
        this.nbWeeks = nbWeeks;
        this.hoursPerWeek = hoursPerWeek;
        this.features = features;
        this.resources = resources;
        previousPlan = null;
    }

    public NextReleaseProblem(
            Integer nbWeeks, Double hoursPerWeek, List<Feature> features, List<Resource> resources,
            PlanningSolution previousPlan)
    {
        this(nbWeeks, hoursPerWeek, features, resources);
        this.previousPlan = previousPlan;
    }




  public NextReleaseProblem nbWeeks(Integer nbWeeks) {
    this.nbWeeks = nbWeeks;
    return this;
  }


    // Not @Generated
    /*
    @ApiModelProperty(value = "")
    public PlanningSolution getPreviousPlan() {
        return previousPlan;
    }

    public void setPreviousPlan(PlanningSolution plan) {
        previousPlan = plan;
    }
    */


   /**
   * Get nbWeeks
   * @return nbWeeks
  **/
  @ApiModelProperty(value = "")
  public Integer getNbWeeks() {
    return nbWeeks;
  }

  public void setNbWeeks(Integer nbWeeks) {
    this.nbWeeks = nbWeeks;
  }

  public NextReleaseProblem hoursPerWeek(Double hoursPerWeek) {
    this.hoursPerWeek = hoursPerWeek;
    return this;
  }

   /**
   * Get hoursPerWeek
   * @return hoursPerWeek
  **/
  @ApiModelProperty(value = "")
  public Double getHoursPerWeek() {
    return hoursPerWeek;
  }

  public void setHoursPerWeek(Double hoursPerWeek) {
    this.hoursPerWeek = hoursPerWeek;
  }

  public NextReleaseProblem features(List<Feature> features) {
    this.features = features;
    return this;
  }

  public NextReleaseProblem addFeaturesItem(Feature featuresItem) {
    this.features.add(featuresItem);
    return this;
  }

   /**
   * Get features
   * @return features
  **/
  @ApiModelProperty(value = "")
  public List<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  public NextReleaseProblem resources(List<Resource> resources) {
    this.resources = resources;
    return this;
  }

  public NextReleaseProblem addResourcesItem(Resource resourcesItem) {
    this.resources.add(resourcesItem);
    return this;
  }

   /**
   * Get resources
   * @return resources
  **/
  @ApiModelProperty(value = "")
  public List<Resource> getResources() {
    return resources;
  }

  public void setResources(List<Resource> resources) {
    this.resources = resources;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NextReleaseProblem nextReleaseProblem = (NextReleaseProblem) o;
    return Objects.equals(this.nbWeeks, nextReleaseProblem.nbWeeks) &&
        Objects.equals(this.hoursPerWeek, nextReleaseProblem.hoursPerWeek) &&
        Objects.equals(this.features, nextReleaseProblem.features) &&
        Objects.equals(this.resources, nextReleaseProblem.resources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nbWeeks, hoursPerWeek, features, resources);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NextReleaseProblem {\n");
    
    sb.append("    nbWeeks: ").append(toIndentedString(nbWeeks)).append("\n");
    sb.append("    hoursPerWeek: ").append(toIndentedString(hoursPerWeek)).append("\n");
    sb.append("    features: ").append(toIndentedString(features)).append("\n");
    sb.append("    resources: ").append(toIndentedString(resources)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

