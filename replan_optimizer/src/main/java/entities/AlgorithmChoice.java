package entities;

/**
 * The algorithms implemented to solve the Next Release Problem
 * @author Vavou
 *
 */
public enum AlgorithmChoice {
	//GENERATIONAL("Generational GA"), 
	MOCell("MOCell"),
	NSGAII("NSGA-II"),
	PESA2("PESA-II"),
	//SMSEMOA("SMSEMOA"), // Does not evaluate the constraints...
	SPEA2("SPEA-II"),
	//STEADY("Steady State GA") // The evaluator does not consider the constraints...
	;
	
	/**
	 * The name of the algortihm
	 */
	private String name;
	
	/**
	 * The constructor of an algorithm choice
	 * @param name the name of the choice
	 */
	private AlgorithmChoice(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
