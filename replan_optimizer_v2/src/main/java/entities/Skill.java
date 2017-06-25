package entities;

import io.swagger.annotations.ApiModelProperty;

/**
 * A skill to execute a task
 * Posseed by employees
 * @author Vavou
 *
 */
public class Skill {
	
	/* --- Atributes --- */

	private String name;
	
	
	/* --- Getters and setters --- */

	@ApiModelProperty(value = "")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Skill name(String name) {
		this.name = name;
		return this;
	}
	
	
	/* --- Constructors --- */

	/**
	 * Empty constructor for the API
	 */
	public Skill() {}

	/**
	 * Constructs a skill
	 * @param name the name of the skill to construct
	 */
	public Skill(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		Skill other = (Skill) obj;

		return other.getName().equals(this.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().length();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
