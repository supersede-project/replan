import entities.Employee;
import entities.Feature;
import entities.PriorityLevel;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import wrapper.SolverNRP;
import wrapper.parser.Transform2SwaggerModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kredes on 27/03/2017.
 */
public class SolverNRPTest {
    private static SolverNRP solver;


    /*   -----------------
        | UTILITY METHODS |
         -----------------
     */
    private Feature randomFeature() {
        return null;
    }


    @BeforeClass
    public static void setUpBeforeClass() {
        solver = new SolverNRP();
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
                new Employee("Test Employee", 40.0, employeeSkills)
        );

        PlanningSolution solution = solver.executeNRP(3, 40.0, features, employees);

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }



    @Test
    public void alreadyFinishedFeaturesAreNotPlanned() {
        Skill s = new Skill("Whatever skill");
        List<Skill> skills = new ArrayList<>();
        skills.add(s);

        LocalDateTime now = LocalDateTime.now();
        Feature f1 = new Feature("Feature 1", PriorityLevel.FIVE, 50.0, null, skills, now.minusDays(7));
        Feature f2 = new Feature("Feature 2", PriorityLevel.FIVE, 50.0, null, skills, now.plusDays(7));

        List<Employee> employees = Arrays.asList(
                new Employee("Test Employee", 40.0, skills)
        );

        List<Feature> features = new ArrayList<>();
        features.add(f1);
        features.add(f2);

        PlanningSolution solution = solver.executeNRP(3, 40.0, features, employees);

        Assert.assertTrue(solution.getPlannedFeatures().size() == 1);
        Assert.assertTrue(solution.getPlannedFeatures().get(0).getFeature().equals(f2));
    }

    /*
        Not an actual test, just checking out some things
     */
    @Test
    public void validPlan() {
        Skill s1 = new Skill("Skill 1");
        Skill s2 = new Skill("Skill 2");

        List<Skill> skills = new ArrayList<>();
        skills.add(s1); skills.add(s1);
        List<Feature> features = Arrays.asList(
                new Feature("Test Feature", PriorityLevel.FIVE, 100.0, null, skills)
        );

        List<Employee> employees = Arrays.asList(
                new Employee("Test Employee", 40.0, skills)
        );

        PlanningSolution solution = this.solver.executeNRP(3, 40.0, features, employees);

        Transform2SwaggerModel ts = new Transform2SwaggerModel();
        io.swagger.model.PlanningSolution s = ts.transformPlanningSolution2Swagger(solution);

        Assert.assertFalse(solution.getPlannedFeatures().isEmpty());
    }
}
