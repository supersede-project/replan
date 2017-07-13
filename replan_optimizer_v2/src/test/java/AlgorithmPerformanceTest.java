import entities.parameters.AlgorithmParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import logic.SolverNRP;
import org.junit.BeforeClass;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * This isn't actually a test, but it is marked as one to make it easy to run.
 * It can generate different charts to measure the performance of the planning algorithm.
 *
 * Make sure to comment out the @Test annotations after you're done running them,
 * as they will cause a crash in the deployment environment if you don't.
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

    private PlanningSolution execute(NextReleaseProblem problem, SolverNRP solver)
    {
        PlanningSolution solution = solver.executeNRP(problem);
        validator.validateAll(solution);
        return solution;
    }

    private void saveChart(XYChart chart, String filename) {
        try {
            String base = "src/test/charts";
            String fullPath = String.format("%s/%s", base, filename);
            File f = new File(fullPath);
            f.getParentFile().mkdirs();

            BitmapEncoder.saveBitmapWithDPI(chart, fullPath, BitmapEncoder.BitmapFormat.PNG, 300);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String algorithmName(SolverNRP solver) {
        return solver.getAlgorithmType().getName();
    }

    private String timestamp() {
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /*   -------
        | TESTS |
         -------
     */

    //@Test
    private void AveragePlannedFeaturesTest()
    {
        NextReleaseProblem base = random.all(7, 20, 5, 4, 40.0);
        SolverNRP solver = new SolverNRP(SolverNRP.AlgorithmType.NSGAII);

        List<Integer> iterations = new ArrayList<>();
        List<Integer> nbPlannedFeatures = new ArrayList<>();

        int totalPlannedFeatures = 0;
        int nbIterations = 20;
        for (int i = 0; i < nbIterations; ++i) {

            NextReleaseProblem problem = new NextReleaseProblem(base);
            PlanningSolution solution = execute(problem, solver);

            iterations.add(i+1);
            nbPlannedFeatures.add(solution.getPlannedFeatures().size());

            totalPlannedFeatures += solution.getPlannedFeatures().size();
        }
        String title = String.format("Algorithm: %s. Test set: %s", solver.getAlgorithmType().getName(), "Random");
        XYChart chart = new XYChartBuilder().width(1024).height(512).title(title)
                .xAxisTitle("Iteration").yAxisTitle("Number of features planned").build();
        chart.addSeries("Number of planned features", iterations, nbPlannedFeatures);

        List<Double> average = new ArrayList<>();
        for (int i = 0; i < nbIterations; ++i)
            average.add((double) totalPlannedFeatures/nbIterations);

        chart.addSeries("Average planned features", iterations, average);
        chart.getStyler().setXAxisMin(0.0).setYAxisMin(0.0)
                .setXAxisMax((double) iterations.size()).setYAxisMax((double) base.getFeatures().size());

        saveChart(chart, String.format("AveragePlannedFeatures_%s_%s", algorithmName(solver), timestamp()));
    }

    //@Test
    public void populationSizeTest() {
        for (int k = 0; k < 5; ++k) {
            NextReleaseProblem base = random.all(7, 20, 5, 4, 40.0);
            SolverNRP solver = new SolverNRP(SolverNRP.AlgorithmType.NSGAII);

            List<Integer> populationSize = new ArrayList<>();
            List<Double> executionTime = new ArrayList<>();
            List<Integer> plannedFeatures = new ArrayList<>();
            for (int i = 0; i < 15; ++i) {
                AlgorithmParameters params = new AlgorithmParameters(SolverNRP.AlgorithmType.NSGAII);
                params.setPopulationSize(params.getPopulationSize() + ++i * 10);

                NextReleaseProblem problem = new NextReleaseProblem(base);
                problem.setAlgorithmParameters(params);

                long start = System.currentTimeMillis();
                PlanningSolution solution = execute(problem, solver);
                long elapsed = System.currentTimeMillis() - start;

                populationSize.add(params.getPopulationSize());
                executionTime.add(elapsed / 1000.0);
                plannedFeatures.add(solution.size());
            }

            XYChart chart = new XYChartBuilder().width(1024).height(512).title("Population/Performance chart")
                    .xAxisTitle("Population size").build();
            chart.addSeries("Execution time (seconds)", populationSize, executionTime);
            chart.addSeries("Number of planned features", populationSize, plannedFeatures);

            saveChart(chart, String.format("PopulationSizeTest_%s_%s", algorithmName(solver), timestamp()));
        }
    }
}
