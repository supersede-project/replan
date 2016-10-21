/**
 * 
 */
package logic.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import entities.PlannedFeature;
import entities.parameters.DefaultAlgorithmParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;

/**
 * The crossover operator on PlanningSolution
 * @author Vavou
 *
 */
public class PlanningCrossoverOperator implements CrossoverOperator<PlanningSolution> {

	/* --- Attributes --- */

	/**
	 * The crossover probability, between 0.0 and 1.0
	 */
	private double crossoverProbability  ;

	/**
	 * Random Generator
	 */
	private JMetalRandom randomGenerator ;
	
	/**
	 * The next release problem
	 */
	private NextReleaseProblem problem;

	
	/* --- Constructors --- */
	
	/**
	 * Constructor that initializes the crossover probability with it default value
	 * Default value from {@link DefaultAlgorithmParameters}
	 * @param problem
	 */
	public PlanningCrossoverOperator(NextReleaseProblem problem) {
		this(problem, DefaultAlgorithmParameters.CROSSOVER_PROBABILITY);
	}

	/**
	 * Constructor
	 * @param problem the next release problem
	 * @param crossoverProbability the probability to do crossover, between 0.0 and 1.0
	 */
	public PlanningCrossoverOperator(NextReleaseProblem problem, double crossoverProbability) {
		if (crossoverProbability < 0) {
			throw new JMetalException("Crossover probability is negative: " + crossoverProbability) ;
		}

		this.crossoverProbability = crossoverProbability;
		this.problem = problem;
		randomGenerator = JMetalRandom.getInstance() ;
	}

	
	/* --- Methods --- */
	
	@Override
	public List<PlanningSolution> execute(List<PlanningSolution> solutions) {
		if (null == solutions) {
			throw new JMetalException("Null parameter") ;
		} else if (solutions.size() != 2) {
			throw new JMetalException("There must be two parents instead of " + solutions.size()) ;
		}

		return doCrossover(solutions.get(0), solutions.get(1)) ;
	}
	
	/**
	 * do the crossover on the 2 parameters solutions
	 * @param parent1 the first parent
	 * @param parent2 the second parent
	 * @return a list oh the two children
	 */
	public List<PlanningSolution> doCrossover(PlanningSolution parent1, PlanningSolution parent2) {
		List<PlanningSolution> offspring = new ArrayList<PlanningSolution>(2);

		offspring.add((PlanningSolution) parent1.copy()) ;
		offspring.add((PlanningSolution) parent2.copy()) ;
		
		if (randomGenerator.nextDouble() < crossoverProbability) {
			// The two final solutions containing, at the beginning, a copy of the parents
			PlanningSolution child1 = offspring.get(0);
			PlanningSolution child2 = offspring.get(1);
			
			int minSize = Math.min(parent1.getNumberOfPlannedFeatures(), parent2.getNumberOfPlannedFeatures());
			
			if (minSize > 0) {
				int splitPosition;
				
				if (minSize == 1) {
					splitPosition = 1;
				} 
				else {
					splitPosition = randomGenerator.nextInt(1, minSize);
				}
				
				// Copy and unschedule the post-cut tasks
				List<PlannedFeature> futurEndChild1 = child2.getEndPlannedFeaturesSubListCopy(splitPosition);
				List<PlannedFeature> futurEndChild2 = child1.getEndPlannedFeaturesSubListCopy(splitPosition);
				for (PlannedFeature plannedTask : futurEndChild2) {
					child1.unschedule(plannedTask);
				}
				for (PlannedFeature plannedTask : futurEndChild1) {
					child2.unschedule(plannedTask);
				}
				
				
				// schedule the new ends and keep it in the list only if they were already planned
				Iterator<PlannedFeature> iteratorEndChild1 = futurEndChild1.iterator();
				while (iteratorEndChild1.hasNext()) {
					PlannedFeature plannedTask = (PlannedFeature) iteratorEndChild1.next();
					if (!child1.isAlreadyPlanned(plannedTask.getFeature())) {
						child1.scheduleAtTheEnd(plannedTask.getFeature(), plannedTask.getEmployee());
						iteratorEndChild1.remove();
					}
				}
				Iterator<PlannedFeature> iteratorEndChild2 = futurEndChild2.iterator();
				while (iteratorEndChild2.hasNext()) {
					PlannedFeature plannedTask = (PlannedFeature) iteratorEndChild2.next();
					if (!child2.isAlreadyPlanned(plannedTask.getFeature())) {
						child2.scheduleAtTheEnd(plannedTask.getFeature(), plannedTask.getEmployee());
						iteratorEndChild2.remove();
					}
				}
				
				// Exchanging the tasks
				iteratorEndChild1 = futurEndChild1.iterator();
				iteratorEndChild2 = futurEndChild2.iterator();
				while (iteratorEndChild1.hasNext() && iteratorEndChild2.hasNext()) {
					PlannedFeature task = iteratorEndChild1.next();
					child1.scheduleAtTheEnd(task.getFeature(), task.getEmployee());
					task = iteratorEndChild2.next();
					child2.scheduleAtTheEnd(task.getFeature(), task.getEmployee());
				}
			}
		}
		
		/*RepairOperator reparator = new RepairOperator(problem);
		for (PlanningSolution planningSolution : offspring) {
			reparator.repair(planningSolution);
		}*/
		
		return offspring;
	}
}
