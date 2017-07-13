package logic.analytics;

import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import entities.Schedule;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supporting class for {@link Analytics}. It generates some useful data from a given employee and solution.
 * Yes, all fields are public to keep its {@link Analytics} more readable and I am ashamed because of  it.
 */
public class EmployeeAnalytics {
    public Employee employee;

    public double workload, doableFeatureHours, doneHours, totalAvailability;
    public double startHour, endHour;
    public List<Feature> doableFeatures, doneFeatures;

    private Utils utils;

    /* --- CONSTRUCTORS --- */
    public EmployeeAnalytics(Employee employee, PlanningSolution solution) {
        utils = new Utils(solution);

        this.employee = employee;
        doableFeatures = utils.doableFeatures(employee);
        startHour = utils.startHour(employee);
        endHour = utils.endHour(employee);
        totalAvailability = employee.getWeekAvailability() * solution.getProblem().getNbWeeks();

        Schedule s = solution.getEmployeesPlanning().get(employee);
        doneFeatures = s == null ? new ArrayList<>() :
                s.getPlannedFeatures().stream().map(PlannedFeature::getFeature).collect(Collectors.toList());;
        doneHours = s == null ? 0.0 : s.getPlannedFeatures().stream()
                .map(PlannedFeature::getFeature).mapToDouble(Feature::getDuration).sum();
        doableFeatureHours = doableFeatures.stream().mapToDouble(Feature::getDuration).sum();
        workload = (doneHours / totalAvailability) * 100;
    }

    public double getWorkload() { return  workload; }
}
