package io.swagger.model;

import entities.Employee;
import entities.Feature;
import io.swagger.annotations.ApiModelProperty;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience data class for receiving data from the API call.
 * Just because the one in model is pretty crowded and complex.
 */
public class ApiNextReleaseProblem {
    private PlanningSolution previousPlan;

    private Integer nbWeeks = null;

    private Double hoursPerWeek = null;

    private List<Feature> features = new ArrayList<>();

    private List<Employee> resources = new ArrayList<>();


    /* --- CONSTRUCTORS --- */

    public ApiNextReleaseProblem() {
        features = new ArrayList<>();
        resources = new ArrayList<>();
    }

    public ApiNextReleaseProblem(Integer nbWeeks, Double hoursPerWeek, List<Feature> features, List<Employee> resources) {
        this.nbWeeks = nbWeeks;
        this.hoursPerWeek = hoursPerWeek;
        this.features = features;
        this.resources = resources;
        previousPlan = null;
    }

    public ApiNextReleaseProblem(
            Integer nbWeeks, Double hoursPerWeek, List<Feature> features, List<Employee> resources,
            PlanningSolution previousPlan)
    {
        this(nbWeeks, hoursPerWeek, features, resources);
        this.previousPlan = previousPlan;
    }



    /* --- GETTERS / SETTERS --- */

    /*
    @ApiModelProperty(value = "")
    public PlanningSolution getPreviousPlan() {
        return previousPlan;
    }
    public void setPreviousPlan(PlanningSolution plan) {
        previousPlan = plan;
    }
    */

    @ApiModelProperty(value = "")
    public Integer getNbWeeks() { return nbWeeks; }

    public void setNbWeeks(Integer nbWeeks) { this.nbWeeks = nbWeeks; }

    public ApiNextReleaseProblem hoursPerWeek(Double hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
        return this;
    }

    @ApiModelProperty(value = "")
    public Double getHoursPerWeek() { return hoursPerWeek; }

    public void setHoursPerWeek(Double hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public ApiNextReleaseProblem addFeaturesItem(Feature featuresItem) {
        this.features.add(featuresItem);
        return this;
    }


    @ApiModelProperty(value = "")
    public List<Feature> getFeatures() { return features; }

    public void setFeatures(List<Feature> features) { this.features = features; }

    public ApiNextReleaseProblem addResourcesItem(Employee resourcesItem) {
        this.resources.add(resourcesItem);
        return this;
    }

    @ApiModelProperty(value = "")
    public List<Employee> getResources() { return resources; }

    public void setResources(List<Employee> resources) { this.resources = resources; }




    /* ----------------- */
/*
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiNextReleaseProblem nextReleaseProblem = (ApiNextReleaseProblem) o;
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
*/
    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
  /*  private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }*/
}
