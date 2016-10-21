package entities;

/**
 * A skill to execute a task
 * Posseed by employees
 * @author Vavou
 *
 */
public class Skill {
	
	/* --- Atributes --- */
	
	/**
	 * The name of the Skill
	 */
	private String name;
	
	
	/* --- Getters and setters --- */
	
	/**
	 * @return the name of the skill
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the skill
	 * @param name the new name of the skill, can't be null
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}
	
	
	/* --- Constructors --- */

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
