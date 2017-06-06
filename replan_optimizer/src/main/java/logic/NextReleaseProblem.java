/**
 * @author Vavou
 */
package logic;

import entities.*;
import entities.parameters.IterationParameters;
import io.swagger.annotations.ApiModelProperty;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Objectives: 0: Doing the high score in priority; 1: The shortest endDate
public class NextReleaseProblem extends AbstractGenericProblem<PlanningSolution> implements ConstrainedProblem<PlanningSolution> {

	private static final boolean USE_NEW_EVALUATION = false;

	private static final long serialVersionUID = 3302475694747789178L; // Generated Id
	public final static int INDEX_PRIORITY_OBJECTIVE = 0; // The index of the priority score objective in the objectives list
	public final static int INDEX_END_DATE_OBJECTIVE = 1; // The index of the end date objective in the objectives list

	// PROBLEM
	private List<Feature> features;
	private List<Employee> employees;
	private PlanningSolution previousSolution;
	private int nbWeeks; // The number of weeks of the iteration
	private double nbHoursByWeek; // The number of worked hours by week

	// SOLUTION
	private NumberOfViolatedConstraints<PlanningSolution> numberOfViolatedConstraints;
	private OverallConstraintViolation<PlanningSolution> overallConstraintViolation;
	private SolutionQuality solutionQuality;
	private double precedenceConstraintOverall; //The overall of a violated constraint
	private double worstScore; //The priority score if there is no planned feature
	private double worstEndDate; //The worst end date, if there is no planned feature

	// GETTERS / SETTERS
	public PlanningSolution getPreviousSolution() {
		return previousSolution;
	}
	public void setPreviousSolution(PlanningSolution previousSolution) {
		this.previousSolution = previousSolution;
	}
    @ApiModelProperty(value = "")
	public List<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
    @ApiModelProperty(value = "")
	public int getNbWeeks() {
		return nbWeeks;
	}
    @ApiModelProperty(value = "")
	public double getNbHoursByWeek() {
		return nbHoursByWeek;
	}
	public int getNumberOfEmployees() {
		return employees.size();
	}
	public NumberOfViolatedConstraints<PlanningSolution> getNumberOfViolatedConstraints() {
		return numberOfViolatedConstraints;
	}
	public List<Employee> getSkilledEmployees(List<Skill> reqSkills) {
		ArrayList<Employee> skilledEmployees = new ArrayList<>();
		for (Employee employee : employees)
			if(employee.getSkills().containsAll(reqSkills))
				skilledEmployees.add(employee);
		return skilledEmployees;
	}
    @ApiModelProperty(value = "")
	public List<Employee> getEmployees() {
		return employees;
	}
	public double getWorstScore() {
		return worstScore;
	}

	// Constructor (empty)
	public NextReleaseProblem() {
		setName("Next Release Problem");
		setNumberOfVariables(1);
		setNumberOfObjectives(2);
		features = new ArrayList<>();
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<PlanningSolution>();
		overallConstraintViolation = new OverallConstraintViolation<>();
		solutionQuality = new SolutionQuality();
	}

	// Constructor (normal)
	public NextReleaseProblem(List<Feature> features, List<Employee> employees, IterationParameters iterationParam) {
		this();

		this.employees = employees;
		this.nbWeeks = iterationParam.getNumberOfWeek();
		this.nbHoursByWeek = iterationParam.getHoursByWeek();

		// TODO: If a feature is not included because 1. lack of skills or 2. the dependee is not included; this information should be noted somewhere and send back to the controller once the plan is produced).
		// checks that features can be satisfied by the skills of the resources and the dependencies are included
		for (Feature feature : features)
			if (getSkilledEmployees(feature.getRequiredSkills()).size() > 0) // 1.
				if (features.containsAll(feature.getPreviousFeatures())) // 2.
					this.features.add(feature);

		worstEndDate = nbWeeks * nbHoursByWeek;

		initializeWorstScore();
		initializeNumberOfConstraint();
	}

	// Constructor (with previous plan)
	public NextReleaseProblem(List<Feature> features, List<Employee> employees, IterationParameters iterationParam, PlanningSolution previousSolution) {
		this(features, employees, iterationParam);
		this.previousSolution = previousSolution;
	}

