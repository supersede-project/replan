package entities;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes an employee who can realize a feature
 * @author Vavou
 *
 */
public class Employee {
	
	/* --- Atributes --- */


	private String name;

	private List<Skill> skills;
	
	/**
	 * The available number of hours per week
	 */
	private double weekAvailability;

	
	/* --- Getters and setters --- */

	@ApiModelProperty(value = "")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ApiModelProperty(value = "")
	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	@ApiModelProperty(value = "")
	public double getWeekAvailability() {
		return weekAvailability;
	}

	public void setWeekAvailability(double weekAvailability) {
		this.weekAvailability = weekAvailability;
	}
	
	
	/* --- Constructor --- */
	
	/**
	 * Constructs a new employee
	 * @param name
	 * @param weekAvailability in hours per week
	 */
	public Employee(String name, double weekAvailability, List<Skill> skills) {
		this.name = name;
		this.weekAvailability = weekAvailability;
		this.skills = skills == null ? new ArrayList<Skill>() : skills;
	}
	
	/**
	 * Returns the name of the employee
	 * @return the name of the employee
	 */
	@Override 
	public String toString() {
		List<String> skillNames = new ArrayList<>();
		for (Skill s : getSkills())
			skillNames.add(s.getName());

		return String.format("%s. Skills: [%s].", getName(), String.join(", ", skillNames));
	}
	
	/**
	 * Two employees are equals if they have the same name
	 * @param obj The other object to compare
	 */
	@Override 
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		Employee other = (Employee) obj;
		
		return this.getName().equals(other.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().length();
	}
}
