package logic.analytics;

import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import logic.PlanningSolution;

import java.util.List;

/**
 * Created by kredes on 03/07/2017.
 */
public class FeatureAnalytics {
    private Feature feature;

    public double startHour, endHour;
    public List<Employee> doableBy;

    private Utils utils;


    public FeatureAnalytics(Feature feature, PlanningSolution solution) {
        utils = new Utils(solution);

        this.feature = feature;

        doableBy = utils.doableBy(feature);
        startHour = utils.startHour(feature);
        endHour = utils.endHour(feature);
    }

    public FeatureAnalytics(PlannedFeature plannedFeature, PlanningSolution solution) {
        this(plannedFeature.getFeature(), solution);

        startHour = plannedFeature.getBeginHour();
        endHour = plannedFeature.getEndHour();
    }
}
