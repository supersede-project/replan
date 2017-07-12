/**
 * @author Vavou
 */
package logic;

import entities.*;
import entities.parameters.AlgorithmParameters;
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

	private static final boolean USE_NEW_EVALUATION = true;

	private static final long serialVersionUID = 3302475694747789178L; // Generated Id

    public final static int INDEX_PRIORITY_OBJECTIVE = 0; // The index of the priority score objective in the objectives list
	public final static int INDEX_END_DATE_OBJECTIVE = 1; // The index of the end date objective in the objectives list
	public final static int INDEX_DISTRIBUTION_OBJECTIVE = 2;

	// PROBLEM
	private List<Feature> features;
	private List<Employee> employees;
	private PlanningSolution previousSolution;
	private int nbWeeks; // The number of weeks of the iteration
	private double nbHoursByWeek; // The number of worked hours by week
	private AlgorithmParameters algorithmParameters;

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
	public List<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	public int getNbWeeks() {
		return nbWeeks;
	}
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
	public List<Employee> getEmployees() {
		return employees;
	}
	public double getWorstScore() {
		return worstScore;
	}

	public AlgorithmParameters getAlgorithmParameters() { return algorithmParameters; }
	public void setAlgorithmParameters(AlgorithmParameters algorithmParameters) { this.algorithmParameters = algorithmParameters; }

	// Constructor (empty)
	public NextReleaseProblem() {
		setName("Next Release Problem");
		setNumberOfVariables(1);
		setNumberOfObjectives(3);
		features = new ArrayList<>();
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
		overallConstraintViolation = new OverallConstraintViolation<>();
		solutionQuality = new SolutionQuality();
		algorithmParameters = new AlgorithmParameters(SolverNRP.AlgorithmType.NSGAII);
	}

	// Constructor (normal)
	public NextReleaseProblem(List<Feature> features, List<Employee> employees, int nbWeeks, double nbHoursPerWeek) {
		this();

		this.employees = employees;
		this.nbWeeks = nbWeeks;
		this.nbHoursByWeek = nbHoursPerWeek;

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
	public NextReleaseProblem(List<Feature> features, List<Employee> employees,
							  int nbWeeks, double nbHoursPerWeek, PlanningSolution previousSolution) {
		this(features, employees, nbWeeks, nbHoursPerWeek);
		this.previousSolution = previousSolution;
	}

	// Copy constructor
	public NextReleaseProblem(NextReleaseProblem origin) {
		this(origin.getFeatures(), origin.getEmployees(), origin.getNbWeeks(), origin.getNbHoursByWeek());
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
		Map<Employee, Schedule> planning = new HashMap<>();
		List<PlannedFeature> plannedFeatures = solution.getPlannedFeatures();

		solution.resetHours();

        for (PlannedFeature currentPlannedFeature : plannedFeatures) {
            computeHours(solution, currentPlannedFeature);

			Employee employee = currentPlannedFeature.getEmployee();
			Schedule employeeSchedule = planning.getOrDefault(employee, new Schedule(employee, nbWeeks, nbHoursByWeek));

			if (!employeeSchedule.contains(currentPlannedFeature))
                if (!employeeSchedule.scheduleFeature(currentPlannedFeature))
                    solution.unschedule(currentPlannedFeature);

			planning.put(employee, employeeSchedule);
        }

        double endHour = 0.0;
        for (Schedule schedule : planning.values())
            for (PlannedFeature pf : schedule.getPlannedFeatures())
                endHour = Math.max(endHour, pf.getEndHour());

		solution.setEmployeesPlanning(planning);
		solution.setEndDate(endHour);


		/* Objectives and quality evaluation */
		SolutionEvaluator evaluator = SolutionEvaluator.getInstance();

		solution.setObjective(INDEX_PRIORITY_OBJECTIVE, evaluator.priorityObjective(solution));
        solution.setObjective(INDEX_END_DATE_OBJECTIVE, evaluator.endDateObjective(solution));
        //solution.setObjective(INDEX_DISTRIBUTION_OBJECTIVE, evaluator.distributionObjective(solution));

		solutionQuality.setAttribute(solution, evaluator.quality(solution));
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

			/* Ignore precedence constraint if the planned feature is frozen in the previous plan */
			if (	previousSolution != null &&
					previousSolution.findPlannedFeature(currentFeature.getFeature()) != null &&
					previousSolution.getPlannedFeatures().contains(currentFeature))
			{
			    continue;
            }

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
					violatedConstraints++;
					overall -= 1.0;
				}
			}

			/* Penalize for every feature that was already planned but was assigned another resource */
			for (PlannedFeature pf : solution.getPlannedFeatures()) {
				if (previousFeatures.containsKey(pf.getFeature()) &&
						!previousFeatures.get(pf.getFeature()).equals(pf.getEmployee()))
				{
					overall -= 0.1;
				}
			}
		}

		numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
		overallConstraintViolation.setAttribute(solution, overall);
		if (violatedConstraints > 0)
			solutionQuality.setAttribute(solution, 0.0);
	}
}
