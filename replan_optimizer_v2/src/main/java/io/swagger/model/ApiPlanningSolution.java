package io.swagger.model;

import entities.PlannedFeature;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kredes on 06/06/2017.
 */
public class ApiPlanningSolution {

    private List<PlannedFeature> jobs;




    /* --- CONSTRUCTORS --- */
    public ApiPlanningSolution() {
        jobs = new ArrayList<>();
    }

    public ApiPlanningSolution(PlanningSolution solution) {
        jobs = solution.getPlannedFeatures();
    }



    /* --- GETTERS / SETTERS --- */

    public List<PlannedFeature> getJobs() {
        return jobs;
    }

    public void setJobs(List<PlannedFeature> jobs) {
        this.jobs = jobs;
    }
}
