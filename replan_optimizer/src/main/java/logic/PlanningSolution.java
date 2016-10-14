/**
 * A solution of the NRP
 * It contains a plannedFeatures list which give the order of the features which are planned
 */
package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import entities.Employee;
import entities.EmployeeWeekAvailability;
import entities.PlannedFeature;
import entities.parameters.DefaultAlgorithmParameters;
import entities.Feature;

/**
 * @author Vavou
 *
 */
public class PlanningSolution extends AbstractGenericSolution<PlannedFeature, NextReleaseProblem> {

	/* --- Attributes --- */
	
	/**
	 * Generated Id
	 */
	private static final long serialVersionUID = 615615442782301271L;
	
	/**
	 * Features planned for the solution
	 */
	private List<PlannedFeature> plannedFeatures;
	
	/**
	 * Features unplanned for the solution
	 */
	private List<Feature> undoneFeatures;

	/**
	 * The end hour of the solution
	 * Is up to date only when isUpToDate field is true
	 */
	private double endDate;
	
	/**
	 * The employees' week planning
	 */
	private Map<Employee, List<EmployeeWeekAvailability>> employeesPlanning;
	
	
	/* --- Getters and Setters --- */

	/**
	 * Return the hour in all of the planned features will be done
	 * @return the end hour
	 */
	public double getEndDate() {
		return endDate;
	}
	
