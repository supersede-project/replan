package io.swagger.model;

import entities.PlannedFeature;
import logic.analytics.Analytics;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kredes on 06/06/2017.
 */
public class ApiPlanningSolution {

    private List<PlannedFeature> jobs;

    private Analytics analytics;



    /* --- CONSTRUCTORS --- */
    public ApiPlanningSolution() {
        jobs = new ArrayList<>();
    }

    public ApiPlanningSolution(PlanningSolution solution) {
        jobs = solution.getPlannedFeatures();
        analytics = new Analytics(solution);
    }



    /* --- GETTERS / SETTERS --- */

    public List<PlannedFeature> getJobs() {
        return jobs;
    }

    public void setJobs(List<PlannedFeature> jobs) {
        this.jobs = jobs;
    }

    public Analytics getAnalytics() { return analytics; }

    public void setAnalytics(Analytics analytics) { this.analytics = analytics; }
}
