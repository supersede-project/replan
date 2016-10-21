/**
 * 
 */
package entities.parameters;

/**
 * This class encapsulates the iteration parameters
 * @author Vavou
 *
 */
public class IterationParameters {
	
	/* --- Attributes --- */
	
	/**
	 * The number of week of the iteration
	 */
	private int numberOfWeek;
	
	/**
	 * The number of worked hours by week
	 */
	private double hoursByWeek;
	
	
	/* --- Getters and setters --- */
	
	/**
	 * @return the numberOfWeek
	 */
	public int getNumberOfWeek() {
		return numberOfWeek;
	}

	/**
	 * @param numberOfWeek the numberOfWeek to set
	 */
	public void setNumberOfWeek(int numberOfWeek) {
		this.numberOfWeek = numberOfWeek;
	}

	/**
	 * @return the hoursByWeek
	 */
	public double getHoursByWeek() {
		return hoursByWeek;
	}

	/**
	 * @param hoursByWeek the hoursByWeek to set
	 */
	public void setHoursByWeek(double hoursByWeek) {
		this.hoursByWeek = hoursByWeek;
	}
	
	
	/* --- Constructors --- */
	
	/**
	 * Default constructor that construct the parameters with their default values
	 * Default values from <code>DefaultIterationParameters</code> class
	 */
	public IterationParameters() {
		this(DefaultIterationParameters.NUMBER_OF_WEEK, DefaultIterationParameters.HOURS_BY_WEEK);
	}

	/**
	 * Constructor
	 * @param numberOfWeek The number of week of the iteration
	 * @param hoursByWeek The number of worked hours by week
	 */
	public IterationParameters(int numberOfWeek, double hoursByWeek) {
		this.numberOfWeek = numberOfWeek;
		this.hoursByWeek = hoursByWeek;
	}

}
