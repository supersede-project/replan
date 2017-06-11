package entities;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

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
	 * A frozen feature will NOT be replanned.
	 */
	private boolean frozen;

	/**
	 * The begin hour of the planned feature
	 */
	private double beginHour;

	/**
	 * The employee who will do the feature
	 */
	@SerializedName("resource")
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

	@ApiModelProperty(value = "")
	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	@ApiModelProperty(value = "")
	public double getBeginHour() {
		return beginHour;
	}

	public void setBeginHour(double beginHour) {
		this.beginHour = beginHour;
	}

	@ApiModelProperty(value = "")
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ApiModelProperty(value = "")
	public double getEndHour() {
		return endHour;
	}

	public void setEndHour(double endHour) {
		this.endHour = endHour;
	}

	@ApiModelProperty(value = "")
	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
	
	/* --- Constructors --- */

    public PlannedFeature() {}

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
		return String.valueOf(getFeature()) + " done by " + getEmployee() +
				" from " + getBeginHour() + " to " + getEndHour();
	}
}
