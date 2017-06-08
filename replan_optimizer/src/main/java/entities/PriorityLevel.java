/**
 * 
 */
package entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

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
	

	/* --- GETTERS / SETTERS --- */
	@ApiModelProperty(value = "")
	public int getLevel() {
		return level;
	}

	public void setLevet(int level) { this.level = level; }

	@ApiModelProperty(value = "")
	public int getScore() {
		return score;
	}

	public void setScore(int score) { this.score = score; }


	/* --- CONSTRUCTORS --- */

	/**
	 * Empty constructor for the API
	 */
	PriorityLevel() {}

	/**
	 * Constructor
	 * @param level the level of the priority
	 * @param score the score of the priority
	 */
	PriorityLevel(int level, int score) {
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

	@JsonCreator
	public static PriorityLevel fromValues(
            @JsonProperty("level") int level,
            @JsonProperty("score") int score) {
		return getPriorityByLevel(level);
	}
}
