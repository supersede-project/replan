package logic.comparators;

import org.uma.jmetal.util.comparator.ConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import logic.PlanningSolution;

/**
 * Constraint violation comparator for planning solutions
 * @author Vavou
 *
 */
public class PlanningSolutionConstraintViolationComparator implements ConstraintViolationComparator<PlanningSolution> {

	/* --- Atributes --- */
	
	/**
	 * Number of Violated Constraint Attribute
	 */
	private NumberOfViolatedConstraints<PlanningSolution> numberOfViolatedConstraints;
	
	
	/* --- Constructors --- */
	
	/**
	 * Constructor
	 * Initializes the number of violated constraints attribute
	 */
	public PlanningSolutionConstraintViolationComparator() {
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
	}
	
	
	/* --- Methods --- */
	
	@Override
	public int compare(PlanningSolution solution1, PlanningSolution solution2) {
		int numViolatedConstraintInSol1 = numberOfViolatedConstraints.getAttribute(solution1),
				numViolatedConstraintInSol2 = numberOfViolatedConstraints.getAttribute(solution2);
		if (numViolatedConstraintInSol1 == numViolatedConstraintInSol2)
			return 0;
		else if (numViolatedConstraintInSol1 > numViolatedConstraintInSol2)
			return 1;
		else
			return -1;
	}

}
