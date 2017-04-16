package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a feature of the Next Release Problem
 * @author Vavou
 *
 */
public class Feature {

	/* --- Atributes --- */

	/**
	 * The deadline of the feature
	 */
	private LocalDateTime deadline;

    /**
     * A static feature will NOT be planned, such as one whose deadline has already passed.
     */
	private boolean isStatic;
	
	/**
	 * The name of the feature
	 */
	private String name;
	
	/**
	 * The priority of the feature
	 */
	private PriorityLevel priority;
	
	/**
	 * The duration of the feature in hours
	 */
	private double duration;
	
	/**
	 * The features which needed to be executed before
	 */
	private List<Feature> previousFeatures;
	
	/**
	 * The skills required to do the feature
	 */
	private List<Skill> requiredSkills;

	
	/* --- Getters and setters --- */

    /**
     * @return the deadline of the feature
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

	/**
	 * @return the name of the feature
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the priority of the feature
	 */
	public PriorityLevel getPriority() {
		return priority;
	}

	/**
	 * @return the duration of the feature
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @return the previous features needed to be finished
	 */
	public List<Feature> getPreviousFeatures() {
		return previousFeatures;
	}
	
	/**
	 * @return the requiredSkills
	 */
	public List<Skill> getRequiredSkills() {
		return requiredSkills;
	}

    /**
     * @return a boolean indicating wether the feature is static. A static feature will NOT be planned, such as one whose deadline has already passed.
     */
    public boolean isStatic() { return isStatic; }

    public void setStatic(boolean b) { isStatic = b; }

	
	
	/* --- Constructors --- */
	
	/**
	 * Construct a feature
	 * @param name the name of the feature
	 * @param priority the priority of the feature
	 * @param duration the duration of the feature
	 * @param previousFeatures the list of the previous features or null
	 * @param requiredSkills the required skills to do this feature
	 */
	public Feature(String name, PriorityLevel priority, Double duration, List<Feature> previousFeatures, List<Skill> requiredSkills) {
		this.name = name;
		this.priority = priority;
		this.duration = duration;
		this.previousFeatures = previousFeatures == null ? new ArrayList<Feature>() : previousFeatures;
		this.requiredSkills = requiredSkills == null ? new ArrayList<Skill>() : requiredSkills;
		isStatic = false;
	}


    /**
     * Constructs a feature with a deadline
     * @param name the name of the feature
     * @param priority the priority of the feature
     * @param duration the duration of the feature
     * @param previousFeatures the list of the previous features or null
     * @param requiredSkills the required skills to do this feature
     */
    public Feature(String name, PriorityLevel priority, Double duration, List<Feature> previousFeatures, List<Skill> requiredSkills, LocalDateTime deadline) {
        this.name = name;
        this.priority = priority;
        this.duration = duration;
        this.previousFeatures = previousFeatures == null ? new ArrayList<Feature>() : previousFeatures;
        this.requiredSkills = requiredSkills == null ? new ArrayList<Skill>() : requiredSkills;
        this.deadline = deadline;

        isStatic = deadline.isBefore(LocalDateTime.now()); // TODO: Most likely use UTC time for the comparison
    }

	
	/**
	 * Constructor with only one Skill
	 * @param name name of the feature
	 * @param priority priority of the feature
	 * @param duration duration of the feature
	 * @param previousFeatures the list of the previous features or null
	 * @param requiredSkill the required skill to do the feature
	 */
	public Feature(String name, PriorityLevel priority, Double duration, List<Feature> previousFeatures, Skill requiredSkill) {
		this.name = name;
		this.priority = priority;
		this.duration = duration;
		this.previousFeatures = previousFeatures == null ? new ArrayList<Feature>() : previousFeatures;
		this.requiredSkills = new ArrayList<>();
		requiredSkills.add(requiredSkill);
		isStatic = false;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		Feature other = (Feature) obj;

		return other.getName().equals(this.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().length();
	}
}
