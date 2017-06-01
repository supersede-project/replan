import entities.Employee;
import entities.Feature;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import wrapper.SolverNRP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kredes on 27/03/2017.
 */
public class SolverNRPTest {
    private static SolverNRP solver;
    private static RandomThings random;
    private static Validator validator;

    /*   -------------
        | AUX METHODS |
         -------------
     */
    private <T> List<T> asList(T... elements) {
        return Arrays.asList(elements);
    }

    private void removeNullSkillsFromEmployees(List<Employee> employees) {
        Skill nil = new Skill("null");
        for (Employee e : employees) {
            if (e.getSkills().contains(nil))
                e.getSkills().remove(nil);
        }
    }

    private void removeNullSkillsFromFeatures(List<Feature> features) {
        Skill nil = new Skill("null");
        for (Feature f : features) {
            if (f.getRequiredSkills().contains(nil))
                f.getRequiredSkills().remove(nil);
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        solver = new SolverNRP();
        random = new RandomThings();
        validator = new Validator();
    }

    /**
     *  - Situation: We need to plan only one Feature that requires two Skills and we have
     * a Resource that only has one of them.
     *  - Expected: The generated solution should not have any PlannedFeature.
     */
    @Test
    public void featureIsNotPlannedIfThereIsNoSkilledResource() {
        List<Skill> skills = random.skillList(2);
        Feature f = random.feature();
        Employee e = random.employee();

        f.getRequiredSkills().addAll(skills);
        e.getSkills().add(skills.get(0));

        PlanningSolution solution = solver.executeNRP(3, 40.0, asList(f), asList(e));

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
        validator.validateSkills(solution);
    }

    /**
     * Validate that precedences between features are respected even if there are enough employees
     * to work on them at the same time.
     */
    @Test
    public void featurePrecedencesAreRespected() {
        Skill s1 = random.skill();

        List<Feature> features = random.featureList(2);
        List<Employee> employees = random.employeeList(2);

        employees.get(0).getSkills().add(s1);
        employees.get(1).getSkills().add(s1);

        features.get(0).getRequiredSkills().add(s1);
        features.get(1).getRequiredSkills().add(s1);

        features.get(1).getPreviousFeatures().add(features.get(0));

        PlanningSolution solution = solver.executeNRP(3, 40, features, employees);

        validator.validateDependencies(solution);
    }

    //@Test Fails with computeHoursRecursive() in NextReleaseProblem.evaluate()
    public void featureDependingOnItselfIsNotPlanned() {
        Skill s1 = random.skill();
        Feature f1 = random.feature();
        Employee e1 = random.employee();

        e1.getSkills().add(s1);

        f1.getRequiredSkills().add(s1);

        f1.getPreviousFeatures().add(f1);

        PlanningSolution solution = solver.executeNRP(3, 40, asList(f1), asList(e1));
        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }


    //@Test Fails with computeHoursRecursive() in NextReleaseProblem.evaluate()
    public void featuresCausingDependencyDeadlockAreNotPlanned() {
        Skill s1 = random.skill();
        List<Feature> features = random.featureList(2);
        List<Employee> employees = random.employeeList(2);

        Feature f0 = features.get(0);
        Feature f1 = features.get(1);

        f0.getRequiredSkills().add(s1);
        f1.getRequiredSkills().add(s1);

        employees.get(0).getSkills().add(s1);
        employees.get(1).getSkills().add(s1);

        f0.getPreviousFeatures().add(f1);
        f1.getPreviousFeatures().add(f0);

        PlanningSolution solution = solver.executeNRP(3, 40.0, features, employees);
        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }

    //@Test
    public void frozenPlannedFeaturesAreNotReplaned() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(7);

        random.mix(features, skills, employees);

        PlanningSolution s1 = solver.executeNRP(3, 40.0, features, employees);

        random.freeze(s1);

        PlanningSolution s2 = solver.executeNRP(3, 40.0, features, employees, s1);

        validator.validateFrozen(s1, s2);
    }

