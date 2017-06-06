package entities;

/**
 * Describes a feature in a planning
 * Contains 
 * - the feature to do
 * - the employee in charge of the feature
 * - the begin hour in the planning
 * - the end hour in the planning
 * @author Vavou
 *
 */
public class PlannedFeature {
	
	/* --- Attributes --- */
	
	/**
	 * The begin hour of the planned feature
	 */
	private double beginHour;
	
	/**
	 * The employee who will do the feature
	 */
	private Employee employee;
	
	/**
	 * The end hour of the planned feature
	 */
	private double endHour;
	
	/**
	 * The feature to do
	 */
	private Feature feature;
	
	
	/* --- Getters and setters --- */


	/**
	 * @return the beginHour
	 */
	public double getBeginHour() {
		return beginHour;
	}

	/**
	 * @param beginHour the beginHour to set
	 */
	public void setBeginHour(double beginHour) {
		this.beginHour = beginHour;
	}

	/**
	 * @return the employee
	 */
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * @param employee the employee to set
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * @return the endHour
	 */
	public double getEndHour() {
		return endHour;
	}

	/**
	 * @param endHour the endHour to set
	 */
	public void setEndHour(double endHour) {
		this.endHour = endHour;
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
	
	/* --- Constructors --- */

	/**
	 * Construct a planned feature
	 * @param feature the feature to plan
	 * @param employee the employee who realize the feature
	 */
	public PlannedFeature(Feature feature, Employee employee) {
		this.feature = feature;
		this.employee = employee;
		beginHour = 0.0;
	}
	
	/**
	 * Copy constructor
	 * @param origin the object to copy
	 */
	public PlannedFeature(PlannedFeature origin) {
		this.employee = origin.getEmployee();
		this.beginHour = origin.getBeginHour();
		this.feature = origin.getFeature();
		this.endHour = origin.getEndHour();
	}
	
	/* --- Methods --- */
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		PlannedFeature other = (PlannedFeature) obj;

		return other.getFeature().equals(this.getFeature()) &&
				other.getEmployee().equals(this.getEmployee()) &&
				other.getBeginHour() == this.getBeginHour() && 
				other.getEndHour() == this.getEndHour();
	}
	
	@Override
	public int hashCode() {
		return new Double(getBeginHour()).intValue();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getFeature()).append(" done by ").append(getEmployee())
			.append(" from ").append(getBeginHour()).append(" to ").append(getEndHour());
		
		return sb.toString();
	}
}
