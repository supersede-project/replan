package eu.supersede.fe.rest;

import java.util.Map;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.dto.Feature;
import eu.supersede.fe.dto.NewReleaseData;
import eu.supersede.fe.dto.NewResourceData;
import eu.supersede.fe.dto.NewSkillData;
import eu.supersede.fe.dto.Plan;
import eu.supersede.fe.dto.Project;
import eu.supersede.fe.dto.Release;
import eu.supersede.fe.dto.Resource;
import eu.supersede.fe.dto.Skill;
import eu.supersede.fe.dto.Status;
import eu.supersede.fe.main.ReleasePlanner;
/**
 * 
 * @author z002z80a
 * Questions
 * 
 * 
 * JAVA
 * 1.Where do I declare the variables?
 * 2.How to clear cache in  eclipse?
 * 3.How to mapping  /projects/:project_id/features?status=pending
 * 
 * GENERAL
 * 1. application.unsecured.urls=/user/count,/test
 * 2. how to debug?
 * 3. wp5_application.properties - posso scriver dentro anche costanti?
 * 4. ADMIN in wp5_application.properties
 * 5. devo usare http://www.jqwidgets.com/?
 * 
 * 
 */
@RestController
@RequestMapping("/projects")
public class ReleasePlannerRest /*implements ReleasePlanner*/{

	@Autowired
	ReleasePlanner manager;

	@RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
	@ResponseBody
	public String hello() {

		return "projects says hello ;-)";
	}
	

	// Main screen
	// GET /projects/:project_id/features?status=pending
	//http://localhost:8083/projects/1/features?status=pending		
	@RequestMapping(value = "/{project_id}/features", method = RequestMethod.GET)
	public SortedSet<Feature> getPendingFeatures(@PathVariable("project_id") long project_id, @RequestParam Map<String, String> uriParameters) {

		return manager.getPendingFeatures(project_id);
	}

	// GET /projects/:project_id/releases
	//http://localhost:8083/projects/1/releases
	@RequestMapping(value = "/{project_id}/releases", method = RequestMethod.GET)
	public SortedSet<Release> getReleases(@PathVariable("project_id") long project_id) {
		
		return manager.getReleases(project_id);
	
	}

	// Re-plan a release screen (1)
	// GET /projects/:project_id/features/:feature_id

	public Feature getFeature(long project_id, long feature_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// GET /projects/:project_id/skills

	public SortedSet<Skill> getProjectSkills(long project_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// GET /projects/:project_id/releases/:release_id/features

	public SortedSet<Feature> getReleaseFeatures(long project_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// PUT /projects/:project_id/features/:feature_id

	public void modifyFeature(long project_id, Feature feature) {
		// TODO Auto-generated method stub

	}

	// POST /projects/:project_id/releases/:release_id/features

	public Status addFeatureToRelease(long project_id, long feature_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// Re-plan a release screen (2)
	// DELETE /projects/:project_id/releases/:release_id/plan

	public void cancelLastReleasePlan(long project_id, long release_id) {
		// TODO Auto-generated method stub

	}

	// Release details screen
	// GET /projects/:project_id/releases/:release_id/plan

	public Plan getReleasePlan(long project_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// DELETE /projects/:project_id/releases/:release_id/features/:feature_id

	public void removeFeatureFromRelease(long project_id, long feature_id, long release_id) {
		// TODO Auto-generated method stub

	}

	//New release / release configuration screen
	// GET /projects/:project_id/resources

	public SortedSet<Resource> getProjectResources(long project_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// PUT /projects/:project_id/releases/:release_id

	public Status modifyRelease(long project_id, Release release) {
		// TODO Auto-generated method stub
		return null;
	}

	// POST /projects/:project_id/releases

	public Release addNewReleaseToProject(long project_id, NewReleaseData nrd) {
		// TODO Auto-generated method stub
		return null;
	}

	// DELETE /projects/:project_id/releases/:release_id

	public Status deleteRelease(long project_id, long release_id) {
		// TODO Auto-generated method stub
		return null;
	}

	//Project configuration screen
	// GET /projects/:project_id

	public Project getProject(long project_id) {
		// TODO Auto-generated method stub
		return null;
	}

	// PUT /projects/:project_id

	public void modifyProject(long project_id, Project project) {
		// TODO Auto-generated method stub

	}

	// PUT /projects/:project_id/resources/:resource_id

	public void modifyResource(long project_id, Resource resource) {
		// TODO Auto-generated method stub

	}

	// POST /projects/:project_id/resources

	public Resource addNewResourceToProject(long project_id, NewResourceData nrd) {
		// TODO Auto-generated method stub
		return null;
	}

	// DELETE /projects/:project_id/resources/:resource_id

	public void deleteResource(long project_id, long resource_id) {
		// TODO Auto-generated method stub

	}

	// POST /projects/:project_id/skills

	public Skill addNewSkillToProject(long project_id, NewSkillData nsd) {
		// TODO Auto-generated method stub
		return null;
	}

	// PUT //projects/:project_id/skills/:skill_id

	public void modifySkill(long project_id, Skill skill) {
		// TODO Auto-generated method stub

	}

	// DELETE //projects/:project_id/skills/:skill_id

	public void deleteSkill(long project_id, long skill_id) {
		// TODO Auto-generated method stub

	}

}
