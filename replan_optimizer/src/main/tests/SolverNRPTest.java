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

    @BeforeClass
    public static void setUpBeforeClass() {
        solver = new SolverNRP();
    }

    /**
     *  - Situation: There is a Release with only one Feature that requires two Skills and
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
                new Employee("Test Employee", 40.0, employeeSkills)
        );


        PlanningSolution solution = this.solver.executeNRP(3, 40.0, features, employees);

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }
}
