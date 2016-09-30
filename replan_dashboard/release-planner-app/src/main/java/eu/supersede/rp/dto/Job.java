package eu.supersede.rp.dto;

import java.util.Date;
import java.util.Set;

public class Job {

	private Resource resource;
	private Feature feature;
	private Set<Job> depends_on;
	private Date starts;
	private Date ends;
	
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	public Set<Job> getDepends_on() {
		return depends_on;
	}
	public void setDepends_on(Set<Job> depends_on) {
		this.depends_on = depends_on;
	}
	public Date getStarts() {
		return starts;
	}
	public void setStarts(Date starts) {
		this.starts = starts;
	}
	public Date getEnds() {
		return ends;
	}
	public void setEnds(Date ends) {
		this.ends = ends;
	}
	
	
}
