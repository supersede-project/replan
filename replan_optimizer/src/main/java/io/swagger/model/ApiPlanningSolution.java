package io.swagger.model;

import entities.PlannedFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kredes on 06/06/2017.
 */
public class ApiPlanningSolution {

    private List<PlannedFeature> plannedFeatures;

    private int numberOfFeatures;


    /* --- CONSTRUCTORS --- */
    public ApiPlanningSolution() {
        plannedFeatures = new ArrayList<>();
    }

    public ApiPlanningSolution(List<PlannedFeature> plannedFeatures) {
        this.plannedFeatures = plannedFeatures;
        this.numberOfFeatures = plannedFeatures.size();
    }



    /* --- GETTERS / SETTERS --- */

    public List<PlannedFeature> getPlannedFeatures() {
        return plannedFeatures;
    }

    public void setPlannedFeatures(List<PlannedFeature> plannedFeatures) {
        this.plannedFeatures = plannedFeatures;
    }

    public int getNumberOfFeatures() {
        return numberOfFeatures;
    }

    public void setNumberOfFeatures(int numberOfFeatures) {
        this.numberOfFeatures = numberOfFeatures;
    }
}
