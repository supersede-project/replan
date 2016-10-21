/**
 * 
 */
package entities;

/**
 * Priority Level of a feature
 * @author Vavou
 *
 */
public enum PriorityLevel {
	ONE(1, 160),
	TWO(2, 80),
	THREE(3, 40),
	FOUR(4, 20),
	FIVE(5, 10);
	
	/**
	 * The priority level
	 */
	private int level;
	
	/**
	 * The score of the priority
	 */
	private int score;
	
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Constructor
	 * @param level the level of the priority
	 * @param score the score of the priority
	 */
	private PriorityLevel(int level, int score) {
		this.level = level;
		this.score = score;
	}

	/**
	 * Return the PriorityLevel of a level
	 * @param level
	 * @return the corresponding PriorityLevel
	 */
	public static PriorityLevel getPriorityByLevel(int level) {
		switch (level) {
			case 1:
				return PriorityLevel.ONE;
			case 2:
				return PriorityLevel.TWO;
			case 3:
				return PriorityLevel.THREE;
			case 4:
				return PriorityLevel.FOUR;
			default:
				return PriorityLevel.FIVE;
		}
	}
}
