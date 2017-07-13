package logic.analytics;

import entities.*;
import logic.NextReleaseProblem;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class containing some frequently needed methods involving a solution and/or a problem.
 */
public class Utils {

    private PlanningSolution solution;
    private NextReleaseProblem problem;

    /* --- CONSTRUCTORS --- */
    public Utils(PlanningSolution solution) {
        this.solution = solution;
        this.problem = solution.getProblem();
    }



    /* --- FEATURES --- */
    public List<Feature> unplannedDependenciesOf(Feature f) {
        List<Feature> features = new ArrayList<>();
        for (Feature prev : f.getPreviousFeatures())
            if (solution.findPlannedFeature(prev) == null)
                features.add(prev);

        return features;
    }

    public void computeHours(PlannedFeature pf) {
        double newBeginHour = pf.getBeginHour();
        Feature feature = pf.getFeature();
        // newBeginHour = maximum end hour of all previous features
        for (Feature previousFeature : feature.getPreviousFeatures()) {
            PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previousFeature);
            if (previousPlannedFeature != null) {
                newBeginHour = Math.max(newBeginHour, previousPlannedFeature.getEndHour());
            }
        }

        pf.setBeginHour(newBeginHour);
        pf.setEndHour(newBeginHour + feature.getDuration());
    }

    // Tells if all precedences of the feature are planned.
    public boolean allPrecedencesArePlanned(Feature f) {
        for (Feature prev : f.getPreviousFeatures())
            if (!solution.isAlreadyPlanned(prev)) return false;

        return true;
    }

    public List<Employee> doableBy(Feature f) {
        return problem.getEmployees().stream()
                .filter(e -> e.getSkills().containsAll(f.getRequiredSkills()))
                .collect(Collectors.toList());
    }


    /* --- EMPLOYEES --- */
    public List<Feature> doableFeatures(Employee e) {
        return problem.getFeatures().stream()
                .filter(f -> e.getSkills().containsAll(f.getRequiredSkills()))
                .collect(Collectors.toList());
    }

    public double startHour(Feature f) {
        PlannedFeature pf = solution.findPlannedFeature(f);
        return pf != null ? pf.getBeginHour() : -1.0;
    }

    public double endHour(Feature f) {
        PlannedFeature pf = solution.findPlannedFeature(f);
        return pf != null ? pf.getEndHour() : -1.0;
    }

    public double startHour(Employee e) {
        Schedule s = solution.getEmployeesPlanning().get(e);
        if (s != null && s.size() >= 1)
            return s.getWeek(0).getBeginHour();

        return -1.0;
    }

    public double endHour(Employee e) {
        Schedule s = solution.getEmployeesPlanning().get(e);
        if (s != null && s.size() >= 1)
            return s.getWeek(s.size() - 1).getEndHour();

        return -1.0;
    }

    // Assumes that the employee has enough time to do the feature and tells whether it would be possible to plan
    // the feature while respecting all its precedences, according to solution's schedule.
    public boolean couldRespectPrecedences(Employee e, Feature f) {
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

    // Tells if the employee had enough time to do this feature, according to solution's schedule.
    public boolean hadEnoughTime(Employee e, Feature f) {
        Schedule s = solution.getEmployeesPlanning().get(e);
        return s != null && s.getTotalHoursLeft() >= f.getDuration();
    }
}

