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


import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class SolverNRP {

    public PlanningSolution executeNRP(int nbWeeks, Number hoursPerweek, List<Feature> features, List<Employee> employees){


        NextReleaseProblem problem = new NextReleaseProblem(features, employees, new IterationParameters(nbWeeks, hoursPerweek.doubleValue()));

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

        return PopulationFilter.getBestSolution(population);


    }

    public static void printPopulation(Collection<PlanningSolution> population) {
        int solutionCpt = 1;
        Iterator<PlanningSolution> iterator = population.iterator();

        while (iterator.hasNext()) {
            PlanningSolution currentSolution = iterator.next();
            System.out.println("Solution " + solutionCpt++ + ": " + currentSolution);
        }
    }

}
