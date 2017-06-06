package entities;

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
