import entities.*;
import io.swagger.model.ApiNextReleaseProblem;
import io.swagger.model.ApiPlanningSolution;
import logic.analytics.Analytics;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import logic.SolverNRP;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Tests a variety of situations on {@link SolverNRP}
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

    @BeforeClass
    public static void setUpBeforeClass() {
        solver = new SolverNRP(SolverNRP.AlgorithmType.NSGAII);
        random = new RandomThings();
        validator = new Validator();
    }


    /*   -------
        | TESTS |
         -------
     */
    @Test
    public void featureIsNotPlannedIfThereIsNoSkilledResource() {
        List<Skill> skills = random.skillList(2);
        Feature f = random.feature();
        Employee e = random.employee();

        f.getRequiredSkills().addAll(skills);
        e.getSkills().add(skills.get(0));

        NextReleaseProblem problem = new NextReleaseProblem(asList(f), asList(e), 3, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
        validator.validateSkills(solution);
    }

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

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 3, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        validator.validateDependencies(solution);
    }

    @Test
    public void featureDependingOnItselfIsNotPlanned() {
        Skill s1 = random.skill();
        Feature f1 = random.feature();
        Employee e1 = random.employee();

        e1.getSkills().add(s1);

        f1.getRequiredSkills().add(s1);

        f1.getPreviousFeatures().add(f1);

        NextReleaseProblem problem = new NextReleaseProblem(asList(f1), asList(e1), 3, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }

    @Test
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

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 3, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }

    //@Test
    public void frozenPlannedFeaturesAreNotReplaned() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(7);

        random.mix(features, skills, employees);

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 3, 40.0);
        PlanningSolution s1 = solver.executeNRP(problem);

        random.freeze(s1);

        problem = new NextReleaseProblem(features, employees, 3, 40.0);
        PlanningSolution s2 = solver.executeNRP(problem);

        validator.validateFrozen(s1, s2);
    }

    //@Test
    public void frozenPlannedFeaturesViolatingPrecedencesAreReplannedToo() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(10);

        random.mix(features, skills, employees);

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 5, 40.0);
        PlanningSolution s1 = solver.executeNRP(problem);

        PlanningSolution s1Prime = new PlanningSolution(s1);

        random.violatePrecedences(s1Prime);

        try {
            validator.validateDependencies(s1Prime);
            Assert.assertTrue("Called Random.violatePrecedences(solution) but precedences are valid.", false);
        } catch (AssertionError e) {
            // OK
        }

        problem = new NextReleaseProblem(features, employees, 5, 40.0);
        problem.setPreviousSolution(new ApiPlanningSolution(s1Prime));
        PlanningSolution s2 = solver.executeNRP(problem);

        validator.validateFrozen(s1, s2);
    }

    @Test
    public void randomProblemValidatesAllConstraints() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(20);
        List<Employee> employees = random.employeeList(5);

        random.mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        for (int i = 0; i < 1; ++i) {
            NextReleaseProblem problem = new NextReleaseProblem(features, employees, 4, 40.0);
            PlanningSolution solution = solver.executeNRP(problem);

            validator.validateAll(solution);

            solutionToDataFile(solution);
        }
    }


    // TODO: It won't pass the frozen validation if the solution is cleared because of constraint violation as all planned features are removed, including frozen ones
    //@Test
    public void randomReplanValidatesAllConstraints() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(15);
        List<Employee> employees = random.employeeList(10);

        random.mix(features, skills, employees);

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 5, 40.0);
        PlanningSolution s1 = solver.executeNRP(problem);

        random.freeze(s1);

        problem = new NextReleaseProblem(features, employees, 5, 40.0);
        problem.setPreviousSolution(new ApiPlanningSolution(s1));
        PlanningSolution s2 = solver.executeNRP(problem);

        validator.validateAll(s1, s2);
    }

    @Test
    public void featureWithNoRequiredSkillsCanBeDoneByAnSkilledEmployee() {
        Feature f = random.feature();
        Employee e = random.employee();
        Skill s = random.skill();

        e.getSkills().add(s);

        NextReleaseProblem problem = new NextReleaseProblem(asList(f), asList(e), 4, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        PlannedFeature pf = jobs.get(0);

        Assert.assertTrue(jobs.size() == 1 && pf.getFeature().equals(f) && pf.getEmployee().equals(e));
    }

    @Test
    public void featureWithNoRequiredSkillsCanBeDoneByANonSkilledEmployee() {
        Feature f = random.feature();
        Employee e = random.employee();
        NextReleaseProblem problem = new NextReleaseProblem(asList(f), asList(e), 4, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        PlannedFeature pf = jobs.get(0);

        Assert.assertTrue(jobs.size() == 1 && pf.getFeature().equals(f) && pf.getEmployee().equals(e));
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

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 4, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

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

        features.get(7).getPreviousFeatures().add(features.get(9));

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

        features.get(19).getRequiredSkills().add(new Skill("No one has this skill"));


        for (int i = 0; i < 1; ++i) {
            NextReleaseProblem problem = new NextReleaseProblem(features, employees, 4, 40.0);
            PlanningSolution solution = solver.executeNRP(problem);

            validator.validateAll(solution);

            Analytics analytics = new Analytics(solution);

            solutionToDataFile(solution);
        }
    }

    @Test
    public void noOverlappedJobs() {
        List<Skill> skills = random.skillList(3);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(3);

        random.mix(features, skills, employees);

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 5, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        validator.validateNoOverlappedJobs(solution);
    }

    @Test
    public void endHourMinusBeginHourEqualsDuration() {
        List<Skill> skills = random.skillList(6);
        List<Feature> features = random.featureList(14);
        List<Employee> employees = random.employeeList(4);

        random.mix(features, skills, employees);

        NextReleaseProblem problem = new NextReleaseProblem(features, employees, 3, 40.0);
        PlanningSolution solution = solver.executeNRP(problem);

        validator.validateAll(solution);

        for (PlannedFeature pf : solution.getPlannedFeatures())
            Assert.assertTrue(
                    "endHour - beginHour != feature.duration for plannedFeature " + pf.toString(),
                    pf.getEndHour() - pf.getBeginHour() == pf.getFeature().getDuration()
            );
    }

    @Test
    public void ATOSRelease7() {
        ApiNextReleaseProblem p = ApiNextReleaseProblem.fromFile("ATOS - Release 7.txt");

        if (p == null)
            throw new AssertionError("Dataset file does not exist or contains invalid data.");

        NextReleaseProblem problem = new NextReleaseProblem(p);
        PlanningSolution solution = new SolverNRP().executeNRP(problem);

        validator.validateAll(solution);

        solutionToDataFile(solution);
    }

    @Test
    public void SenerconRelease2() {
        ApiNextReleaseProblem p = ApiNextReleaseProblem.fromFile("SEnerCon - Release 2.txt");

        if (p == null)
            throw new AssertionError("Dataset file does not exist or contains invalid data.");

        NextReleaseProblem problem = new NextReleaseProblem(p);
        PlanningSolution solution = new SolverNRP().executeNRP(problem);

        validator.validateAll(solution);

        solutionToDataFile(solution);
    }

    @Test
    public void SenerconRelease3() {
        ApiNextReleaseProblem p = ApiNextReleaseProblem.fromFile("SEnerCon - Release 3.txt");

        if (p == null)
            throw new AssertionError("Dataset file does not exist or contains invalid data.");

        NextReleaseProblem problem = new NextReleaseProblem(p);
        PlanningSolution solution = new SolverNRP().executeNRP(problem);

        validator.validateAll(solution);

        solutionToDataFile(solution);
    }

}