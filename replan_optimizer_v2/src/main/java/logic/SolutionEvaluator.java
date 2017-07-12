package logic;

import entities.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kredes on 22/06/2017.
 */
public class SolutionEvaluator {

    private static SolutionEvaluator instance = new SolutionEvaluator();

    public static SolutionEvaluator getInstance() {
        return instance;
    }

    private SolutionEvaluator() {}


    /* --- OBJECTIVES --- */
    public double priorityObjective(PlanningSolution solution) {
        return solution.getPriorityScore();
    }

    public double endDateObjective(PlanningSolution solution) {
        NextReleaseProblem problem = solution.getProblem();
        double worstEndDate = problem.getNbWeeks() * problem.getNbHoursByWeek();

        return solution.getPlannedFeatures().isEmpty() ? worstEndDate : solution.getEndDate();
    }

    public double distributionObjective(PlanningSolution solution) {
        Map<Employee, Double> hoursPerEmployee = new HashMap<>();
        double totalHours = 0.0;
        for (Map.Entry<Employee, Schedule> entry : solution.getEmployeesPlanning().entrySet()) {
            Employee employee = entry.getKey();
            hoursPerEmployee.put(employee, 0.0);
            for (WeekSchedule week : entry.getValue()) {
                double aux = hoursPerEmployee.get(employee);
                for (PlannedFeature pf : week.getPlannedFeatures()) {
                    totalHours += pf.getFeature().getDuration();
                    aux += pf.getFeature().getDuration();
                }
                hoursPerEmployee.put(employee, aux);
            }
        }

        int nbEmployees = hoursPerEmployee.size();
        double expectedAvg = totalHours/nbEmployees;
        double result = 0.0;
        for (Double nbHours : hoursPerEmployee.values()) {
            double rating;
            if (nbHours <= expectedAvg)
                rating = nbHours * (1/expectedAvg);
            else
                rating = Math.max(0.0, 1.0 - (nbHours - expectedAvg) * (1/expectedAvg));

            result += rating/nbEmployees;
        }

        return 1.0 - result;    // Because the algorithm minimizes objectives
    }


    /* --- QUALITY --- */
    public double quality(PlanningSolution solution) {
        NextReleaseProblem problem = solution.getProblem();
        double worstEndDate = problem.getNbWeeks() * problem.getNbHoursByWeek();

        double unplannedFeatures = solution.getUndoneFeatures().size();
        double totalFeatures = problem.getFeatures().size();
        double penalty = worstEndDate/totalFeatures;

        double endDateQuality = Math.max(0.0, 1.0 - (penalty * unplannedFeatures) / worstEndDate);
        double priorityQuality = 1.0 - priorityObjective(solution) / worstScore(problem);
        double distributionQuality = 1.0 - distributionObjective(solution);

        return (endDateQuality*0.3 + priorityQuality*0.4 + distributionQuality*0.3);
    }


    /* --- PRIVATE AUX --- */
    private double worstScore(NextReleaseProblem problem) {
        return problem.getFeatures().stream()
                .map(Feature::getPriority)
                .reduce(0.0, (sum, next) -> sum += next.getScore(), Double::sum);
    }
}
