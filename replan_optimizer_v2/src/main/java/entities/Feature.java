package entities;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a feature of the Next Release Problem
 * @author Vavou
 *
 */
public class Feature {

	/* --- Atributes --- */

	private String name;

	private PriorityLevel priority;

	private double duration;

	@SerializedName("required_skills")
	private List<Skill> requiredSkills = new ArrayList<Skill>();;

	@SerializedName("depends_on")
	private List<Feature> previousFeatures = new ArrayList<Feature>();;

	
	/* --- Getters and setters --- */
	@ApiModelProperty(value = "")
	public String getName() {
		return name;
	}

    @ApiModelProperty(value = "")
	public PriorityLevel getPriority() {
		return priority;
	}

    @ApiModelProperty(value = "")
	public double getDuration() {
		return duration;
	}

    public void setDuration(Double d) {
        duration = d;
    }

    @ApiModelProperty(value = "array of features")
	public List<Feature> getPreviousFeatures() {
		return previousFeatures;
	}

    @ApiModelProperty(value = "array of skills")
	public List<Skill> getRequiredSkills() {
		return requiredSkills;
	}

    /**
     *
     * @param f a feature
     * @return a boolean indicating whether this feature depends on f
     */

    public boolean dependsOn(Feature f) {
        return previousFeatures.contains(f);
    }
	
	
	/* --- Constructors --- */

    public Feature() {}
	
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
		this.previousFeatures = previousFeatures == null ? new ArrayList<>() : previousFeatures;
		this.requiredSkills = requiredSkills == null ? new ArrayList<>() : requiredSkills;
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
		List<String> dependencesNames = new ArrayList<>();
		for (Feature f : getPreviousFeatures())
			dependencesNames.add(f.getName());

		List<String> requiredSkillsNames = new ArrayList<>();
		for (Skill s : getRequiredSkills())
			requiredSkillsNames.add(s.getName());

		return String.format("%s. Required skills: [%s]. Dependences: [%s].",
				getName(), String.join(", ", requiredSkillsNames), String.join(", ", dependencesNames));
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
