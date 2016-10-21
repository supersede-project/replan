package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes an employee who can realize a feature
 * @author Vavou
 *
 */
public class Employee {
	
	/* --- Atributes --- */

	/**
	 * The name of the employee
	 */
	private String name;
	
	/**
	 * The skills of the employee
	 */
	private List<Skill> skills;
	
	/**
	 * The available number of hours per week
	 */
	private double weekAvailability;

	
	/* --- Getters and setters --- */
	
	/**
	 * Returns the name of the employee
	 * @return the name of the employee
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the employee
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the skills of the employee
	 * @return the skills of the employee
	 */
	public List<Skill> getSkills() {
		return skills;
	}

	/**
	 * @param skills the skills to set
	 */
	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	/**
	 * @return the weekAvailability
	 */
	public double getWeekAvailability() {
		return weekAvailability;
	}

	/**
	 * @param weekAvailability the weekAvailability to set
	 */
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
		return getName();
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
