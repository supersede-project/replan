/**
 * 
 */
package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import entities.Employee;
import entities.PlannedFeature;
import entities.EmployeeWeekAvailability;
import entities.Skill;
import entities.Feature;
import entities.parameters.IterationParameters;

/**
 * @author Vavou
 * 
 * Objectives: 
 * 0: Doing the high score in priority
 * 1: The shortest endDate
 *
 */
public class NextReleaseProblem extends AbstractGenericProblem<PlanningSolution> implements ConstrainedProblem<PlanningSolution> {

	/* --- Attributes --- */
	
	/**
	 * Generated Id
	 */
	private static final long serialVersionUID = 3302475694747789178L;
	
	/**
	 * Features available for the iteration
	 */
	private List<Feature> features;
	
	/**
	 * Employees available for the iteration
	 */
	private List<Employee> employees;
	
	/**
	 * Number of violated constraints
	 */
	private NumberOfViolatedConstraints<PlanningSolution> numberOfViolatedConstraints;
	
	/**
	 * Number of violated constraints
	 */
	private OverallConstraintViolation<PlanningSolution> overallConstraintViolation;
	
	/**
	 * The solution quality attribute
	 */
	private SolutionQuality solutionQuality;
	
	/**
	 * Employees sorted by skill 
	 * An employee is in a the lists of all his skills
	 */
	private Map<Skill, List<Employee>> skilledEmployees;

	/**
	 * The number of weeks of the iteration
	 */
	private int nbWeeks;
	
	/**
	 * The number of worked hours by week
	 */
	private double nbHoursByWeek;
	
	/**
	 * The overall of a violated constraint
	 */
	private double precedenceConstraintOverall;
	
	/**
	 * The priority score if there is no planned feature
	 */
	private double worstScore;
	
	/**
	 * The worst end date, if there is no planned feature
	 */
	private double worstEndDate;
	
	/**
	 * The index of the priority score objective in the objectives list
	 */
	public final static int INDEX_PRIORITY_OBJECTIVE = 0;
	
	/**
	 * The index of the end date objective in the objectives list
	 */
	public final static int INDEX_END_DATE_OBJECTIVE = 1;
	
	
	/* --- Getters and setters --- */

	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
	/**
	 * Returns the number of weeks of the iteration
	 * @return The number of weeks of the iteration
	 */
	public int getNbWeeks() {
		return nbWeeks;
	}
	
	/**
	 * Returns the number of worked hours by week
	 * @return the number of worked hours by week
	 */
	public double getNbHoursByWeek() {
		return nbHoursByWeek;
	}
	
	/**
	 * Return the number of employees
	 * @return the number of employees
	 */
	public int getNumberOfEmployees() {
		return employees.size();
	}
	
	/**
	 * @return the numberOfViolatedConstraints
	 */
	public NumberOfViolatedConstraints<PlanningSolution> getNumberOfViolatedConstraints() {
		return numberOfViolatedConstraints;
	}

	/**
	 * @return the employees with a skill
	 */
	public List<Employee> getSkilledEmployees(Skill skill) {
		return skilledEmployees.get(skill);
	}
	
	/**
	 * @return the list of the employees
	 */
	public List<Employee> getEmployees() {
		return employees;
	}
	
	/**
	 * @return the worstScore
	 */
	public double getWorstScore() {
		return worstScore;
	}


	/* --- Constructors --- */
	
	/**
	 * Constructor
	 * @param features features of the iteration
	 * @param employees employees available during the iteration
	 * @param iterationParam The parameters of the iteration
	 */
	public NextReleaseProblem(List<Feature> features, List<Employee> employees, IterationParameters iterationParam) {
		this.employees = employees;
		this.nbWeeks = iterationParam.getNumberOfWeek();
		this.nbHoursByWeek = iterationParam.getHoursByWeek();
		
		skilledEmployees = new HashMap<>();
		for (Employee employee : employees) {
			for (Skill skill : employee.getSkills()) {
				List<Employee> employeesList = skilledEmployees.get(skill);
				if (employeesList == null) {
					employeesList = new ArrayList<>();
					skilledEmployees.put(skill, employeesList);
				}
				employeesList.add(employee);
			}
		}
		
		this.features = new ArrayList<>();
		for (Feature feature : features) {
			if (skilledEmployees.get(feature.getRequiredSkills().get(0)) != null) {
				if (features.containsAll(feature.getPreviousFeatures())) {
					this.features.add(feature);
				}
			}
		}
		
		worstEndDate = nbWeeks * nbHoursByWeek;
		setNumberOfVariables(1);
		setName("Next Release Problem");
		setNumberOfObjectives(2);
		initializeWorstScore();
		initializeNumberOfConstraint();
		
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<PlanningSolution>();
		overallConstraintViolation = new OverallConstraintViolation<>();
		solutionQuality = new SolutionQuality();
	}
	
	
	/* --- Methods --- */
	
	/**
	 * Initializes the worst score 
	 * Corresponding to the addition of each feature priority score
	 */
	private void initializeWorstScore() {
		worstScore = 0.0;
		for (Feature feature : features) {
			worstScore += feature.getPriority().getScore();
		}
	}
	
	/**
	 * Initializes the number of constraints for the problem
	 * Corresponding to the number of precedences more one for the time overflow
	 */
	private void initializeNumberOfConstraint() {
		int numberOfConstraints = 0;
		
		//Precedences
		for (Feature feature : features) {
			numberOfConstraints += feature.getPreviousFeatures().size();
		}
		
		precedenceConstraintOverall = 1.0 / numberOfConstraints;
		
		// Global overflow
		numberOfConstraints++;
		
		setNumberOfConstraints(numberOfConstraints);
	}


	@Override
	public PlanningSolution createSolution() {
		return new PlanningSolution(this);
	}

