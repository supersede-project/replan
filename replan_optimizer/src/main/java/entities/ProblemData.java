/**
 * 
 */
package entities;

import java.util.List;

/**
 * Encapsulates the data of a Next Release Problem
 * @author Vavou
 *
 */
public class ProblemData {
	
	/* --- Attributes --- */
	
	/**
	 * List of features
	 */
	private List<Feature> features;
	
	/**
	 * List of employees
	 */
	private List<Employee> employees;
	
	/**
	 * List of skills
	 */
	private List<Skill> skills;

	
	/* --- Getters and setters --- */
	
	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	/**
	 * @return the employees
	 */
	public List<Employee> getEmployees() {
		return employees;
	}

	/**
	 * @param employees the employees to set
	 */
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	/**
	 * @return the skills
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
	
	/* --- Constructors --- */

	/**
	 * Constructor
	 * @param features the features of the problem
	 * @param employees the employees of the problem
	 * @param skills the skills of the problem
	 */
	public ProblemData(List<Feature> features, List<Employee> employees, List<Skill> skills) {
		this.features = features;
		this.employees = employees;
		this.skills = skills;
	}

}
