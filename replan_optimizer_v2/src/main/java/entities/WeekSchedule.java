package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the week planning of an employee
 * @author Vavou
 *
 */
public class WeekSchedule {

	// The begining hour of the employee in the week
	private double beginHour;
	
	// The remaining hours of the employee
	private double remainingHours;
	
	// The ending hour of the employee in the week
	private double endHour;
	
	// The features done during the week
	private List<PlannedFeature> plannedFeatures;
	
	
	/* --- Getters and Setters --- */
	
	/**
	 * @return the remainingHours
	 */
	public double getRemainingHours() {
		return remainingHours;
	}

	/**
	 * @param remainingHours the remainingHours to set
	 */
	public void setRemainingHours(double remainingHours) {
		this.remainingHours = remainingHours;
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

	public void setBeginHour(double hour) { beginHour = hour; }
	
	/**
	 * @return the plannedFeatures
	 */
	public List<PlannedFeature> getPlannedFeatures() {
		return plannedFeatures;
	}

	/**
	 * @param plannedFeature the plannedFeature to set
	 */
	public void addPlannedFeature(PlannedFeature plannedFeature) {
		this.plannedFeatures.add(plannedFeature);
	}

	
	/* --- Constructors --- */

	/**
	 * Constructor
	 * @param beginHour the begin hour of the employee in the week
	 * @param remainingHours the number of hours the employee can do in the week
	 */
	public WeekSchedule(double beginHour, double remainingHours) {
		this.beginHour = beginHour;
		this.remainingHours = remainingHours;
		endHour = beginHour;
		this.plannedFeatures = new ArrayList<>();
	}
	
	/**
	 * Copy constructor
	 * @param origin the object to copy
	 */
	public WeekSchedule(WeekSchedule origin) {
		this.beginHour = origin.getBeginHour();
		this.remainingHours = origin.getRemainingHours();
		this.endHour = origin.getEndHour();
		this.plannedFeatures = new ArrayList<>(origin.getPlannedFeatures().size());
		for (PlannedFeature plannedFeature : origin.getPlannedFeatures()) {
			this.plannedFeatures.add(new PlannedFeature(plannedFeature));
		}
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekSchedule that = (WeekSchedule) o;

        if (Double.compare(that.beginHour, beginHour) != 0) return false;
        if (Double.compare(that.remainingHours, remainingHours) != 0) return false;
        if (Double.compare(that.endHour, endHour) != 0) return false;
        return plannedFeatures != null ? plannedFeatures.equals(that.plannedFeatures) : that.plannedFeatures == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(beginHour);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(remainingHours);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(endHour);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (plannedFeatures != null ? plannedFeatures.hashCode() : 0);
        return result;
    }
}
