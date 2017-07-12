/**
 * 
 */
package logic.operators;

import entities.Employee;
import entities.PlannedFeature;
import entities.parameters.AlgorithmParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vavou
 *
 */
public class PlanningMutationOperator implements MutationOperator<PlanningSolution> {

	/* --- Attributes --- */
	
	/**
	 * The number of tasks of the problem
	 */
	private int numberOfTasks;

	/**
	 * Mutation probability between 0.0 and 1.0
	 */
	private double mutationProbability;

	/**
	 * Random generator
	 */
	private JMetalRandom randomGenerator;

	/**
	 * The Next Release Problem which contents the employees and tasks list
	 */
	private NextReleaseProblem problem;

	/* --- Getters and setters --- */
	
	/**
	 * @return the mutationProbability
	 */
	public double getMutationProbability() {
		return mutationProbability;
	}
	
	
	/* --- Constructors --- */
	
	/**
	 * Constructs a new PlanningMutationOperator with a default value for the mutation probability
	 * Default value from {@link AlgorithmParameters}
	 * @param problem the next release problem to solve
	 */
	public PlanningMutationOperator(NextReleaseProblem problem) {
		this(problem, problem.getAlgorithmParameters().getMutationProbability(problem.getFeatures().size()));
	}
	
	/**
	 * Constructor
	 * @param problem The problem
	 * @param mutationProbability The mutation probability between 0.0 and 1.0
	 */
	public PlanningMutationOperator(NextReleaseProblem problem, double mutationProbability) {
		if (mutationProbability < 0) {
			throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
		}
		
		this.numberOfTasks = problem.getFeatures().size();
		this.mutationProbability = mutationProbability;
		this.problem = problem;
		randomGenerator = JMetalRandom.getInstance() ;
	}
	
	
	/* --- Methods --- */
	
	@Override
	public PlanningSolution execute(PlanningSolution solution) {
		int nbPlannedTasks = solution.size();
		
		for (int i = 0 ; i < nbPlannedTasks ; i++) {
			if (doMutation()) { // If we have to do a mutation
				PlannedFeature taskToMutate = solution.getPlannedFeature(i);
				if (randomGenerator.nextDouble() < 0.5) {
					changeEmployee(taskToMutate);
				}
				else {
					changeTask(solution, taskToMutate, i);
				}
			}
		}
		
		for (int i = nbPlannedTasks ; i < problem.getFeatures().size() ; i++) {
			if (doMutation()) {
				addNewTask(solution);
			}
		}
		
		/*RepairOperator reparator = new RepairOperator(problem);
		reparator.repair(child);*/
		
		return solution;
	}
	
	/**
	 * Defines if we do or not the mutation
	 * It randomly chose a number and checks if it is lower than the mutation probability
	 * @return true if the mutation must be done
	 */
	private boolean doMutation() {
		return randomGenerator.nextDouble() <= mutationProbability;
	}

	/**
	 * Add an random unplanned task to the planning
	 * - chose randomly an unplanned task
	 * - remove it from the unplanned tasks list of the solution
	 * - chose randomly an employee
	 * - create and add the planned task with the chosen task and employee
	 * @param solution the solution to mutate
	 */
	private void addNewTask(PlanningSolution solution) {
		solution.scheduleRandomFeature();
	}
	
	/**
	 * Replaces a task by another one.
	 * It can be a planned or an unplanned task, it updates the unplannedTasks list in the second case
	 * @param solution The solution to mutate
	 * @param taskToChange The planned task to modify
	 * @param taskPosition The position of the task to modify in the planning (the plannedTask list)
	 */
	private void changeTask(PlanningSolution solution, PlannedFeature taskToChange, int taskPosition) {
		int randomPosition = randomGenerator.nextInt(0, numberOfTasks);
		if (randomPosition < solution.size() - 1) { // If the random selected task is already planned then exchange with the current
			if (taskPosition == randomPosition) { 
				randomPosition++; // If problem then apply a % (modulo) size
			}
			solution.exchange(taskPosition, randomPosition);
		}
		else { // If the random selected task is not yet planned, let's do it
			// Why would you unschedule it if you're saying it's not planned?
			solution.unschedule(taskToChange);
			solution.scheduleRandomFeature(taskPosition);
		}
	}
	
	/**
	 * Change the employee of a planned task by a random one
	 * @param taskToChange the planned task to modify
	 */
	private void changeEmployee(PlannedFeature taskToChange) {
		List<Employee> skilledEmployees = new ArrayList<>(problem.getSkilledEmployees(taskToChange.getFeature().getRequiredSkills()));
		skilledEmployees.remove(taskToChange.getEmployee());
		if (skilledEmployees.size() > 0) {
			taskToChange.setEmployee(skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1)));
		}
	}
}
