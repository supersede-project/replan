import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.Assert;

import java.util.*;

import static junit.framework.Assert.assertTrue;

/**
 * Created by kredes on 02/05/2017.
 */
public class Validator {

    private static final String DEPENDENCE_FAIL_MESSAGE =
            "Feature %s depends on feature %s, but planning does not respect this precedence.\n" +
                    " Dependence -> beginHour: %f, endHour: %f.\n Dependant -> beginHour: %f, endHour: %f";

    private static final String SKILL_FAIL_MESSAGE =
            "Feature %s requires skill %s, but employee %s does not have it.";

    private static final String FROZEN_FAIL_MESSAGE =
            "PlannedFeature %s is frozen in the previous plan, but this is not respected in the new plan.";

    private static final String OVERLAPPING_FAIL_MESSAGE =
            "PlannedFeature %s overlaps with PlannedFeature %s";

    private static final String DEADLOCK_MESSAGE =
            "Feature %s depends on itself at some point.";


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
        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        Map<Employee, List<PlannedFeature>> schedule = new HashMap<>();

        for (PlannedFeature pf : jobs) {
            Employee e = pf.getEmployee();
            if (!schedule.containsKey(e)) schedule.put(e, new ArrayList<>());
            schedule.get(e).add(pf);
        }

        for (Map.Entry<Employee, List<PlannedFeature>> entry : schedule.entrySet()) {
            double endHour = 0.0;
            List<PlannedFeature> employeeJobs = entry.getValue();
            PlannedFeature previous = employeeJobs.get(0);

            sortJobsByBeginHour(employeeJobs);

            for (PlannedFeature pf : employeeJobs) {
                Assert.assertTrue(
                        String.format(OVERLAPPING_FAIL_MESSAGE, pf, previous),
                        pf.getBeginHour() >= endHour
                );

                endHour = pf.getEndHour();
                previous = pf;
            }
        }
    }

    private void sortJobsByBeginHour(List<PlannedFeature> jobs) {
        Collections.sort(jobs, new Comparator<PlannedFeature>() {
            @Override
            public int compare(PlannedFeature o1, PlannedFeature o2) {
                double h1 = o1.getBeginHour();
                double h2 = o2.getBeginHour();

                if (h1 < h2) return -1;
                else if (h1 > h2) return 1;
                else return 0;
            }
        });
    }
}
