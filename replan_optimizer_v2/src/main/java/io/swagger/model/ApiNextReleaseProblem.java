package io.swagger.model;

import com.google.gson.JsonSyntaxException;
import entities.Employee;
import entities.Feature;
import entities.parameters.AlgorithmParameters;
import io.swagger.api.ReplanGson;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Convenience data class for receiving data from the API call.
 * Don't try to use {@link logic.NextReleaseProblem} as it extends a class implementing Serializable,
 * which makes it hard to serialize only the fields relevant to the API.
 *
 * @author kredes
 */
public class ApiNextReleaseProblem {

    private static final String DATASET_PATH = "src/test/datasets";

    private ApiPlanningSolution previousSolution = null;

    private int nbWeeks;

    private double hoursPerWeek;

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
    }

    public ApiNextReleaseProblem(
            Integer nbWeeks, Double hoursPerWeek, List<Feature> features, List<Employee> resources,
            ApiPlanningSolution previousSolution)
    {
        this(nbWeeks, hoursPerWeek, features, resources);
        this.previousSolution = previousSolution;
    }

    /**
     * Constructs a problem from a file containing a JSON representation of a NextReleaseProblem similar to that
     * passed to the API replan call. If only a filename is provided as path, it searches the default dataset
     * location: '/src/test/datasets'.
     * Returns null if the file doesn't exist or the file data is invalid.
     */
    public static ApiNextReleaseProblem fromFile(String location) {
        if (location.split("/").length == 1)    // Filename only
            location = String.format("%s/%s", DATASET_PATH, location);

        location = location.endsWith(".txt") ? location : location + ".txt";

        try {
            String data = new String(Files.readAllBytes(Paths.get(location)));
            return ReplanGson.getGson().fromJson(data, ApiNextReleaseProblem.class);
        }
        catch (IOException | JsonSyntaxException e) {
            return null;
        }
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
    public int getNbWeeks() { return nbWeeks; }

    public void setNbWeeks(int nbWeeks) { this.nbWeeks = nbWeeks; }

    @ApiModelProperty(value = "")
    public double getHoursPerWeek() { return hoursPerWeek; }

    public void setHoursPerWeek(double hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    @ApiModelProperty(value = "")
    public List<Feature> getFeatures() { return features; }

    public void setFeatures(List<Feature> features) { this.features = features; }


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