	/**
	 * Setter of the end date
	 * @param endDate the new end date of the solution
	 */
	public void setEndDate(double endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Returns the number of features already planned
	 * @return The number of features already planned
	 */
	public int getNumberOfPlannedFeatures() {
		return plannedFeatures.size();
	}

	/**
	 * @return the plannedFeatures
	 */
	public List<PlannedFeature> getPlannedFeatures() {
		return new ArrayList<>(plannedFeatures);
	}
	
	/**
	 * Return the planned feature at <code>position</code> in the list
	 * @param position The position in the list
	 * @return the planned feature or null
	 */
	public PlannedFeature getPlannedFeature(int position) {
		if (position >= 0 && position < plannedFeatures.size())
			return plannedFeatures.get(position);
		return null;
	}
	
	/**
	 * Get a copy of the planned feature from position in the original list
	 * @param beginPosition the begin position
	 * @return a copy of the end list
	 */
	public List<PlannedFeature> getEndPlannedFeaturesSubListCopy(int beginPosition) {
		return new ArrayList<>(plannedFeatures.subList(beginPosition, plannedFeatures.size()));
	}
	
	/**
	 * private getter for undone features list
	 * @return the undone features list
	 */
	private List<Feature> getUndoneFeatures() {
		return undoneFeatures;
	}
	
	/**
	 * @return the employeesPlannings
	 */
	public Map<Employee, List<EmployeeWeekAvailability>> getEmployeesPlanning() {
		return employeesPlanning;
	}

	/**
	 * @param employeesPlannings the employeesPlannings to set
	 */
	public void setEmployeesPlanning(Map<Employee, List<EmployeeWeekAvailability>> employeesPlanning) {
		this.employeesPlanning = employeesPlanning;
	}

	
	/* --- Constructors --- */

	/**
	 * Constructor
	 * initialize a random set of planned features
	 * @param problem
	 */
	public PlanningSolution(NextReleaseProblem problem) {
		super(problem);
	    numberOfViolatedConstraints = 0;

	    initializePlannedFeatureVariables();
	    initializeObjectiveValues();
	}
	
	/**
	 * Constructor
	 * initialize a random set of planned features
	 * @param problem
	 */
	public PlanningSolution(NextReleaseProblem problem, List<PlannedFeature> plannedFeatures) {
		super(problem);
	    numberOfViolatedConstraints = 0;

	    undoneFeatures = new CopyOnWriteArrayList<Feature>();
		undoneFeatures.addAll(problem.getFeatures());
		this.plannedFeatures = new CopyOnWriteArrayList<PlannedFeature>();
		for (PlannedFeature plannedFeature : plannedFeatures) {
			scheduleAtTheEnd(plannedFeature.getFeature(), plannedFeature.getEmployee());
		}
	    initializeObjectiveValues();
	}

	/**
	 * Copy constructor
	 * @param origin PlanningSoltion to copy
	 */
	public PlanningSolution(PlanningSolution origin) {
		super(origin.problem);

	    numberOfViolatedConstraints = origin.numberOfViolatedConstraints;
	    
	    plannedFeatures = new CopyOnWriteArrayList<>();
	    for (PlannedFeature plannedFeature : origin.getPlannedFeatures()) {
			plannedFeatures.add(new PlannedFeature(plannedFeature));
		}
	    
	    // Copy constraints and quality
	    this.attributes.putAll(origin.attributes);
	    
	    employeesPlanning = new HashMap<>();
	    
	    for (Employee e : origin.employeesPlanning.keySet()) {
	    	List<EmployeeWeekAvailability> old = origin.employeesPlanning.get(e);
			List<EmployeeWeekAvailability> availabilities = new ArrayList<>(old.size());
			for (EmployeeWeekAvailability employeeWeekAvailability : old) {
				availabilities.add(new EmployeeWeekAvailability(employeeWeekAvailability));
			}
			employeesPlanning.put(e, availabilities);
		}
	    
	    for (int i = 0 ; i < origin.getNumberOfObjectives() ; i++) {
	    	this.setObjective(i, origin.getObjective(i));
	    }
	    
	    endDate = origin.getEndDate();
	    undoneFeatures = new CopyOnWriteArrayList<>(origin.getUndoneFeatures());
	}
	
	
	/* --- Methods --- */
	
	/**
	 * Exchange the two features in positions pos1 and pos2
	 * @param pos1 The position of the first planned feature to exchange
	 * @param pos2 The position of the second planned feature to exchange
	 */
	public void exchange(int pos1, int pos2) {
		if (pos1 >= 0 && pos2 >= 0 && pos1 < plannedFeatures.size() && pos2 < plannedFeatures.size() && pos1 != pos2) {
			PlannedFeature feature1 = plannedFeatures.get(pos1);
			plannedFeatures.set(pos1, new PlannedFeature(plannedFeatures.get(pos2)));
			plannedFeatures.set(pos2, new PlannedFeature(feature1));
		}
	}
	
	/**
	 * Calculate the sum of the priority of each feature
	 * @return the priority score
	 */
	public double getPriorityScore() {
		double score = problem.getWorstScore();
		
		for (PlannedFeature plannedFeature : plannedFeatures) {
			score -= plannedFeature.getFeature().getPriority().getScore();
		}
		
		return score;
	}
	
	/**
	 * Returns all of the planned features done by a specific employee
	 * @param e The employee
	 * @return The list of features done by the employee
	 */
	public List<PlannedFeature> getFeaturesDoneBy(Employee e) {
		List<PlannedFeature> featuresOfEmployee = new ArrayList<>();

		for (PlannedFeature plannedFeature : plannedFeatures) {
			if (plannedFeature.getEmployee() == e) {
				featuresOfEmployee.add(plannedFeature);
			}
		}

		return featuresOfEmployee;
	}

	/**
	 * Return true if the feature is already in the planned features
	 * @param feature Feature to search
	 * @return true if the feature is already planned
	 */
	public boolean isAlreadyPlanned(Feature feature) {
		boolean found = false;
		Iterator<PlannedFeature> it = plannedFeatures.iterator();
		
		while (!found && it.hasNext()) {
			PlannedFeature plannedFeature = (PlannedFeature) it.next();
			if (plannedFeature.getFeature().equals(feature)) {
				found = true;
			}
		}
		
		return found;
	}

	/* --- Methods --- */
	
	/**
	 * Returns the planned feature corresponding to the feature given in parameter
	 * @param feature The searched feature
	 * @return The planned feature or null if it is not yet planned
	 */
	public PlannedFeature findPlannedFeature(Feature feature) {
		for (Iterator<PlannedFeature> iterator = plannedFeatures.iterator(); iterator.hasNext();) {
			PlannedFeature plannedFeature = iterator.next();
			if (plannedFeature.getFeature().equals(feature)) {
				return plannedFeature;
			}
		}
		
		return null;
	}
	
	/**
	 * Initializes the planned features randomly
	 * @param number the number of features to plan
	 */
	private void initializePlannedFeaturesRandomly(int number) {
		Feature featureToDo;
		List<Employee> skilledEmployees;
		
		for (int i = 0 ; i < number ; i++) {
			featureToDo = undoneFeatures.get(randomGenerator.nextInt(0, undoneFeatures.size()-1));
			skilledEmployees = problem.getSkilledEmployees(featureToDo.getRequiredSkills().get(0));
			scheduleAtTheEnd(featureToDo,
					skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1)));
		}
	}

	/**
	 * Initialize the variables
	 * Load a random number of planned features
	 */
	private void initializePlannedFeatureVariables() {
		int numberOfFeatures = problem.getFeatures().size();
		int nbFeaturesToDo = randomGenerator.nextInt(0, numberOfFeatures);
		
		undoneFeatures = new CopyOnWriteArrayList<Feature>();
		undoneFeatures.addAll(problem.getFeatures());
		plannedFeatures = new CopyOnWriteArrayList<PlannedFeature>();
	
		if (randomGenerator.nextDouble() > DefaultAlgorithmParameters.RATE_OF_NOT_RANDOM_GENERATED_SOLUTION) {
			initializePlannedFeaturesRandomly(nbFeaturesToDo);
		}
		else {
			initializePlannedFeaturesWithPrecedences(nbFeaturesToDo);
		}
	}
	
	/**
	 * Initializes the planned features considering the precedences
	 * @param number the number of features to plan
	 */
	private void initializePlannedFeaturesWithPrecedences(int number) {
		Feature featureToDo;
		List<Employee> skilledEmployees;
		List<Feature> possibleFeatures = updatePossibleFeatures();
		int i = 0;
		while (i < number && possibleFeatures.size() > 0) {
			featureToDo = possibleFeatures.get(randomGenerator.nextInt(0, possibleFeatures.size()-1));
			skilledEmployees = problem.getSkilledEmployees(featureToDo.getRequiredSkills().get(0));
			scheduleAtTheEnd(featureToDo,
					skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1)));
			possibleFeatures = updatePossibleFeatures();
			i++;
		}
	}
	
	/**
	 * Reset the begin hours of all the planned feature to 0.0
	 */
	public void resetHours() {
		for (PlannedFeature plannedFeature : plannedFeatures) {
			plannedFeature.setBeginHour(0.0);
			plannedFeature.setEndHour(0.0);
		}
	}

	/**
	 * Schedule a planned feature to a position in the planning
	 * @param position the position of the planning
	 * @param feature the feature to plan
	 * @param e the employee who will execute the feature
	 */
	public void schedule(int position, Feature feature, Employee e) {
		undoneFeatures.remove(feature);
		plannedFeatures.add(position, new PlannedFeature(feature, e));
	}
		
	/**
	 * Schedule a feature in the planning
	 * Remove the feature from the undoneFeatures 
	 * and add the planned feature at the end of the planned features list
	 * @param feature the feature to schedule
	 * @param e the employee who will realize the feature
	 */
	public void scheduleAtTheEnd(Feature feature, Employee e) {
		if (!isAlreadyPlanned(feature)) {
			undoneFeatures.remove(feature);
			plannedFeatures.add(new PlannedFeature(feature, e));
		}
	}
	
	/**
	 * Schedule a random undone feature to a random place in the planning
	 */
	public void scheduleRandomFeature() {
		scheduleRandomFeature(randomGenerator.nextInt(0, plannedFeatures.size()));
	}
	
	/**
	 * Schedule a random feature to insertionPosition of the planning list
	 * @param insertionPosition the insertion position
	 */
	public void scheduleRandomFeature(int insertionPosition) {
		if (undoneFeatures.size() <= 0)
			return;
		Feature newFeature = undoneFeatures.get(randomGenerator.nextInt(0, undoneFeatures.size() -1));
		List<Employee> skilledEmployees = problem.getSkilledEmployees(newFeature.getRequiredSkills().get(0));
		Employee newEmployee = skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1));
		schedule(insertionPosition, newFeature, newEmployee);
	}
	
	/**
	 * Schedule the planned feature at a random position in the planning
	 * @param plannedFeature the plannedFeature to integrate to the planning
	 */
	public void scheduleRandomly(PlannedFeature plannedFeature) {
		schedule(randomGenerator.nextInt(0, plannedFeatures.size()), plannedFeature.getFeature(), plannedFeature.getEmployee());
	}

	/**
	 * Unschedule a feature : remove it from the planned features and add it to the undone ones
	 * @param plannedFeature the planned feature to unschedule
	 */
	public void unschedule(PlannedFeature plannedFeature) {
		if (isAlreadyPlanned(plannedFeature.getFeature())) {
			undoneFeatures.add(plannedFeature.getFeature());
			plannedFeatures.remove(plannedFeature);
				
		}
	}

	/**
	 * Creates a list of the possible features to do regarding to the precedences of the undone features
	 * @return the list of the possible features to do
	 */
	private List<Feature> updatePossibleFeatures() {
		List<Feature> possibleFeatures = new ArrayList<>();
		boolean possible;
		int i;
		
		for (Feature feature : undoneFeatures) {
			possible = true;
			i = 0;
			while (possible && i < feature.getPreviousFeatures().size()) {
				if (!isAlreadyPlanned(feature.getPreviousFeatures().get(i))) {
					possible = false;
				}
				i++;
			}
			if (possible) {
				possibleFeatures.add(feature);
			}
		}
		
		return possibleFeatures;
	}

	@Override
	public String getVariableValueString(int index) {
		return getVariableValueString(index).toString();
	}

	@Override
	public Solution<PlannedFeature> copy() {
		return new PlanningSolution(this);
	}
	
	@Override
	public int hashCode() {
		return getPlannedFeatures().size();
	};
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		PlanningSolution other = (PlanningSolution) obj;
		
		int size = this.getPlannedFeatures().size();
		boolean equals = other.getPlannedFeatures().size() == size;
		int i = 0;
		while (equals && i < size) {
			if (!other.getPlannedFeatures().contains(this.getPlannedFeatures().get(i))) {
				equals = false;
			}
			i++;
		}

		return equals;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");
		
		sb.append('(');
		for (int i = 0 ; i < getNumberOfObjectives() ; i++) {
			sb.append(getObjective(i)).append('\t');
		}
		
		sb.append(new NumberOfViolatedConstraints<>().getAttribute(this));
		sb.append(')').append(lineSeparator);
		
		for (PlannedFeature feature : getPlannedFeatures()) {
			sb.append("-").append(feature);
			sb.append(lineSeparator);
		}
		
		sb.append("End Date: ").append(getEndDate()).append(System.getProperty("line.separator"));
		
		return sb.toString();
	}
}
