package io.swagger.model;

import entities.Feature;
import entities.PlannedFeature;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience data class for returning data from the API call.
 * Don't try to use {@link PlanningSolution} as it extends a class implementing Serializable,
 * which makes it hard to serialize only the fields relevant to the API.
 *
 * @author kredes
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

    public PlannedFeature findJobOf(Feature f) {
        for (PlannedFeature pf : jobs)
            if (pf.getFeature().equals(f))
                return pf;

        return null;
    }
}
