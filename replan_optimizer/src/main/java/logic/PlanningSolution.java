// @author Vavou
package logic;

import entities.Employee;
import entities.EmployeeWeekAvailability;
import entities.Feature;
import entities.PlannedFeature;
import entities.parameters.DefaultAlgorithmParameters;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// A solution of the NRP contains a plannedFeatures list which give the order of the features which are planned
public class PlanningSolution extends AbstractGenericSolution<PlannedFeature, NextReleaseProblem> {

	private static final long serialVersionUID = 615615442782301271L; //Generated Id
	
	private List<PlannedFeature> plannedFeatures; // included features
	private List<Feature> undoneFeatures; // not included features
    private Map<Employee, List<EmployeeWeekAvailability>> employeesPlanning; // The employees' week planning
	private double endDate; // The end hour of the solution. It's updated only when isUpToDate field is true
    private NextReleaseProblem NRP;

    // GETTERS / SETTERS
	public double getEndDate() {
		return endDate;
	}
	public void setEndDate(double endDate) {
		this.endDate = endDate;
	}
	public int getNumberOfPlannedFeatures() {
		return plannedFeatures.size();
	}
	public List<PlannedFeature> getPlannedFeatures() {
		return new ArrayList<>(plannedFeatures);
	}
	public PlannedFeature getPlannedFeature(int position) {
		if (position >= 0 && position < plannedFeatures.size())
			return plannedFeatures.get(position);
		return null;
	}
	public List<PlannedFeature> getEndPlannedFeaturesSubListCopy(int beginPosition) {
		return new ArrayList<>(plannedFeatures.subList(beginPosition, plannedFeatures.size()));
	}
	private List<Feature> getUndoneFeatures() {
		return undoneFeatures;
	}
	public Map<Employee, List<EmployeeWeekAvailability>> getEmployeesPlanning() {
		return employeesPlanning;
	}
	public void setEmployeesPlanning(Map<Employee, List<EmployeeWeekAvailability>> employeesPlanning) {
		this.employeesPlanning = employeesPlanning;
	}

    // constructor (normal)
	public PlanningSolution(NextReleaseProblem problem) {
		super(problem);

        NRP=problem;
	    numberOfViolatedConstraints = 0;

	    initializePlannedFeatureVariables();
	    initializeObjectiveValues();
	}
	
    // constructor (with previous plan)
	public PlanningSolution(NextReleaseProblem problem, List<PlannedFeature> plannedFeatures) {
	    super(problem);

	    NRP=problem;
	    numberOfViolatedConstraints = 0;

	    undoneFeatures = new CopyOnWriteArrayList<Feature>();
		undoneFeatures.addAll(problem.getFeatures());
		this.plannedFeatures = new CopyOnWriteArrayList<PlannedFeature>();
		for (PlannedFeature plannedFeature : plannedFeatures) {
			if (plannedFeature.isFrozen()) this.plannedFeatures.add(plannedFeature);
			else scheduleAtTheEnd(plannedFeature.getFeature(), plannedFeature.getEmployee());
		}
	    initializeObjectiveValues();
	}

