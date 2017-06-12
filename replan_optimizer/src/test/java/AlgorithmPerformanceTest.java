import entities.Employee;
import entities.Feature;
import entities.Skill;
import logic.PlanningSolution;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import wrapper.SolverNRP;

import java.io.File;
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
// TODO: I think that this is not a test, it does not verify that the algorithm works fine, it just produces some charts but the test itself cannot fail. Therefore we can, rethink it as a test or place it somewhere else to be executed when we want to generate the charts.
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
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(40);
        List<Employee> employees = random.employeeList(8);

        random.mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        runWith(solver, nbIterations, features, skills, employees);
    }

    private void runWith(SolverNRP solver, int nbIterations, List<Feature> features, List<Skill> skills,
                         List<Employee> employees) {

        List<Integer> iterations = new ArrayList<>();
        List<Integer> nbPlannedFeatures = new ArrayList<>();

        int totalPlannedFeatures = 0;

        for (int i = 0; i < nbIterations; ++i) {

            PlanningSolution solution = solver.executeNRP(20, 40.0, features, employees);

            validator.validateAll(solution);

            iterations.add(i);
            nbPlannedFeatures.add(solution.getPlannedFeatures().size());

            totalPlannedFeatures += solution.getPlannedFeatures().size();
        }
        String title = String.format("Algorithm: %s. Test set: %s", solver.getAlgorithmType().getName(), "Dummy");
        XYChart chart = new XYChartBuilder().width(1024).height(512).title(title)
                .xAxisTitle("Iteration").yAxisTitle("Number of features planned").build();
        chart.addSeries("Number of planned features", iterations, nbPlannedFeatures);

        List<Double> average = new ArrayList<>();
        for (int i = 0; i < nbIterations; ++i)
            average.add((double) totalPlannedFeatures / nbIterations);

        chart.addSeries("Average planned features", iterations, average);
        chart.getStyler().setXAxisMin(0.0).setYAxisMin(0.0)
                .setXAxisMax((double) iterations.size()).setYAxisMax((double) features.size());

        try {

            String base = "src/test/charts";
            String filename = String.format("%s_%s", solver.getAlgorithmType().getName(),
                    dateFormat.format(Calendar.getInstance().getTime()));

            String fullPath = String.format("%s/%s", base, filename);
            File f = new File(fullPath);
            f.getParentFile().mkdirs();

            BitmapEncoder.saveBitmapWithDPI(chart, fullPath, BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: I commented this test because it takes too much time.
    //@Test
    public void NSGAIITest() {
        runWith(new SolverNRP(SolverNRP.AlgorithmType.NSGAII), 20);
    }

    // TODO: I commented this test because it does not work on the development server.
    //@Test
    public void MOCellTest() {
        runWith(new SolverNRP(SolverNRP.AlgorithmType.MOCell), 20);
    }

    // TODO: I commented this test because it does not work on the development server.
    @Test
    public void runAllWithSameInput() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(20);
        List<Employee> employees = random.employeeList(5);

        random.mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        SolverNRP solver;
        //solver = new SolverNRP(SolverNRP.AlgorithmType.NSGAII);
        //runWith(solver, 20, features, skills, employees);

        solver = new SolverNRP(SolverNRP.AlgorithmType.MOCell);
        runWith(solver, 20, features, skills, employees);

        //solver = new SolverNRP(SolverNRP.AlgorithmType.PESA2);
        //runWith(solver, 20, features, skills, employees);
/*
        // Too slow (1:39 min one iteration)
        solver = new SolverNRP(SolverNRP.AlgorithmType.SPEA2);
        runWith(solver, 2, features, skills, employees);

        // Never ends
        solver = new SolverNRP(SolverNRP.AlgorithmType.SMSEMOA);
        runWith(solver, 20, features, skills, employees);*/
    }
}
