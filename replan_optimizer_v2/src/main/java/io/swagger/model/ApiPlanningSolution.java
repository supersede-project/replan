package io.swagger.model;

import entities.PlannedFeature;

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

    public ApiPlanningSolution(List<PlannedFeature> jobs) {
        this.jobs = jobs;
    }



    /* --- GETTERS / SETTERS --- */

    public List<PlannedFeature> getJobs() {
        return jobs;
    }

    public void setJobs(List<PlannedFeature> jobs) {
        this.jobs = jobs;
    }
}
