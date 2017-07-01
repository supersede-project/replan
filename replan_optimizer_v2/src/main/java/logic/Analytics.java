package logic;

import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import entities.Schedule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kredes on 26/06/2017.
 */
public class Analytics {

    private NextReleaseProblem problem;
    private PlanningSolution solution;

    private List<Feature> features;
    private List<Employee> employees;
    private List<PlannedFeature> plannedFeatures;
    private List<Feature> doneFeatures;
    private List<Feature> undoneFeatures;

    private List<String> info;

    public Analytics(PlanningSolution solution) {
        info = new ArrayList<>();

        problem = solution.getProblem();
        this.solution = solution;

        features = problem.getFeatures();
        employees = problem.getEmployees();
        plannedFeatures = solution.getPlannedFeatures();
        doneFeatures = new ArrayList<>();
        for (PlannedFeature pf : plannedFeatures)
            doneFeatures.add(pf.getFeature());
        undoneFeatures = solution.getUndoneFeatures();
        evaluateQuality();
        evaluateFeatures();
    }

    private void evaluateFeatures() {
        Map<Feature, List<Employee>> doableBy = new HashMap<>();
        for (Feature f : features) {
            List<Employee> skilled =  employees.stream()
                    .filter(e -> e.getSkills().containsAll(f.getRequiredSkills()))
                    .collect(Collectors.toList());
            doableBy.put(f, skilled);
        }

        String message = "Feature %s was not planned but could have been done by employee(s): %s";
        String depMessage = "Feature %s couldn't be planned because dependencie(s) %s haven't been planned";
        String skillMessage = "The isn't any employee with the required skills to do Feature %s";
        for (Feature f : undoneFeatures) {
            if (allPrecedencesArePlanned(f)) {
                String employees = doableBy.get(f).stream()
                        .filter(e -> hadEnoughTime(e, f))
                        .filter(e -> couldRespectPrecedences(e, f))
                        .map(Employee::getName)
                        .collect(Collectors.joining(", "));

                if (!employees.equals(""))
                    info.add(String.format(Locale.ENGLISH, message, f.getName(), employees));
            } else {
                String features = unplannedDependenciesOf(f).stream()
                        .map(Feature::getName)
                        .collect(Collectors.joining(", "));
                if (!features.equals(""))
                    info.add(String.format(Locale.ENGLISH, depMessage, f.getName(), features));
            }

            if (problem.getSkilledEmployees(f.getRequiredSkills()).isEmpty())
                info.add(String.format(skillMessage, f.getName()));
        }

        for (Feature f : features) {

        }
    }

    private void evaluateQuality() {
        Map<Employee, Double> doableFeatureTime = new HashMap<>();
        for (Employee e : employees) {
            Double doable = doneFeatures.stream()
                    .filter(f -> e.getSkills().containsAll(f.getRequiredSkills()))
                    .filter(f -> e.getWeekAvailability() * problem.getNbWeeks() >= f.getDuration())
                    .mapToDouble(Feature::getDuration)
                    .sum();
            doableFeatureTime.put(e, doable);
        }

        String message = "Employee %s (total availability: %.2f hours) is working at %.2f%% of its capacity. " +
                "Could have potentially done features for a total of %.2f hours. Did %.2f hours.";
        for (Map.Entry<Employee, Double> entry : doableFeatureTime.entrySet()) {
            Employee e = entry.getKey();
            double availability = e.getWeekAvailability() * problem.getNbWeeks();
            double doableHours = entry.getValue();

            Schedule s = solution.getEmployeesPlanning().get(e);
            double doneHours = s == null ? 0.0 : s.getPlannedFeatures().stream()
                    .map(PlannedFeature::getFeature).mapToDouble(Feature::getDuration).sum();

            info.add(String.format(message,
                    e.getName(), availability, (doneHours / availability) * 100, doableHours, doneHours));
        }

        message = "The employees total available hours (%.2f) are far greater than the sum of all features durations " +
                "(%.2f hours). Consider either reducing the number of weeks or adding more features.";
        double totalEmployeesAvailableHours = employees.stream()
                .mapToDouble(e -> e.getWeekAvailability() * problem.getNbWeeks()).sum();
        double featureHours = features.stream().mapToDouble(Feature::getDuration).sum();

        if (totalEmployeesAvailableHours - featureHours >= 120.0)
            info.add(String.format(message, totalEmployeesAvailableHours, featureHours));
    }

    private List<Feature> unplannedDependenciesOf(Feature f) {
        List<Feature> features = new ArrayList<>();
        for (Feature prev : f.getPreviousFeatures())
            if (solution.findPlannedFeature(prev) == null)
                features.add(prev);

        return features;
    }

    // Tells if the employee had enough time to do this feature, according to solution's schedule.
    private boolean hadEnoughTime(Employee e, Feature f) {
        Schedule s = solution.getEmployeesPlanning().get(e);
        return s != null && s.getTotalHoursLeft() >= f.getDuration();
    }

    // Assumes that the employee has enough time to do the feature and tells whether it would be possible to plan
    // the feature while respecting all its precedences, according to solution's schedule.
    private boolean couldRespectPrecedences(Employee e, Feature f) {
        // Get the end hour of the nearest precedence
        double endHour = 0.0;
        for (Feature dep : f.getPreviousFeatures()) {
            PlannedFeature pf = solution.findPlannedFeature(dep);
            if (pf != null) endHour = Math.max(endHour, pf.getEndHour());
        }

        double employeeEndHour = e.getWeekAvailability() * problem.getNbWeeks();
        Schedule s = solution.getEmployeesPlanning().get(e);
        return employeeEndHour - endHour >= f.getDuration();
    }

    // Tells if all precedences of the feature are planned.
    private boolean allPrecedencesArePlanned(Feature f) {
        for (Feature prev : f.getPreviousFeatures())
            if (!solution.isAlreadyPlanned(prev)) return false;

        return true;
    }

    @Override
    public String toString() {
        return info.size() + " issues";
    }
}
