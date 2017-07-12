package io.swagger.model;

import entities.Employee;
import entities.Feature;
import entities.parameters.AlgorithmParameters;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Convenience data class for receiving data from the API call.
 * Just because the one in model is pretty crowded and complex.
 */
public class ApiNextReleaseProblem {
    private ApiPlanningSolution previousSolution = null;

    private Integer nbWeeks = null;

    private Double hoursPerWeek = null;

    private List<Feature> features = new ArrayList<>();

    private List<Employee> resources = new ArrayList<>();

    private AlgorithmParameters algorithmParameters = null;


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
        previousSolution = null;
    }

    public ApiNextReleaseProblem(
            Integer nbWeeks, Double hoursPerWeek, List<Feature> features, List<Employee> resources,
            ApiPlanningSolution previousSolution)
    {
        this(nbWeeks, hoursPerWeek, features, resources);
        this.previousSolution = previousSolution;
    }



    /* --- GETTERS / SETTERS --- */


    @ApiModelProperty(value = "")
    public ApiPlanningSolution getPreviousSolution() {
        return previousSolution;
    }
    public void setPreviousSolution(ApiPlanningSolution plan) {
        previousSolution = plan;
    }

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

    @ApiModelProperty(value="")
    public AlgorithmParameters getAlgorithmParameters() { return algorithmParameters; }

    public void setAlgorithmParameters(AlgorithmParameters algorithmParameters) { this.algorithmParameters = algorithmParameters; }



    /* ----------------- */

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
        String f = features.stream().map(Feature::getName).collect(Collectors.joining(", "));
        String r = resources.stream().map(Employee::getName).collect(Collectors.joining(", "));
        return String.format(
                Locale.ENGLISH, "nbWeeks: %d, hoursPerWeek: %.2f, features: %s, resources: %s",
                nbWeeks, hoursPerWeek, f, r);
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