    // Copy constructor
	public PlanningSolution(PlanningSolution origin) {
		super(origin.problem);

	    numberOfViolatedConstraints = origin.numberOfViolatedConstraints;
	    NRP=origin.NRP;
	    
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
	
	// Exchange the two features in positions pos1 and pos2
	public void exchange(int pos1, int pos2) {
		if (pos1 >= 0 && pos2 >= 0 && pos1 < plannedFeatures.size() && pos2 < plannedFeatures.size() && pos1 != pos2) {
			PlannedFeature feature1 = plannedFeatures.get(pos1);
			plannedFeatures.set(pos1, new PlannedFeature(plannedFeatures.get(pos2)));
			plannedFeatures.set(pos2, new PlannedFeature(feature1));
		}
	}
	
    // Calculate the sum of the priority of each feature
	public double getPriorityScore() {
		double score = problem.getWorstScore();
		for (PlannedFeature plannedFeature : plannedFeatures)
			score -= plannedFeature.getFeature().getPriority().getScore();
		return score;
	}
	
	// Returns all of the planned features done by a specific employee
	public List<PlannedFeature> getFeaturesDoneBy(Employee e) {
		List<PlannedFeature> featuresOfEmployee = new ArrayList<>();
		for (PlannedFeature plannedFeature : plannedFeatures)
			if (plannedFeature.getEmployee() == e)
				featuresOfEmployee.add(plannedFeature);
		return featuresOfEmployee;
	}

	// Return true if the feature is already in the planned features
	public boolean isAlreadyPlanned(Feature feature) {
		boolean found = false;
		Iterator<PlannedFeature> it = plannedFeatures.iterator();
		
		while (!found && it.hasNext()) {
			PlannedFeature plannedFeature = (PlannedFeature) it.next();
			if (plannedFeature.getFeature().equals(feature))
				found = true;
		}
		return found;
	}

	// Returns the planned feature corresponding to the feature given in parameter
	public PlannedFeature findPlannedFeature(Feature feature) {
		for (PlannedFeature plannedFeature : plannedFeatures)
			if (plannedFeature.getFeature().equals(feature))
				return plannedFeature;
		return null;
	}
	
	// Initialize the variables. Load a random number of planned features
	private void initializePlannedFeatureVariables() {
		int numberOfFeatures = problem.getFeatures().size();
		// TODO: All the solutions will have all the features. This is a temporal solution.
        int nbFeaturesToDo = randomGenerator.nextInt(0, numberOfFeatures);
		//int nbFeaturesToDo = numberOfFeatures;
		
		undoneFeatures = new CopyOnWriteArrayList<Feature>();
		undoneFeatures.addAll(problem.getFeatures());
		plannedFeatures = new CopyOnWriteArrayList<PlannedFeature>();
	
		if (randomGenerator.nextDouble() > DefaultAlgorithmParameters.RATE_OF_NOT_RANDOM_GENERATED_SOLUTION)
			initializePlannedFeaturesRandomly(nbFeaturesToDo);
		else
			initializePlannedFeaturesWithPrecedences(nbFeaturesToDo);
	}

    // Initializes the planned features randomly
    private void initializePlannedFeaturesRandomly(int numFeaturesToPlan) {
        Feature featureToDo;
        List<Employee> skilledEmployees;

        for (int i = 0 ; i < numFeaturesToPlan ; i++) {
            featureToDo = undoneFeatures.get(randomGenerator.nextInt(0, undoneFeatures.size()-1));
            skilledEmployees = problem.getSkilledEmployees(featureToDo.getRequiredSkills());
            scheduleAtTheEnd(featureToDo, skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1)));
        }
    }

	// Initializes the planned features considering the precedences
	private void initializePlannedFeaturesWithPrecedences(int numFeaturesToPlan) {
		Feature featureToDo;
		List<Employee> skilledEmployees;
		List<Feature> possibleFeatures = updatePossibleFeatures();
		int i = 0;
		while (i < numFeaturesToPlan && possibleFeatures.size() > 0) {
			featureToDo = possibleFeatures.get(randomGenerator.nextInt(0, possibleFeatures.size()-1));
			skilledEmployees = problem.getSkilledEmployees(featureToDo.getRequiredSkills());
			scheduleAtTheEnd(featureToDo, skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1)));
			possibleFeatures = updatePossibleFeatures();
			i++;
		}
	}
	
	// Reset the begin hours of all the planned feature to 0.0
	public void resetHours() {
		for (PlannedFeature plannedFeature : plannedFeatures) {
			plannedFeature.setBeginHour(0.0);
			plannedFeature.setEndHour(0.0);
		}
	}

	// Schedule a planned feature to a position in the planning
	public void schedule(int position, Feature feature, Employee e) {
		undoneFeatures.remove(feature);
		plannedFeatures.add(position, new PlannedFeature(feature, e));
	}
		
	// Schedule a feature in the planning
	public void scheduleAtTheEnd(Feature feature, Employee e) {
	    // TODO: is this correct? it assumes that if already planned nothing happens. It may be associated to another employee or is not at the end.
		if (!isAlreadyPlanned(feature)) {
			undoneFeatures.remove(feature);
			plannedFeatures.add(new PlannedFeature(feature, e));
		}

		/* DAVID: my proposal
		if (isAlreadyPlanned(feature)) plannedFeatures.remove(findPlannedFeature(feature));
		else undoneFeatures.remove(feature);
        plannedFeatures.add(new PlannedFeature(feature, e));
        */
	}
	
	// Schedule a random undone feature to a random place in the planning
	public void scheduleRandomFeature() {
		scheduleRandomFeature(randomGenerator.nextInt(0, plannedFeatures.size()));
	}
	
	// Schedule a random feature to insertionPosition of the planning list
	public void scheduleRandomFeature(int insertionPosition) {
		if (undoneFeatures.size() <= 0)
			return;
		Feature newFeature = undoneFeatures.get(randomGenerator.nextInt(0, undoneFeatures.size() -1));
		List<Employee> skilledEmployees = problem.getSkilledEmployees(newFeature.getRequiredSkills());
		Employee newEmployee = skilledEmployees.get(randomGenerator.nextInt(0, skilledEmployees.size()-1));
		schedule(insertionPosition, newFeature, newEmployee);
	}
	
	// Schedule the planned feature at a random position in the planning
	public void scheduleRandomly(PlannedFeature plannedFeature) {
		schedule(randomGenerator.nextInt(0, plannedFeatures.size()), plannedFeature.getFeature(), plannedFeature.getEmployee());
	}

	// Unschedule a feature : remove it from the planned features and add it to the undone ones
	public void unschedule(PlannedFeature plannedFeature) {
		if (isAlreadyPlanned(plannedFeature.getFeature())) {
			undoneFeatures.add(plannedFeature.getFeature());
			plannedFeatures.remove(plannedFeature);
		}
	}

	// Creates a list of the possible features to do regarding to the precedences of the undone features
	private List<Feature> updatePossibleFeatures() {
		List<Feature> possibleFeatures = new ArrayList<>();
		boolean possible;
		int i;
		
		for (Feature feature : undoneFeatures) {
			possible = true;
			i = 0;
			while (possible && i < feature.getPreviousFeatures().size()) {
				if (!isAlreadyPlanned(feature.getPreviousFeatures().get(i)))
					possible = false;
				i++;
			}
			if (possible)
				possibleFeatures.add(feature);
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
	}
	
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
		
		sb.append("End Date: ").append(getEndDate()).append(lineSeparator);
		
		return sb.toString();
	}

	public String toR() {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        List<PlannedFeature> plannedFeatures = getPlannedFeatures();
        List<Employee> resources = getResources();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        final int OneHour = 60 * 60 * 1000;

        sb.append("userData <- list()").append(lineSeparator);

		sb.append(lineSeparator);

        sb.append("  userData$plan <- data.frame(\n" +
                "    id=numeric(), \n" +
                "    content=character(), \n" +
                "    start=character(), \n" +
                "    end=character(), \n" +
                "    group=character(), #resource\n" +
                "    type=character(), \n" +
                "    priority=numeric(), \n" +
                "    effort=numeric(), \n" +
                "    stringsAsFactors=FALSE)").append(lineSeparator);

        sb.append(lineSeparator);

        sb.append("  userData$resources <- data.frame(\n" +
                "    id=character(), #same as group in plan\n" +
                "    content=character(), # display name\n" +
                "    availability=numeric(), \n" +
                "    stringsAsFactors=FALSE)").append(lineSeparator);

        sb.append(lineSeparator);

        sb.append("  userData$features <- data.frame(\n" +
                "    id=character(), #same as content in plan\n" +
                "    content=character(), # display name\n" +
                "    scheduled=character(), # Yes/No\n" +
                "    priority=numeric(), \n" +
                "    effort=numeric(), \n" +
                "    stringsAsFactors=FALSE)").append(lineSeparator);

        sb.append(lineSeparator);

        sb.append("  userData$depGraphEdges <- data.frame(\n" +
                "    node1=character(), \n" +
                "    node2=character(), \n" +
                "    stringsAsFactors=FALSE)").append(lineSeparator);

        sb.append(lineSeparator);

        for (PlannedFeature pf : plannedFeatures)
        	sb      .append("userData$plan[nrow(userData$plan)+1,] <- c(")
                    .append(quote(pf.getFeature().getName())).append(", ") // id
                    .append(quote(pf.getFeature().getName())).append(", ") // content
                    .append(quote(dateFormat.format(new Date(curDate.getTime()+OneHour*(int)pf.getBeginHour())))).append(", ") // start
                    .append(quote(dateFormat.format(new Date(curDate.getTime()+OneHour*(int)pf.getEndHour())))).append(", ") // end
                    .append(quote(pf.getEmployee().getName())).append(", ") // group
                    .append(quote("range")).append(", ") // type
                    .append(pf.getFeature().getPriority().ordinal()+1).append(", ") // priority
                    .append((int)pf.getFeature().getDuration()).append(")").append(lineSeparator); // effort

        sb.append(lineSeparator);

        for (Employee e : resources)
            sb      .append("userData$resources[nrow(userData$resources)+1,] <- c(")
                    .append(quote(e.getName())).append(", ") // id
                    .append(quote(e.getName())).append(", ") // content
                    .append(e.getWeekAvailability()).append(")").append(lineSeparator); // availability

		sb.append(lineSeparator);

        for (Feature f : NRP.getFeatures()) {
            sb.append("userData$features[nrow(userData$features)+1,] <- c(")
                    .append(quote(f.getName())).append(", ") // id
                    .append(quote(f.getName())).append(", ") // id
                    .append(quote(isAlreadyPlanned(f) ? "Yes" : "No")).append(", ") // id
                    .append(f.getPriority().ordinal() + 1).append(", ") // priority
                    .append((int) f.getDuration()).append(")").append(lineSeparator); // effort
            for(Feature d : f.getPreviousFeatures())
                sb.append("userData$depGraphEdges[nrow(userData$depGraphEdges)+1,] <- c(")
                        .append(quote(f.getName())).append(", ")
                        .append(quote(d.getName())).append(")").append(lineSeparator);
        }

        sb.append(lineSeparator);

        sb.append("userData$nWeeks <- ").append(NRP.getNbWeeks()).append(lineSeparator);
        sb.append("userData$nFeatures <- ").append(NRP.getFeatures().size()).append(lineSeparator);


        return sb.toString();
    }

    private String quote(String s) {
	    return "\"".concat(s).concat("\"");
    }

    private List<Employee> getResources() {
	    ArrayList<Employee> employees = new ArrayList<>();
        for (PlannedFeature feature : getPlannedFeatures())
            if(!employees.contains(feature.getEmployee())) employees.add(feature.getEmployee());
        return employees;
    }
}
