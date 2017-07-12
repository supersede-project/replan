package logic.comparators;

import logic.PlanningSolution;
import logic.SolutionQuality;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import java.util.Comparator;

/**
 * Comparator of PlanningSolution
 * Uses the ConstraintViolationComparator
 * @author Vavou
 *
 */
public class PlanningSolutionDominanceComparator implements Comparator<PlanningSolution> {

	/* --- Attributes --- */
	
	/**
	 * The constraint comparator
	 */
	private ConstraintViolationComparator<PlanningSolution> constraintViolationComparator;
	private NumberOfViolatedConstraints<PlanningSolution> numberOfViolatedConstraints;;
	
	
	/* --- Constructors --- */
	
	/**
	 * Constructor
	 * Initializes the constraint comparator
	 */
	public PlanningSolutionDominanceComparator() {
		constraintViolationComparator = new PlanningSolutionConstraintViolationComparator();
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
	}

	@Override
	public int compare(PlanningSolution solution1, PlanningSolution solution2) {
		if (solution1 == null) {
			throw new JMetalException("Solution1 is null") ;
		} else if (solution2 == null) {
			throw new JMetalException("Solution2 is null") ;
		} else if (solution1.getNumberOfObjectives() != solution2.getNumberOfObjectives()) {
			throw new JMetalException("Cannot compare because solution1 has " +
					solution1.getNumberOfObjectives()+ " objectives and solution2 has " +
					solution2.getNumberOfObjectives()) ;
		}

		int result = constraintViolationComparator.compare(solution1, solution2) ;

		return result != 0 ? result : dominanceTest(solution1, solution2);
	}
	
	/**
	 * Compares the two solutions objectives
	 * Returns the result of comparing the priority score objective
	 * If they are equals, it compares the end date objective
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @return -1 if solution1 is better than the second, 0 if they are equals and 1 if the second solution is better than the first
	 */
/*
	private int dominanceTest(PlanningSolution solution1, PlanningSolution solution2) {
		final int INDEX_PRIORITY_OBJECTIVE = NextReleaseProblem.INDEX_PRIORITY_OBJECTIVE;
		double sol1PriorityObjectiveValue = solution1.getObjective(INDEX_PRIORITY_OBJECTIVE);
		double sol2PriorityObjectiveValue = solution2.getObjective(INDEX_PRIORITY_OBJECTIVE);

		if (sol1PriorityObjectiveValue < sol2PriorityObjectiveValue) {
			return -1;
		}
		else if (sol1PriorityObjectiveValue > sol2PriorityObjectiveValue) {
			return 1;
		}
		else {
			final int INDEX_END_DATE_OBJECTIVE = NextReleaseProblem.INDEX_END_DATE_OBJECTIVE;
			double sol1EndDateObjectiveValue = solution1.getObjective(INDEX_END_DATE_OBJECTIVE);
			double sol2EndDateObjectiveValue = solution2.getObjective(INDEX_END_DATE_OBJECTIVE);
			if (sol1EndDateObjectiveValue < sol2EndDateObjectiveValue) {
				return -1;
			}
			else if (sol1EndDateObjectiveValue > sol2EndDateObjectiveValue) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}*/

	private int dominanceTest(PlanningSolution solution1, PlanningSolution solution2) {
		SolutionQuality quality = new SolutionQuality();
		if (quality.getAttribute(solution1) > quality.getAttribute(solution2))
			return -1;
		else if (quality.getAttribute(solution1) < quality.getAttribute(solution2))
			return 1;
		else
			return 0;
	}

}
