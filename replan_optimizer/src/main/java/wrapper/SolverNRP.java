package wrapper;

import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import entities.parameters.IterationParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import logic.PopulationFilter;
import logic.SolutionQuality;
import logic.comparators.PlanningSolutionDominanceComparator;
import logic.operators.PlanningCrossoverOperator;
import logic.operators.PlanningMutationOperator;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import java.util.List;
import java.util.Set;


public class SolverNRP {

    public enum AlgorithmType {
        NSGAII("NSGA-II"), MOCell("MOCell"), SPEA2("SPEA2"), PESA2("PESA2"), SMSEMOA("SMSEMOA");

        private String name;

        AlgorithmType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Algorithm<List<PlanningSolution>> algorithm;
    private AlgorithmType algorithmType;


    public SolverNRP() {
        algorithm = null;
        algorithmType = AlgorithmType.MOCell;
    }

    public SolverNRP(AlgorithmType algorithmType) {
        this();
        this.algorithmType = algorithmType;
    }


    private Algorithm<List<PlanningSolution>> createAlgorithm(AlgorithmType algorithmType, NextReleaseProblem problem) {
        CrossoverOperator<PlanningSolution> crossover;
        MutationOperator<PlanningSolution> mutation;
        SelectionOperator<List<PlanningSolution>, PlanningSolution> selection;

        crossover = new PlanningCrossoverOperator(problem);
        mutation = new PlanningMutationOperator(problem);
        selection = new BinaryTournamentSelection<>(new PlanningSolutionDominanceComparator());

        switch (algorithmType) {
            case NSGAII:
                return new NSGAIIBuilder<>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxIterations(500)
                        .setPopulationSize(100)
                        .build();
            case MOCell:
                return new MOCellBuilder<>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(25000)
                        .setPopulationSize(2500)    // sqrt(populationSize) tiene que ser entero
                        .setNeighborhood(new C9<>((int) Math.sqrt(2500), (int) Math.sqrt(2500)))
                        .build();
            case SPEA2:
                return new SPEA2Builder<>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxIterations(500)
                        .setPopulationSize(200)
                        .build();
            case PESA2:
                return new PESA2Builder<>(problem, crossover, mutation)
                        .setMaxEvaluations(500)
                        .setPopulationSize(200)
                        .build();
            case SMSEMOA:
                return new SMSEMOABuilder<>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(500)
                        .setPopulationSize(200)
                        .build();
            default:
                return createAlgorithm(AlgorithmType.MOCell, problem);
        }
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }




    public PlanningSolution executeNRP(int nbWeeks, Number hoursPerweek, List<Feature> features, List<Employee> employees){

        NextReleaseProblem problem =
                new NextReleaseProblem(features, employees, new IterationParameters(nbWeeks, hoursPerweek.doubleValue()));

        PlanningSolution solution = this.generatePlanningSolution(problem);

        clearSolutionIfNotValid(solution);

        return solution;
    }

    public PlanningSolution executeNRP(int nbWeeks, Number hoursPerweek, List<Feature> features,
                                       List<Employee> employees, PlanningSolution previousSolution) {

        NextReleaseProblem problem =
                new NextReleaseProblem(features, employees, new IterationParameters(nbWeeks, hoursPerweek.doubleValue()));

        problem.setPreviousSolution(previousSolution);

        PlanningSolution solution = this.generatePlanningSolution(problem);

        clearSolutionIfNotValid(solution);

        return solution;
    }

    /*
        The generated solution might violate constraints in case the solver does not find a better one.
        In that case, the planning is invalid and should be cleared.
    */
    private void clearSolutionIfNotValid(PlanningSolution solution) {
        NumberOfViolatedConstraints<PlanningSolution> numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
        // TODO: We should know the reason of the violation.
        if (numberOfViolatedConstraints.getAttribute(solution) > 0) {
            solution.getEmployeesPlanning().clear();
            for (PlannedFeature plannedFeature : solution.getPlannedFeatures())
                solution.unschedule(plannedFeature);
        }
    }

    private PlanningSolution generatePlanningSolution(NextReleaseProblem problem) {

        algorithm = createAlgorithm(algorithmType, problem);

        new AlgorithmRunner.Executor(algorithm).execute();

        List<PlanningSolution> population = algorithm.getResult();

        SolutionQuality solutionQuality = new SolutionQuality();
        double totalQuality = 0.0;
        int totalPlannedFeatures = 0;
        for (PlanningSolution solution : population) {
            totalQuality += solutionQuality.getAttribute(solution);
            totalPlannedFeatures += solution.getPlannedFeatures().size();
        }

        double averageQuality = totalQuality/population.size();
        int averagePlannedFeatures = totalPlannedFeatures/population.size();

        System.out.println("Average solution quality: " + averageQuality + ". Average planned features: " + averagePlannedFeatures);

        Set<PlanningSolution> bestSolutions = PopulationFilter.getBestSolutions(population);

        return bestSolutions.iterator().next();
    }
}
