import entities.Employee;
import entities.Feature;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import wrapper.SolverNRP;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kredes on 09/05/2017.
 */
public class AlgorithmPerformanceTest {
    private static RandomThings random;
    private static Validator validator;
    private static DateFormat dateFormat;

    @BeforeClass
    public static void setUpBeforeClass() {
        random = new RandomThings();
        validator = new Validator();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    }

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


    private void runWith(SolverNRP solver, int nbIterations) {
        List<Skill> skills = random.skillList(10);
        List<Feature> features = random.featureList(50);
        List<Employee> employees = random.employeeList(50);

        random.mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        runWith(solver, nbIterations, features, skills, employees);
    }

    private void runWith(SolverNRP solver, int nbIterations, List<Feature> features, List<Skill> skills,
                         List<Employee> employees) {

        List<Integer> iterations = new ArrayList<>();
        List<Integer> nbPlannedFeatures = new ArrayList<>();

        for (int i = 0; i < nbIterations; ++i) {
            /* I have to do this because of the null skill added by EntitiesEvaluator */
            removeNullSkillsFromEmployees(employees);
            removeNullSkillsFromFeatures(features);

            PlanningSolution solution = solver.executeNRP(10, 40.0, features, employees);

            validator.validateAll(solution);
            Assert.assertTrue(solution.getPlannedFeatures().size() > 0);

            iterations.add(i);
            nbPlannedFeatures.add(solution.getPlannedFeatures().size());
        }
        String title = String.format("Algorithm: %s. Test set: %s", solver.getAlgorithmType().getName(), "Dummy");
        XYChart chart = new XYChartBuilder().width(1024).height(512).title(title)
                .xAxisTitle("Iteration").yAxisTitle("Number of features planned").build();
        chart.addSeries("-", iterations, nbPlannedFeatures);
        chart.getStyler().setXAxisMin(0.0).setYAxisMin(0.0)
                .setXAxisMax((double) iterations.size() - 1).setYAxisMax((double) features.size() - 1);

        try {
            String base = "../../test/charts/";
            String filename = String.format("%s_%s", solver.getAlgorithmType().getName(),
                    dateFormat.format(Calendar.getInstance().getTime()));

            String fullPath = String.format("%s/%s", base, filename);

            BitmapEncoder.saveBitmapWithDPI(chart, fullPath, BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    @Test
    public void NSGAIITest() {
        runWith(new SolverNRP(SolverNRP.AlgorithmType.NSGAII), 20);
    }

    @Test
    public void MOCellTest() {
        runWith(new SolverNRP(SolverNRP.AlgorithmType.MOCell), 20);
    }

    @Test
    public void runAllWithSameInput() {
        List<Skill> skills = random.skillList(10);
        List<Feature> features = random.featureList(50);
        List<Employee> employees = random.employeeList(50);

        random.mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        SolverNRP solver1 = new SolverNRP(SolverNRP.AlgorithmType.NSGAII);
        SolverNRP solver2 = new SolverNRP(SolverNRP.AlgorithmType.MOCell);

        runWith(solver1, 20, features, skills, employees);
        runWith(solver2, 20, features, skills, employees);
    }
}
