package java;

import entities.Employee;
import entities.Feature;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import wrapper.SolverNRP;

import java.util.Arrays;
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

    @Test
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

        PlanningSolution solution = solver.executeNRP(3, 40.0, features, employees);
        Assert.assertTrue(solution.getPlannedFeatures().isEmpty());
    }

    @Test
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

    @Test
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
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(10);

        random.mix(features, skills, employees);

        PlanningSolution solution = solver.executeNRP(5, 40.0, features, employees);

        validator.validateAll(solution);
    }

    @Test
    public void randomReplanValidatesAllConstraints() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> employees = random.employeeList(10);

        random.mix(features, skills, employees);

        PlanningSolution s1 = solver.executeNRP(5, 40.0, features, employees);

        random.freeze(s1);

        PlanningSolution s2 = solver.executeNRP(5, 40.0, features, employees, s1);

        validator.validateAll(s1, s2);
    }



    @Test
    public void ideal() {
        /*
        int nbIterations = 20;

        List<Integer> iterations = new ArrayList<>();
        List<Integer> nbPlannedFeatures = new ArrayList<>();

        List<Skill> skills = random.skillList(10);
        List<Feature> features = random.featureList(50);
        List<Employee> employees = random.employeeList(50);

        random.mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        for (int i = 0; i < nbIterations; ++i) {
            // I have to do this because of the null skill added by EntitiesEvaluator
            removeNullSkillsFromEmployees(employees);
            removeNullSkillsFromFeatures(features);

            PlanningSolution s1 = solver.executeNRP(10, 40.0, features, employees);

            validator.validateAll(s1);

            iterations.add(i);
            nbPlannedFeatures.add(s1.getPlannedFeatures().size());
        }

        XYChart chart = new XYChartBuilder().width(1024).height(512).title("Test chart")
                .xAxisTitle("Iteration").yAxisTitle("Number of features planned").build();
        chart.addSeries("-", iterations, nbPlannedFeatures);
        chart.getStyler().setXAxisMin(0.0).setYAxisMin(0.0)
                .setXAxisMax((double) nbIterations).setYAxisMax((double) features.size());

        try {
            BitmapEncoder.saveBitmapWithDPI(chart,
                    "C:\\Users\\kredes\\Desktop\\Proyectos\\replan\\replan_optimizer\\src\\main\\tests\\charts\\test",
                    BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}