	@Override
	public void evaluate(PlanningSolution solution) {
		double newBeginHour;
		double endPlanningHour = 0.0;
		Map<Employee, List<EmployeeWeekAvailability>> employeesTimeSlots = new HashMap<>();
		List<PlannedFeature> plannedFeatures = solution.getPlannedFeatures();
			
		solution.resetHours();
		
		for (PlannedFeature currentPlannedFeature : plannedFeatures) {
			newBeginHour = 0.0;
			Feature currentFeature = currentPlannedFeature.getFeature();
				
			// Checks the previous features end hour
			for (Feature previousFeature : currentFeature.getPreviousFeatures()) {
				PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previousFeature);
				if (previousPlannedFeature != null) {
					newBeginHour = Math.max(newBeginHour, previousPlannedFeature.getEndHour());
				}
			}
				
			// Checks the employee availability
			Employee currentEmployee = currentPlannedFeature.getEmployee();
			List<EmployeeWeekAvailability> employeeTimeSlots = employeesTimeSlots.get(currentEmployee);
			int currentWeek;
			
			if (employeeTimeSlots == null) {
				employeeTimeSlots = new ArrayList<>();
				employeeTimeSlots.add(new EmployeeWeekAvailability(newBeginHour, currentEmployee.getWeekAvailability()));
				employeesTimeSlots.put(currentEmployee, employeeTimeSlots);
				currentWeek = 0;
			}
			else {
				currentWeek = employeeTimeSlots.size()-1;
				newBeginHour = Math.max(newBeginHour, employeeTimeSlots.get(currentWeek).getEndHour());
			}

			currentPlannedFeature.setBeginHour(newBeginHour);
			
			double remainFeatureHours = currentPlannedFeature.getFeature().getDuration();
			double leftHoursInWeek;
			EmployeeWeekAvailability currentWeekAvailability;
			
			currentWeek = ((int) newBeginHour) / (int)nbHoursByWeek;
			
			while (newBeginHour > (currentWeek + 1) * nbHoursByWeek) {
				System.err.println("go");
				currentWeek++;
			}
			
			
			
			
			
			do {
				currentWeekAvailability = employeeTimeSlots.get(employeeTimeSlots.size() - 1);
				double newBeginHourInWeek = Math.max(newBeginHour, currentWeekAvailability.getEndHour());
				leftHoursInWeek = Math.min((currentWeek + 1) * nbHoursByWeek - newBeginHourInWeek //Left Hours in the week
						, currentWeekAvailability.getRemainHoursAvailable());
				
				if (remainFeatureHours <= leftHoursInWeek) { // The feature can be ended before the end of the week
					currentWeekAvailability.setRemainHoursAvailable(currentWeekAvailability.getRemainHoursAvailable() - remainFeatureHours);
					currentWeekAvailability.setEndHour(newBeginHourInWeek + remainFeatureHours);
					remainFeatureHours = 0.0;
				}
				else {
					currentWeekAvailability.setRemainHoursAvailable(currentWeekAvailability.getRemainHoursAvailable() - leftHoursInWeek);
					currentWeekAvailability.setEndHour(currentWeekAvailability.getEndHour() + leftHoursInWeek);
					remainFeatureHours -= leftHoursInWeek;
					currentWeek++;
					employeeTimeSlots.add(new EmployeeWeekAvailability(currentWeek*nbHoursByWeek, currentEmployee.getWeekAvailability()));
				}
				currentWeekAvailability.addPlannedFeature(currentPlannedFeature);
			} while (remainFeatureHours > 0.0);
			
			currentPlannedFeature.setEndHour(currentWeekAvailability.getEndHour());

			endPlanningHour = Math.max(currentPlannedFeature.getEndHour(), endPlanningHour);
		}
		
		solution.setEmployeesPlanning(employeesTimeSlots);
		solution.setEndDate(endPlanningHour);
		solution.setObjective(INDEX_PRIORITY_OBJECTIVE, solution.getPriorityScore());
		solution.setObjective(INDEX_END_DATE_OBJECTIVE, 
				plannedFeatures.size() == 0 ? worstEndDate : endPlanningHour);
		computeQuality(solution);
	}

	@Override
	public void evaluateConstraints(PlanningSolution solution) {
		int precedencesViolated = 0;
		int violatedConstraints;
		double overall;
		
		for (PlannedFeature currentFeature : solution.getPlannedFeatures()) {
			for (Feature previousFeature : currentFeature.getFeature().getPreviousFeatures()) {
				PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previousFeature);
				if (previousPlannedFeature == null || previousPlannedFeature.getEndHour() > currentFeature.getBeginHour()) {
					precedencesViolated++;
				}
			}
		}
		
		overall = -1.0 * precedencesViolated * precedenceConstraintOverall;
		violatedConstraints = precedencesViolated;
		
		if (solution.getEndDate() > nbWeeks * nbHoursByWeek) {
			violatedConstraints++;
			overall -= 1.0;
		}
		
		numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
		overallConstraintViolation.setAttribute(solution, overall);
		if (violatedConstraints > 0) {
			solutionQuality.setAttribute(solution, 0.0);
		}
	}
	
	/**
	 * Updates the quality attribute of the solution
	 * @param solution The solution to evaluate quality
	 */
	private void computeQuality(PlanningSolution solution) {
		double globalQuality;
		double endDateQuality = 1.0 - (solution.getObjective(INDEX_END_DATE_OBJECTIVE) / worstEndDate);
		double priorityQuality = 1.0 - (solution.getObjective(INDEX_PRIORITY_OBJECTIVE) / worstScore);
		
		globalQuality = (endDateQuality + priorityQuality) / 2;
		solutionQuality.setAttribute(solution, globalQuality);
	}
}
