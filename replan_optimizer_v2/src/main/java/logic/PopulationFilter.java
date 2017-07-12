package logic;

import logic.comparators.PlanningSolutionDominanceComparator;

import java.util.*;

public class PopulationFilter {
	
	/**
	 * Sorts the population by dominance
	 * uses a <code>PlaningSolutionDominanceComparator</code>
	 * @param population the population to sort
	 */
	private static void sortByDominance(List<PlanningSolution> population) {	
		population.sort(new PlanningSolutionDominanceComparator());
	}
	
	/**
	 * Returns the best solution
	 * after sorting by dominance, it returns the first one
	 * @param population the base population
	 * @return the best solution
	 */
	public static PlanningSolution getBestSolution(List<PlanningSolution> population) {
		sortByDominance(population);
		
		return population.get(0);
	}
	
	/**
	 * Returns only the bests solutions
	 * Uses the <code>PlanningSolutionDominanceComparator</code>
	 * @param population The population with only the best solutions
	 */
	public static Set<PlanningSolution> getBestSolutions(List<PlanningSolution> population) {
		Set<PlanningSolution> bestSolutions = new HashSet<>();
		
		if (population.size() == 0)
			return bestSolutions;
		
		PlanningSolution bestSolution = getBestSolution(population);
		
		Comparator<PlanningSolution> comparator = new PlanningSolutionDominanceComparator();

		for (PlanningSolution currentSolution : population)
			if (comparator.compare(currentSolution, bestSolution) == 0)
				bestSolutions.add(currentSolution);
		
		return bestSolutions;
	}

}
