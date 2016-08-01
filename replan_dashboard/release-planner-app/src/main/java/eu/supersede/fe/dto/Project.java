package eu.supersede.fe.dto;

import java.util.Set;

public class Project {

	private String name;
	private String description;
	private Set<Resource> resources;
	private String effort_unit; // e.g "bin"
	private float hours_per_effort_unit;
	private float hours_per_week_and_full_time_resource;
	
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
	public Set<Resource> getResources() {
		return resources;
	}
	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
	public String getEffort_unit() {
		return effort_unit;
	}
	public void setEffort_unit(String effort_unit) {
		this.effort_unit = effort_unit;
	}
	public float getHours_per_effort_unit() {
		return hours_per_effort_unit;
	}
	public void setHours_per_effort_unit(float hours_per_effort_unit) {
		this.hours_per_effort_unit = hours_per_effort_unit;
	}
	public float getHours_per_week_and_full_time_resource() {
		return hours_per_week_and_full_time_resource;
	}
	public void setHours_per_week_and_full_time_resource(float hours_per_week_and_full_time_resource) {
		this.hours_per_week_and_full_time_resource = hours_per_week_and_full_time_resource;
	}
	
	
	
}
