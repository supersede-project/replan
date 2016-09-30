package eu.supersede.rp.dto;

import java.util.Set;

public class Plan {

	private long id;
	private Set<Job> jobs;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Set<Job> getJobs() {
		return jobs;
	}
	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}
	
	
	
}
