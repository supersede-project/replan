/**
 * 
 */
package logic.operators;

import entities.PlannedFeature;
import entities.parameters.AlgorithmParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

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
	 * Default value from {@link AlgorithmParameters}
	 * @param problem
	 */
	public PlanningCrossoverOperator(NextReleaseProblem problem) {
		this(problem, problem.getAlgorithmParameters().getCrossoverProbability());
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
	private List<PlanningSolution> doCrossover(PlanningSolution parent1, PlanningSolution parent2) {
		List<PlanningSolution> offspring = new ArrayList<>(2);

		offspring.add(new PlanningSolution(parent1.getProblem(), false)) ;
		offspring.add(new PlanningSolution(parent1.getProblem(), false)) ;

        int sizeP1 = parent1.size();
        int sizeP2 = parent2.size();

        int minSize = Math.min(sizeP1, sizeP2);
        PlanningSolution shorterParent = sizeP1 < sizeP2 ? parent1 : parent2;
        PlanningSolution largerParent = sizeP1 < sizeP2 ? parent2 : parent1;

        for (PlanningSolution child : offspring) {
            int i = 0;
            for (; i < minSize; ++i) {
                PlannedFeature pf;
                if (randomGenerator.nextDouble() < 0.5)
                    pf = shorterParent.getPlannedFeatures().get(i);
                else
                    pf = largerParent.getPlannedFeatures().get(i);

                child.scheduleAtTheEnd(pf.getFeature(), pf.getEmployee());
            }
            List<PlannedFeature> uncrossed = largerParent.getEndPlannedFeaturesSubListCopy(i);
            for (PlannedFeature pf : uncrossed) {
                if (randomGenerator.nextDouble() < 0.5)
                    child.scheduleAtTheEnd(pf.getFeature(), pf.getEmployee());
            }
        }



		
		/*RepairOperator reparator = new RepairOperator(problem);
		for (PlanningSolution planningSolution : offspring) {
			reparator.repair(planningSolution);
		}*/
		
		return offspring;
	}
}
