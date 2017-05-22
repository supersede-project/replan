package wrapper;

import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import logic.PopulationFilter;
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
                return new NSGAIIBuilder<PlanningSolution>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxIterations(500)
                        .setPopulationSize(100)
                        .build();
            case MOCell:
                return new MOCellBuilder<PlanningSolution>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(5000) // TODO: Does it work better by having 50000? or it is the same with 500? as in the other cases. Execution time is also important.
                        .setPopulationSize(100) // TODO: any number > 100 makes the algorithm to trigger an exception: org.uma.jmetal.util.JMetalException: The solution list size 101 is not equal to the grid size: 10 * 10
                        .build();
            case SPEA2:
                return new SPEA2Builder<PlanningSolution>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxIterations(500)
                        .setPopulationSize(200)
                        .build();
            case PESA2:
                return new PESA2Builder<PlanningSolution>(problem, crossover, mutation)
                        .setMaxEvaluations(500)
                        .setPopulationSize(200)
                        .build();
            case SMSEMOA:
                return new SMSEMOABuilder<PlanningSolution>(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(500)
                        .setPopulationSize(200)
                        .build();
            default:
                return createAlgorithm(AlgorithmType.MOCell, problem);
        }
    }

    /* ---------------------------- */
    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }
    /* ---------------------------- */

    public PlanningSolution executeNRP(int nbWeeks, Number hoursPerweek, List<Feature> features, List<Employee> employees){

        EntitiesEvaluator ee = EntitiesEvaluator.getInstance();

        NextReleaseProblem problem = ee.nextReleaseProblemAddSkills(nbWeeks, hoursPerweek, features, employees);

        PlanningSolution solution = this.generatePlanningSolution(problem);

        clearSolutionIfNotValid(solution);

        return ee.planningSolution(solution);
    }

    public PlanningSolution executeNRP(int nbWeeks, Number hoursPerweek, List<Feature> features,
                                       List<Employee> employees, PlanningSolution previousSolution) {
        EntitiesEvaluator ee = EntitiesEvaluator.getInstance();



        NextReleaseProblem problem = ee.nextReleaseProblemAddSkills(nbWeeks, hoursPerweek, features, employees);

        problem.setPreviousSolution(previousSolution);

        PlanningSolution solution = this.generatePlanningSolution(problem);

        // TODO: Since we may obtain several solutions, we should return the first one without violations (and if all have violations, and empty one).

        clearSolutionIfNotValid(solution);

        return ee.planningSolution(solution);
    }

    /*
        The generated solution might violate constraints in case the solver does not find a better one.
        In that case, the planning is invalid and should be cleared.
    */
    private void clearSolutionIfNotValid(PlanningSolution solution) {
        NumberOfViolatedConstraints<logic.PlanningSolution> numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
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
        Set<PlanningSolution> bestSolutions = PopulationFilter.getBestSolutions(population);

        return bestSolutions.iterator().next();
    }



}
