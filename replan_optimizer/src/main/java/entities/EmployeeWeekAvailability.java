package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the week planning of an employee
 * @author Vavou
 *
 */
public class EmployeeWeekAvailability {

	/**
	 * The begin hour of the employee in the week
	 */
	private double beginHour;
	
	/**
	 * The remain hours of the employee
	 */
	private double remainHoursAvailable;
	
	/**
	 * The end hour of the employee in the week
	 */
	private double endHour;
	
	/**
	 * The features done during the week
	 */
	private List<PlannedFeature> plannedFeatures;
	
	
	/* --- Getters and Setters --- */
	
	/**
	 * @return the remainHoursAvailable
	 */
	public double getRemainHoursAvailable() {
		return remainHoursAvailable;
	}

	/**
	 * @param remainHoursAvailable the remainHoursAvailable to set
	 */
	public void setRemainHoursAvailable(double remainHoursAvailable) {
		this.remainHoursAvailable = remainHoursAvailable;
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
	 * @return the beginHour
	 */
	public double getBeginHour() {
		return beginHour;
	}
	
	/**
	 * @return the plannedFeatures
	 */
	public List<PlannedFeature> getPlannedFeatures() {
		return plannedFeatures;
	}

	/**
	 * @param plannedFeatures the plannedFeatures to set
	 */
	public void addPlannedFeature(PlannedFeature plannedFeature) {
		this.plannedFeatures.add(plannedFeature);
	}

	
	/* --- Constructors --- */

	/**
	 * Constructor
	 * @param beginHour the begin hour of the employee in the week
	 * @param remainHoursAvailable the number of hours the employee can do in the week
	 */
	public EmployeeWeekAvailability(double beginHour, double remainHoursAvailable) {
		this.beginHour = beginHour;
		this.remainHoursAvailable = remainHoursAvailable;
		endHour = beginHour;
		this.plannedFeatures = new ArrayList<>();
	}
	
	/**
	 * Copy constructor
	 * @param origin the object to copy
	 */
	public EmployeeWeekAvailability(EmployeeWeekAvailability origin) {
		this.beginHour = origin.getBeginHour();
		this.remainHoursAvailable = origin.getRemainHoursAvailable();
		this.endHour = origin.getEndHour();
		this.plannedFeatures = new ArrayList<>(origin.getPlannedFeatures().size());
		for (PlannedFeature plannedFeature : origin.getPlannedFeatures()) {
			this.plannedFeatures.add(new PlannedFeature(plannedFeature));
		}
	}
}
