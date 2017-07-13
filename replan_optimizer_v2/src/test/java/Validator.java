import entities.*;
import logic.PlanningSolution;
import org.junit.Assert;

import java.util.*;

import static junit.framework.Assert.assertTrue;

/**
 * A class that provides helpful methods to validate the correctness of solutions and entities.
 * @author kredes
 */
public class Validator {

    private static final String DEPENDENCE_FAIL_MESSAGE =
            "Feature %s depends on feature %s, but planning does not respect this precedence.\n" +
                    " Dependent -> beginHour: %f, endHour: %f.\n Dependency -> beginHour: %f, endHour: %f";

    private static final String SKILL_FAIL_MESSAGE =
            "Feature %s requires skill %s, but employee %s does not have it.";

    private static final String FROZEN_FAIL_MESSAGE =
            "PlannedFeature %s is frozen in the previous plan, but this is not respected in the new plan.";

    private static final String OVERLAPPING_FAIL_MESSAGE =
            "PlannedFeature %s overlaps with PlannedFeature %s";

    private static final String DEADLOCK_MESSAGE =
            "Feature %s depends on itself at some point.";


    /* --- PUBLIC --- */
    public void validateDependencies(PlanningSolution solution) {
        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        Map<Feature, PlannedFeature> dependences = new HashMap<>();

        for (PlannedFeature pf : jobs) {
            dependences.put(pf.getFeature(), pf);
        }

        for (PlannedFeature pf : jobs) {
            Feature f = pf.getFeature();
            for (Feature d : f.getPreviousFeatures()) {
                PlannedFeature depPf = dependences.get(d);
                assertTrue(String.format(DEPENDENCE_FAIL_MESSAGE,
                        f.toString(), d.toString(), pf.getBeginHour(), pf.getEndHour(), depPf.getBeginHour(), depPf.getEndHour()),
                        pf.getBeginHour() >= depPf.getEndHour());
            }
        }
    }

    public void validateSkills(PlanningSolution solution) {
        for (PlannedFeature pf : solution.getPlannedFeatures()) {
            Employee e = pf.getEmployee();
            Feature f = pf.getFeature();

            for (Skill s : f.getRequiredSkills()) {
                assertTrue(String.format(SKILL_FAIL_MESSAGE, f.toString(), s.toString(), e.toString()),
                        e.getSkills().contains(s));
            }
        }
    }

    public void validateFrozen(PlanningSolution previous, PlanningSolution current) {
        for (PlannedFeature pf : previous.getPlannedFeatures()) {
            if (pf.isFrozen()) {
                assertTrue(String.format(FROZEN_FAIL_MESSAGE, pf.toString()), current.getPlannedFeatures().contains(pf));
            }
        }
    }

    public void validateAll(PlanningSolution solution) {
        validateDependencies(solution);
        validateNoOverlappedJobs(solution);
        validateSkills(solution);
    }

    public void validateAll(PlanningSolution previous, PlanningSolution current) {
        validateDependencies(current);
        validateFrozen(previous, current);
        validateNoOverlappedJobs(current);
        validateSkills(current);
    }

    public void validateNoUnassignedSkills(List<Skill> skills, List<Employee> employees) {
        Set<Skill> assignedSkills = new HashSet<>();
        for (Employee e : employees)
            assignedSkills.addAll(e.getSkills());

        for (Skill s : skills)
            Assert.assertTrue(assignedSkills.contains(s));
    }

    public void validateNoDependencyDeadlocks(List<Feature> features) {
        for (Feature f : features) {
            validateNoDependencyDeadlock(f, f);
        }
    }

    public void validateNoDependencyDeadlock(Feature original, Feature recursive) {
        if (!recursive.getPreviousFeatures().isEmpty()) {
            Assert.assertFalse(recursive.dependsOn(original));

            for (Feature previous : recursive.getPreviousFeatures()) {
                validateNoDependencyDeadlock(original, previous);
            }
        }
    }

    public void validateNoOverlappedJobs(PlanningSolution solution) {
        Map<Employee, Schedule> schedule = solution.getEmployeesPlanning();
        for (Map.Entry<Employee, Schedule> entry : schedule.entrySet()) {
            for (WeekSchedule week : entry.getValue()) {
                double endHour = 0.0;
                List<PlannedFeature> employeeJobs = week.getPlannedFeatures();
                PlannedFeature previous = employeeJobs.isEmpty() ? null : employeeJobs.get(0);

                for (PlannedFeature pf : employeeJobs) {
                    if (pf != previous) {
                        Assert.assertTrue(
                                String.format(OVERLAPPING_FAIL_MESSAGE, pf, previous),
                                pf.getBeginHour() >= endHour
                        );
                    }
                    endHour = pf.getEndHour();
                    previous = pf;
                }
            }
        }
    }
}
