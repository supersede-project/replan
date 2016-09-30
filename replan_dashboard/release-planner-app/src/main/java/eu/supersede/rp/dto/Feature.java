package eu.supersede.rp.dto;

import java.util.Date;
import java.util.Set;

public class Feature implements Comparable<Feature> {
	
	private long id;
	private String code;
	private String name;
	private String description;
	private float effort;
	private Set<Skill> required_skills;
	private Date deadline;
	private Set<Feature> depends_on;
	private int priority;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getEffort() {
		return effort;
	}
	public void setEffort(float effort) {
		this.effort = effort;
	}
	public Set<Skill> getRequired_skills() {
		return required_skills;
	}
	public void setRequired_skills(Set<Skill> required_skills) {
		this.required_skills = required_skills;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public Set<Feature> getDepends_on() {
		return depends_on;
	}
	public void setDepends_on(Set<Feature> depends_on) {
		this.depends_on = depends_on;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(Feature o) {
		return this.name.compareTo(o.getName());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((deadline == null) ? 0 : deadline.hashCode());
		result = prime * result + ((depends_on == null) ? 0 : depends_on.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + Float.floatToIntBits(effort);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + priority;
		result = prime * result + ((required_skills == null) ? 0 : required_skills.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (deadline == null) {
			if (other.deadline != null)
				return false;
		} else if (!deadline.equals(other.deadline))
			return false;
		if (depends_on == null) {
			if (other.depends_on != null)
				return false;
		} else if (!depends_on.equals(other.depends_on))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (Float.floatToIntBits(effort) != Float.floatToIntBits(other.effort))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (priority != other.priority)
			return false;
		if (required_skills == null) {
			if (other.required_skills != null)
				return false;
		} else if (!required_skills.equals(other.required_skills))
			return false;
		return true;
	}
	
	
	
	
	
}
