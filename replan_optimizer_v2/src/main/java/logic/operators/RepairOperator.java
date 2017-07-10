package logic.operators;

import java.util.Iterator;

import entities.PlannedFeature;
import entities.Feature;
import logic.NextReleaseProblem;
import logic.PlanningSolution;

/**
 * Fixer for solutions with violated constraints
 * @author Vavou
 *
 */
public class RepairOperator {
	
	/**
	 * The problem to solve
	 */
	private NextReleaseProblem problem;
	
	/**
	 * Constructor of the reparator
	 * @param problem problem to solve
	 */
	public RepairOperator(NextReleaseProblem problem) {
		this.problem = problem;
	}
	
	/**
	 * Repair a solution by removing the features that violated constraints
	 * @param solution the solution to repair
	 */
	public void repair(PlanningSolution solution) {
		Iterator<PlannedFeature> it = solution.getPlannedFeatures().iterator();
		problem.evaluate(solution);
		
		while (it.hasNext()) {
			PlannedFeature currentPlannedFeature = it.next();
			boolean fine = true;
			Iterator<Feature> itPrevious = currentPlannedFeature.getFeature().getPreviousFeatures().iterator();
			
			while (fine && itPrevious.hasNext()) {
				Feature previousFeature = itPrevious.next();
				PlannedFeature currentPreviousPlannedFeature = solution.findPlannedFeature(previousFeature);
				if (currentPreviousPlannedFeature == null || currentPreviousPlannedFeature.getEndHour() > currentPlannedFeature.getBeginHour()) {
					solution.unschedule(currentPlannedFeature);
					it.remove();
					fine = false;
					problem.evaluate(solution);
				}
			}
		}
	}

}
