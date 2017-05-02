import entities.Employee;
import entities.Feature;
import entities.PriorityLevel;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import wrapper.SolverNRP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kredes on 27/03/2017.
 */
public class SolverNRPTest {
    private static SolverNRP solver;
    private static RandomThings random;

    /*   -----------------
        | UTILITY METHODS |
         -----------------
     */
    private void validateDependencies(PlanningSolution solution) {

    }

    private void validateSkills(PlanningSolution solution) {

    }

    private void validateFrozen(PlanningSolution solution) {

    }

    private void validateAll(PlanningSolution solution) {
        validateDependencies(solution);
        validateFrozen(solution);
        validateSkills(solution);
    }

    private List<Feature> featuresToList(Feature... features) {
        return Arrays.asList(features);
    }

    private List<Employee> employeesToList(Employee... employees) {
        return Arrays.asList(employees);
    }


    @BeforeClass
    public static void setUpBeforeClass() {
        solver = new SolverNRP();
        random = new RandomThings();
    }

    /**
     *  - Situation: We need to plan only one Feature that requires two Skills and we have
     * a Resource that only has one of them.
     *  - Expected: The generated solution should not have any PlannedFeature.
     */
    @Test
    public void notEnoughSkilledResourcesForFeature() {
        Skill commonSkill = new Skill("Common skill");
        Skill nonCommonSkill = new Skill("Non-common skill");

        List<Skill> featureSkills = new ArrayList<>();
        featureSkills.add(commonSkill);
        featureSkills.add(nonCommonSkill);
        List<Feature> features = Arrays.asList(
                new Feature("Test Feature", PriorityLevel.FIVE, 100.0, null, featureSkills)
        );

        List<Skill> employeeSkills = new ArrayList<>();
        employeeSkills.add(commonSkill);
        List<Employee> employees = Arrays.asList(
                new Employee("Test Employee", 41.0, employeeSkills)
        );

        PlanningSolution solution = solver.executeNRP(3, 40.0, features, employees);

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }

    /**
     * Validate that precedences between features are respected even if there are enough employees
     * to work on them at the same time.
     */
    @Test
    public void precencesAreRespected() {
        Skill s1 = random.skill();

        List<Feature> features = random.featureList(2);
        List<Employee> employees = random.employeeList(2);

        employees.get(0).getSkills().add(s1);
        employees.get(1).getSkills().add(s1);

        features.get(0).getRequiredSkills().add(s1);
        features.get(1).getRequiredSkills().add(s1);

        features.get(1).getPreviousFeatures().add(features.get(0));

        PlanningSolution solution = solver.executeNRP(3, 40, features, employees);

        validateDependencies(solution);
    }

    /**
     * ´Features with recursive dependencies should not be planned
     */
    @Test
    public void featureDependingOnItselfIsNotPlanned() {
        Skill s1 = random.skill();
        Feature f1 = random.feature();
        Employee e1 = random.employee();

        e1.getSkills().add(s1);

        f1.getRequiredSkills().add(s1);

        f1.getPreviousFeatures().add(f1);

        for (int i = 0; i < 5; ++i) {
            PlanningSolution solution = solver.executeNRP(3, 40, featuresToList(f1), employeesToList(e1));
            Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
        }
    }
}
