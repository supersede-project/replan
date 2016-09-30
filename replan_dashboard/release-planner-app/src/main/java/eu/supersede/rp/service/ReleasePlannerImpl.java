package eu.supersede.rp.service;

import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import eu.supersede.rp.dto.Feature;
import eu.supersede.rp.dto.NewReleaseData;
import eu.supersede.rp.dto.NewResourceData;
import eu.supersede.rp.dto.NewSkillData;
import eu.supersede.rp.dto.Plan;
import eu.supersede.rp.dto.Project;
import eu.supersede.rp.dto.Release;
import eu.supersede.rp.dto.Resource;
import eu.supersede.rp.dto.Skill;
import eu.supersede.rp.dto.Status;
import eu.supersede.rp.main.ReleasePlanner;

@Service ("releasePlanner")
public class ReleasePlannerImpl implements ReleasePlanner{

	@Override
	public SortedSet<Feature> getPendingFeatures(long projec_id) {
		SortedSet<Feature> pendingFeatures = new TreeSet<>();
		for (int i = 0; i < 10; i++) {
			Feature f = new Feature();
//			private long id;
//			private String code;
//			private String name;
//			private String description;
//			private float effort;
//			private Set<Skill> required_skills;
//			private Date deadline;
//			private Set<Feature> depends_on;
//			private int priority;
		
			f.setId(i);
			f.setCode("code"+i);
			f.setName("feature"+i);
			f.setDescription("description"+i);
			pendingFeatures.add(f);
		}
		
		return pendingFeatures;
	}

	@Override
	public SortedSet<Release> getReleases(long project_id) {
		SortedSet<Release> releases = new TreeSet<>();
		for (int i = 0; i < 3; i++) {
			Release r = new Release();
//			private long id;
//			private String code;
//			private String description;
//			private Date deadline;
//			private Set<Resource> resources;
			
			r.setId(i);
			r.setCode("code"+i);
			r.setDescription("release"+i);
			
			releases.add(r);
		}
		return releases;
	}

	@Override
	public Feature getFeature(long project_id, long feature_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Skill> getProjectSkills(long project_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Feature> getReleaseFeatures(long project_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyFeature(long project_id, Feature feature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Status addFeatureToRelease(long project_id, long feature_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelLastReleasePlan(long project_id, long release_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Plan getReleasePlan(long project_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFeatureFromRelease(long project_id, long feature_id, long release_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SortedSet<Resource> getProjectResources(long project_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status modifyRelease(long project_id, Release release) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Release addNewReleaseToProject(long project_id, NewReleaseData nrd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status deleteRelease(long project_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project getProject(long project_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyProject(long project_id, Project project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyResource(long project_id, Resource resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Resource addNewResourceToProject(long project_id, NewResourceData nrd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteResource(long project_id, long resource_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Skill addNewSkillToProject(long project_id, NewSkillData nsd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifySkill(long project_id, Skill skill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteSkill(long project_id, long skill_id) {
		// TODO Auto-generated method stub
		
	}

}