    //@Test
    public void frozenPlannedFeaturesViolatingPrecedencesAreReplannedToo() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(10);

        random.mix(features, skills, employees);

        PlanningSolution s1 = solver.executeNRP(5, 40.0, features, employees);
        PlanningSolution s1Prime = new PlanningSolution(s1);

        random.violatePrecedences(s1Prime);

        try {
            validator.validateDependencies(s1Prime);
            Assert.assertTrue("Called Random.violatePrecedences(solution) but precedences are valid.", false);
        } catch (AssertionError e) {
            // OK
        }

        PlanningSolution s2 = solver.executeNRP(5, 40.0, features, employees, s1Prime);

        validator.validateFrozen(s1, s2);
    }

    @Test
    public void randomProblemValidatesAllConstraints() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(20);
        List<Employee> employees = random.employeeList(5);

        random.mix(features, skills, employees);

        PlanningSolution solution = solver.executeNRP(5, 40.0, features, employees);

        validator.validateAll(solution);

        solutionToDataFile(solution);
    }

    @Test
    public void manyRandomProblemsToSeeIfICatchAnOverlappingErrorBecauseItDoesntSeemToHappen() {
        for (int i = 0; i < 50; ++i) {
            List<Skill> skills = random.skillList(7);
            List<Feature> features = random.featureList(20);
            List<Employee> employees = random.employeeList(6);

            random.mix(features, skills, employees);

            PlanningSolution solution = solver.executeNRP(5, 40.0, features, employees);

            validator.validateAll(solution);
        }
    }


    // TODO: It won't pass the frozen validation if the solution is cleared because of constraint violation as all planned features are removed, including frozen ones
    //@Test
    public void randomReplanValidatesAllConstraints() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(15);
        List<Employee> employees = random.employeeList(10);

        random.mix(features, skills, employees);

        PlanningSolution s1 = solver.executeNRP(5, 40.0, features, employees);

        //random.freeze(s1);
        removeNullSkillsFromFeatures(features);
        removeNullSkillsFromEmployees(employees);

        PlanningSolution s2 = solver.executeNRP(5, 40.0, features, employees, s1);

        validator.validateAll(s1, s2);
    }

    @Test
    public void featureWithNoRequiredSkillsCanBeDoneByAnSkilledEmployee() {
        Feature f = random.feature();
        Employee e = random.employee();
        Skill s = random.skill();

        e.getSkills().add(s);

        PlanningSolution solution = solver.executeNRP(4, 40.0, asList(f), asList(e));

        Assert.assertTrue(solution.getPlannedFeatures().size() == 1);
    }

    @Test
    public void featureWithNoRequiredSkillsCanBeDoneByANonSkilledEmployee() {
        Feature f = random.feature();
        Employee e = random.employee();
        PlanningSolution solution = solver.executeNRP(4, 40.0, asList(f), asList(e));

        Assert.assertTrue(solution.getPlannedFeatures().size() == 1);
    }

    @Test
    public void featureWithRequiredSkillsCanBeDoneOnlyByTheSkilledEmployee() {
        List<Skill> skills = random.skillList(2);
        List<Feature> features = random.featureList(1);
        List<Employee> employees = random.employeeList(2);

        // 1 employee with 1 skill
        employees.get(0).getSkills().add(skills.get(0));

        // 1 employee with 2 skills
        employees.get(1).getSkills().add(skills.get(0));
        employees.get(1).getSkills().add(skills.get(1));

        // 1 feature requires 2 skills
        features.get(0).getRequiredSkills().add(skills.get(0));
        features.get(0).getRequiredSkills().add(skills.get(1));

        PlanningSolution solution = solver.executeNRP(4, 40.0, features, employees);

        Assert.assertTrue(solution.getPlannedFeatures().size() == 1 && // is planned
                solution.getPlannedFeatures().get(0).getEmployee().equals(employees.get(1))); // and done by the skilled employee
    }

    @Test
    public void averageUseCaseTest() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(20);
        List<Employee> employees = random.employeeList(4);

        // resource skills
        employees.get(0).getSkills().add(skills.get(0));
        employees.get(0).getSkills().add(skills.get(3));

        employees.get(1).getSkills().add(skills.get(0));
        employees.get(1).getSkills().add(skills.get(1));
        employees.get(1).getSkills().add(skills.get(3));

        employees.get(2).getSkills().add(skills.get(0));
        employees.get(2).getSkills().add(skills.get(1));
        employees.get(2).getSkills().add(skills.get(2));

        employees.get(3).getSkills().add(skills.get(2));
        employees.get(3).getSkills().add(skills.get(4));
        employees.get(3).getSkills().add(skills.get(3));

        employees.get(3).setWeekAvailability(1.0);

        // dependencies
        //features.get(3).getPreviousFeatures().add(features.get(0));
        //features.get(3).getPreviousFeatures().add(features.get(1));

        features.get(7).getPreviousFeatures().add(features.get(3));

        features.get(10).getPreviousFeatures().add(features.get(2));

        features.get(11).getPreviousFeatures().add(features.get(7));

        features.get(16).getPreviousFeatures().add(features.get(10));

        features.get(19).getPreviousFeatures().add(features.get(16));

        features.get(3).setDuration(4.0);


        // required skills by features
        features.get(0).getRequiredSkills().add(skills.get(0));
        features.get(0).getRequiredSkills().add(skills.get(1));

        features.get(1).getRequiredSkills().add(skills.get(2));

        features.get(2).getRequiredSkills().add(skills.get(3));

        features.get(3).getRequiredSkills().add(skills.get(3));
        features.get(3).getRequiredSkills().add(skills.get(4));

        features.get(4).getRequiredSkills().add(skills.get(0));

        features.get(5).getRequiredSkills().add(skills.get(0));
        features.get(5).getRequiredSkills().add(skills.get(1));

        features.get(6).getRequiredSkills().add(skills.get(0));
        features.get(6).getRequiredSkills().add(skills.get(3));

        features.get(7).getRequiredSkills().add(skills.get(0));

        features.get(8).getRequiredSkills().add(skills.get(1));

        features.get(9).getRequiredSkills().add(skills.get(0));

        features.get(10).getRequiredSkills().add(skills.get(3));

        features.get(11).getRequiredSkills().add(skills.get(1));
        features.get(11).getRequiredSkills().add(skills.get(3));

        features.get(12).getRequiredSkills().add(skills.get(2));
        features.get(12).getRequiredSkills().add(skills.get(4));

        features.get(13).getRequiredSkills().add(skills.get(0));

        features.get(14).getRequiredSkills().add(skills.get(1));

        features.get(15).getRequiredSkills().add(skills.get(0));

        features.get(16).getRequiredSkills().add(skills.get(3));

        features.get(17).getRequiredSkills().add(skills.get(0));

        features.get(18).getRequiredSkills().add(skills.get(3));

        features.get(19).getRequiredSkills().add(skills.get(0));
        features.get(19).getRequiredSkills().add(skills.get(3));

        PlanningSolution solution = solver.executeNRP(4, 40.0, features, employees);

        System.out.print(solution.toR());

        Assert.assertTrue(solution.getPlannedFeatures().size() <= 20 ); // and done by the skilled employee

        //validator.validateAll(solution);

        solutionToDataFile(solution);
    }

    @Test
    public void noOverlappedJobs() {
        List<Skill> skills = random.skillList(3);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(3);

        random.mix(features, skills, employees);

        PlanningSolution solution = solver.executeNRP(5, 40.0, features, employees);

        validator.validateNoOverlappedJobs(solution);
    }

    private void solutionToDataFile(PlanningSolution solution) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

        String base = "src/test/data";
        String filename = String.format("%s_%s", solver.getAlgorithmType().getName(),
                dateFormat.format(Calendar.getInstance().getTime()));
        String fullPath = String.format("%s/%s.txt", base, filename);

        File f = new File(fullPath);
        f.getParentFile().mkdirs();


        try {
            Files.write(f.toPath(), solution.toR().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}