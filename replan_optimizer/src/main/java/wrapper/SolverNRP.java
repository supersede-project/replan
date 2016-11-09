package wrapper;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.AlgorithmRunner;


import entities.Employee;
import entities.Feature;
import entities.parameters.IterationParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import logic.PopulationFilter;
import logic.comparators.PlanningSolutionDominanceComparator;
import logic.operators.PlanningCrossoverOperator;
import logic.operators.PlanningMutationOperator;


import java.util.List;
import java.util.Set;


public class SolverNRP {

    public PlanningSolution executeNRP(int nbWeeks, Number hoursPerweek, List<Feature> features, List<Employee> employees){

        EntitiesEvaluator ee = EntitiesEvaluator.getInstance();

        NextReleaseProblem problem = ee.nextReleaseProblemAddSkills(nbWeeks,hoursPerweek,features,employees);

        PlanningSolution solution = this.generatePlanningSolution(problem);

        return ee.planningSolution(solution);
    }

    private PlanningSolution generatePlanningSolution(NextReleaseProblem problem) {

        Algorithm<List<PlanningSolution>> algorithm;
        CrossoverOperator<PlanningSolution> crossover;
        MutationOperator<PlanningSolution> mutation;
        SelectionOperator<List<PlanningSolution>, PlanningSolution> selection;

        crossover = new PlanningCrossoverOperator(problem);

        mutation = new PlanningMutationOperator(problem);

        selection = new BinaryTournamentSelection<>(new PlanningSolutionDominanceComparator());

        algorithm = new NSGAIIBuilder<PlanningSolution>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(500)
                .setPopulationSize(100)
                .build();

        new AlgorithmRunner.Executor(algorithm).execute();

        List<PlanningSolution> population = algorithm.getResult();
        Set<PlanningSolution> bestSolutions = PopulationFilter.getBestSolutions(population);

        return bestSolutions.iterator().next();
    }

}
