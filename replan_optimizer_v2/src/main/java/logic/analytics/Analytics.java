package logic.analytics;

import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import logic.NextReleaseProblem;
import logic.PlanningSolution;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is constructed from a {@link PlanningSolution} and generates a series of information messages
 * with useful information. It currently generates messages for:
 * <ul>
 *     <li>Unplanned features due to missing dependencies</li>
 *     <li>Unplanned features due to the lack of a skilled employee</li>
 *     <li>Workload of the employees</li>
 *     <li>Very long releases</li>
 * </ul>
 *
 * Usage is very simple, you only need to call the constructor with a solution to automatically get it analyzed:
 * {@code Analytics a = new Analytics(solution);}
 *
 * @author kredes
 * @see EmployeeAnalytics
 * @see FeatureAnalytics
 */
public class Analytics {
    private NextReleaseProblem problem;
    private PlanningSolution solution;

    private List<Feature> features;
    private List<Employee> employees;
    private List<PlannedFeature> plannedFeatures;
    private List<Feature> doneFeatures = new ArrayList<>();
    private List<Feature> undoneFeatures;

    private Map<Employee, EmployeeAnalytics> employeesInfo = new HashMap<>();
    private Map<Feature, FeatureAnalytics> featuresInfo = new HashMap<>();

    private List<String> info = new ArrayList<>();

    private Utils utils;


    /* --- CONSTRUCTORS --- */
    public Analytics(PlanningSolution solution) {
        problem = solution.getProblem();
        this.solution = solution;

        features = problem.getFeatures();
        employees = problem.getEmployees();
        plannedFeatures = solution.getPlannedFeatures();
        for (PlannedFeature pf : plannedFeatures)
            doneFeatures.add(pf.getFeature());
        undoneFeatures = solution.getUndoneFeatures();

        utils = new Utils(solution);

        initializeInfo();

        evaluateFeatures();
        evaluateQuality();
    }

    private void initializeInfo() {
        for (Feature f : features)
            featuresInfo.put(f, new FeatureAnalytics(f, solution));
        for (Employee e : employees)
            employeesInfo.put(e, new EmployeeAnalytics(e, solution));
    }


    /* --- PRIVATE --- */
    private void evaluateFeatures() {
        String depMessage = "Feature '%s' couldn't be planned because dependencie(s) [%s] haven't been planned";
        String skillMessage = "There isn't any employee with the required skills to do Feature '%s'";
        String doableMessage = "Feature '%s' could have been done by employee(s) [%s], but wasn't planned";
        for (Feature f : undoneFeatures) {
            if (!utils.allPrecedencesArePlanned(f)) {
                String unplannedDependencies = utils.unplannedDependenciesOf(f).stream()
                        .map(Feature::getName).collect(Collectors.joining(", "));
                if (!unplannedDependencies.equals(""))
                    info.add(String.format(Locale.ENGLISH, depMessage, f.getName(), unplannedDependencies));
            } else {
                String doableBy = featuresInfo.get(f).doableBy.stream()
                        .filter(e -> utils.hadEnoughTime(e, f))
                        .filter(e -> utils.couldRespectPrecedences(e, f))
                        .map(Employee::getName)
                        .collect(Collectors.joining(", "));

                if (doableBy != null)
                    info.add(String.format(doableMessage, f.getName(), doableBy));
            }

            if (problem.getSkilledEmployees(f.getRequiredSkills()).isEmpty())
                info.add(String.format(skillMessage, f.getName()));
        }
    }

    private void evaluateQuality() {
        String message = "Employee %s (total availability: %.2f hours) is working at %.2f%% of its capacity. " +
                "Could have potentially done features for a total of %.2f hours. Did %.2f hours.";
        for (Map.Entry<Employee, EmployeeAnalytics> entry : employeesInfo.entrySet()) {
            EmployeeAnalytics ea = entry.getValue();
            info.add(String.format(message,
                    ea.employee.getName(), ea.totalAvailability, ea.workload, ea.doableFeatureHours, ea.doneHours));
        }

        message = "The employees total available hours (%.2f) are far greater than the sum of all features durations " +
                "(%.2f hours). Consider either reducing the number of weeks or adding more features.";
        double totalEmployeesAvailableHours = employees.stream()
                .mapToDouble(e -> e.getWeekAvailability() * problem.getNbWeeks()).sum();
        double featureHours = features.stream().mapToDouble(Feature::getDuration).sum();

        if (totalEmployeesAvailableHours - featureHours >= 240.0)
            info.add(String.format(message, totalEmployeesAvailableHours, featureHours));
    }

    @Override
    public String toString() {
        return info.size() + " messages";
    }
}