	// Initializes the worst score
	private void initializeWorstScore() {
		worstScore = 0.0;
		for (Feature feature : features)
			worstScore += feature.getPriority().getScore();
	}

	// Initializes the number of constraints for the problem
	private void initializeNumberOfConstraint() {
		int numberOfConstraints = 0;

		// 1 for each dependency
		for (Feature feature : features)
			numberOfConstraints += feature.getPreviousFeatures().size();

		precedenceConstraintOverall = 1.0 / numberOfConstraints;

		// 1 for passing the deadline of the release
		numberOfConstraints++;

		setNumberOfConstraints(numberOfConstraints);
	}

	@Override
	public PlanningSolution createSolution() {
		return new PlanningSolution(this);
	}

	@Override
	public void evaluate(PlanningSolution solution) {
		if (USE_NEW_EVALUATION)
			evaluateNew(solution);
		else
			evaluateOld(solution);
	}

	/*
	public void evaluate(PlanningSolution solution) {
	    Map<Employee, List<EmployeeWeekAvailability>> s1 = evaluateOld(solution);
	    Map<Employee, List<EmployeeWeekAvailability>> s2 = evaluate2(solution);


	    double count = 0.0;
        double coincidences = 0.0;
	    for (Map.Entry<Employee, List<EmployeeWeekAvailability>> entry : s1.entrySet()) {
	        Employee e = entry.getKey();
	        List<EmployeeWeekAvailability> weeksS1 = entry.getValue();
	        List<EmployeeWeekAvailability> weeksS2 = s2.get(e);

	        for (int i = 0; i < weeksS1.size(); ++i) {
	            for (PlannedFeature pf : weeksS1.get(i).getPlannedFeatures()) {
	                EmployeeWeekAvailability weekS2 = weeksS2.get(i);
                    if (weekS2.getPlannedFeatures().contains(pf)) {
                        ++coincidences;
                    }
                    ++count;
                }
            }
        }

        double similarity = coincidences/count;

    }
    */


	// TODO: read
	/*
	     OK, I think I understand it now. The way it creates the schedule is wrong, but it doesn't affect the algorithm
	     result that much because evaluateConstraints() is called right after evaluate() and gives a bad quality to the
	     wrong ones. It will eventually get to a valid solution (most likely), but only based on the randomness of
	     the algorithm and not because evaluate() is doing a good job.
	*/
	public void evaluateOld(PlanningSolution solution) {
		double newBeginHour;
		double endPlanningHour = 0.0;
		Map<Employee, List<EmployeeWeekAvailability>> employeesTimeSlots = new HashMap<>();
		List<PlannedFeature> plannedFeatures = solution.getPlannedFeatures();

		solution.resetHours();

		// TODO: I'm not sure that this is part of the evaluation, I think this is part of the solution creation.
		// NOTE this for is to build the employeesTimeSlots and (by the way, calculate the endPlanningHour).
		for (PlannedFeature currentPlannedFeature : plannedFeatures) {
			newBeginHour = 0.0;
			Feature currentFeature = currentPlannedFeature.getFeature();

			// Checks the dependencies end hour
			// TODO: this part assumes that dependee features have been processed before. I.e., the order matters.
			// NOTE: the order of the list of features seems to be the other in which the features are implemented in the timeline.

			for (Feature previousFeature : currentFeature.getPreviousFeatures()) {
				PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previousFeature);
				if (previousPlannedFeature != null) {
					// TODO: this part assumes that hours are absolute values. But they are relative to the availability of the resource.
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

			// TODO: this should be currentEmployee.getWeekAvailability() instead of nbHoursByWeek because the current week of the employee depends on his availability.
			currentWeek = ((int) newBeginHour) / (int)nbHoursByWeek;

			// TODO: this should be currentEmployee.getWeekAvailability() instead of nbHoursByWeek
			// TODO: is this redundant? I think it will never enter.
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

		// TODO From here to the end is the evaluation of the solution.
		solution.setObjective(INDEX_PRIORITY_OBJECTIVE, solution.getPriorityScore());
		// TODO: Not urgent, but I think this needs to be calculated in a different way. We are setting the worstEndDate to a 0 planned features solution to let it with the worse overall quality.
		solution.setObjective(INDEX_END_DATE_OBJECTIVE, plannedFeatures.size() == 0 ? worstEndDate : endPlanningHour);

		// TODO: maybe these values can be the values of the objectives.
		double endDateQuality = 1.0 - (solution.getObjective(INDEX_END_DATE_OBJECTIVE) / worstEndDate);
		double priorityQuality = 1.0 - (solution.getObjective(INDEX_PRIORITY_OBJECTIVE) / worstScore);

		// TODO: I'm not sure that this is used for anything. It seems that the PlanningSolutionDominanceComparator does it by itself
		solutionQuality.setAttribute(solution, (endDateQuality + priorityQuality) / 2);
    }



	public void evaluateNew(PlanningSolution solution) {
		Map<Employee, Schedule> schedule = new HashMap<>();
		List<PlannedFeature> plannedFeatures = solution.getPlannedFeatures();

		solution.resetHours();

		/* Both of these seem to perform pretty well */
		computeHours(solution);
        //computeHoursRecursive(solution);

        for (PlannedFeature currentPlannedFeature : plannedFeatures) {

            // Causes a drop in planned features if called from here
            //computeHours(solution, currentPlannedFeature);

            // Seems to cause overlaps if called from here
            computeHoursRecursive(solution, currentPlannedFeature);

			Employee employee = currentPlannedFeature.getEmployee();

			Schedule employeeSchedule = schedule.getOrDefault(employee, new Schedule(employee, nbWeeks));

			if (!employeeSchedule.contains(currentPlannedFeature)) {
                if (!employeeSchedule.scheduleFeature(currentPlannedFeature)) {
                    solution.unschedule(currentPlannedFeature);
                }
            } else {
			    throw new IllegalStateException(".");
            }

			schedule.put(employee, employeeSchedule);
        }

        double endHour = 0.0;
		Map<Employee, List<EmployeeWeekAvailability>> employeesTimeSlots = new HashMap<>();
		for (Map.Entry<Employee, Schedule> entry : schedule.entrySet()) {
			Employee e = entry.getKey();
			Schedule s = entry.getValue();

			List<EmployeeWeekAvailability> weeks = s.getAllWeeks();
			for (EmployeeWeekAvailability week : weeks)
			    for (PlannedFeature pf : week.getPlannedFeatures())
			        endHour = Math.max(endHour, pf.getEndHour());

			employeesTimeSlots.put(e, weeks);
		}

		solution.setEmployeesPlanning(employeesTimeSlots);
		solution.setEndDate(endHour);


		// TODO From here to the end is the evaluation of the solution.
		solution.setObjective(INDEX_PRIORITY_OBJECTIVE, solution.getPriorityScore());
		// TODO: Not urgent, but I think this needs to be calculated in a different way. We are setting the worstEndDate to a 0 planned features solution to let it with the worse overall quality.
		solution.setObjective(INDEX_END_DATE_OBJECTIVE, plannedFeatures.size() == 0 ? worstEndDate : endHour);

		// TODO: maybe these values can be the values of the objectives.
		double endDateQuality = 1.0 - (solution.getObjective(INDEX_END_DATE_OBJECTIVE) / worstEndDate);
		double priorityQuality = 1.0 - (solution.getObjective(INDEX_PRIORITY_OBJECTIVE) / worstScore);

		// TODO: I'm not sure that this is used for anything. It seems that the PlanningSolutionDominanceComparator does it by itself
		solutionQuality.setAttribute(solution, (endDateQuality + priorityQuality) / 2);

	}

	private void computeHoursRecursive(PlanningSolution solution, PlannedFeature pf) {
		Feature feature = pf.getFeature();
		if (feature.getPreviousFeatures().isEmpty()) {
            pf.setEndHour(feature.getDuration());
		} else {
		    double newBeginHour = 0.0;
			for (Feature previous : feature.getPreviousFeatures()) {
				PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previous);

				if (previousPlannedFeature != null) {
					computeHoursRecursive(solution, previousPlannedFeature);
					newBeginHour = Math.max(newBeginHour, previousPlannedFeature.getEndHour());
				}
			}
			pf.setBeginHour(newBeginHour);
            pf.setEndHour(pf.getBeginHour() + feature.getDuration());
		}
	}

	private void computeHoursRecursive(PlanningSolution solution) {
		for (PlannedFeature pf : solution.getPlannedFeatures())
			computeHoursRecursive(solution, pf);
	}

	private void computeHours(PlanningSolution solution) {
        for (PlannedFeature pf : solution.getPlannedFeatures())
            computeHours(solution, pf);
    }

	private void computeHours(PlanningSolution solution, PlannedFeature pf) {
		double newBeginHour = pf.getBeginHour();
		Feature feature = pf.getFeature();
		// newBeginHour = maximum end hour of all previous features
		for (Feature previousFeature : feature.getPreviousFeatures()) {
			PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previousFeature);
			if (previousPlannedFeature != null) {
				newBeginHour = Math.max(newBeginHour, previousPlannedFeature.getEndHour());
			}
		}

		pf.setBeginHour(newBeginHour);
		pf.setEndHour(newBeginHour + feature.getDuration());
	}

	@Override
	public void evaluateConstraints(PlanningSolution solution) {
		int precedencesViolated = 0;
		int violatedConstraints;
		double overall;

		for (PlannedFeature currentFeature : solution.getPlannedFeatures()) {
		    // Use this loop to clear the previous reports
            currentFeature.getFeature().getReport().clear();

			/* Ignore precedence constraint if the planned feature is frozen in the previous plan */
			if (	previousSolution != null &&
					previousSolution.findPlannedFeature(currentFeature.getFeature()) != null &&
					previousSolution.getPlannedFeatures().contains(currentFeature))
			{
                currentFeature.getFeature().addInfo("Ignored precedence constraint because feature is frozen");
			    continue;
            }

			for (Feature previousFeature : currentFeature.getFeature().getPreviousFeatures()) {
				PlannedFeature previousPlannedFeature = solution.findPlannedFeature(previousFeature);
				if (previousPlannedFeature == null || previousPlannedFeature.getEndHour() > currentFeature.getBeginHour()) {
                    currentFeature.getFeature().addError(
                            String.format("Precedence violated: Feature %s should be done before", previousFeature.getName()));
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

		// Check if the employees assigned to the planned features have the required skills
		for (PlannedFeature plannedFeature : solution.getPlannedFeatures()) {
			List<Skill> featureSkills = plannedFeature.getFeature().getRequiredSkills();
			List<Skill> employeeSkills = plannedFeature.getEmployee().getSkills();
			for (Skill featureSkill : featureSkills) {
				boolean hasSkill = false;
				for (Skill employeeSkill : employeeSkills) {
					if (featureSkill.equals(employeeSkill)) {
						hasSkill = true;
						break;
					}
				}
				if (!hasSkill) {
				    plannedFeature.getFeature().addError("Assigned employee does not have the required skills");
					violatedConstraints++;
					overall -= 1.0;
				}
			}
		}

		if (previousSolution != null) {
			Map<Feature, Employee> previousFeatures = new HashMap<>();

			for (PlannedFeature pf : previousSolution.getPlannedFeatures()) {
				previousFeatures.put(pf.getFeature(), pf.getEmployee());

				/* Frozen jobs constraint */
				if (pf.isFrozen() && !solution.getPlannedFeatures().contains(pf)) {
				    pf.getFeature().addError("Frozen feature should not be replanned");
					violatedConstraints++;
					overall -= 1.0;
				}
			}

			/* Penalize for every feature that was already planned but was assigned another resource */
			for (PlannedFeature pf : solution.getPlannedFeatures()) {
				if (previousFeatures.containsKey(pf.getFeature()) &&
						!previousFeatures.get(pf.getFeature()).equals(pf.getEmployee()))
				{
				    pf.getFeature().addWarning("Feature assigned to a different employee on this replan");
					overall -= 0.1;
				}
			}
		}

		numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
		overallConstraintViolation.setAttribute(solution, overall);
		if (violatedConstraints > 0) {
			solutionQuality.setAttribute(solution, 0.0);
		}
	}
}
