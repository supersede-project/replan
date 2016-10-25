package wrapper;

import entities.*;

import io.swagger.model.*;
import io.swagger.model.Feature;
import io.swagger.model.Skill;

import java.util.ArrayList;
import java.util.List;

public class Transform2SwaggerModel {

    /*Planning solution*/
    public PlanningSolution transformPlanningSolution2Swagger(logic.PlanningSolution planningSolution) {

        List<io.swagger.model.PlannedFeature> pf = PlannedFeatureList2Swagger(planningSolution.getPlannedFeatures());

        PlanningSolution ps = new PlanningSolution();
        ps.setJobs(pf);

        return ps;
    }

    /*Planed feature*/
    public List<io.swagger.model.PlannedFeature> PlannedFeatureList2Swagger(List<entities.PlannedFeature> plannedFeatures) {

        List<io.swagger.model.PlannedFeature> pfList = new ArrayList<>();
        for (entities.PlannedFeature pf: plannedFeatures){
            pfList.add(PlannedFeature2Swagger(pf));
        }
        return pfList;
    }

    public io.swagger.model.PlannedFeature PlannedFeature2Swagger(entities.PlannedFeature pf) {

        io.swagger.model.PlannedFeature plannedFeature = new io.swagger.model.PlannedFeature();
        plannedFeature.setBeginHour(pf.getBeginHour());
        plannedFeature.setEndHour(pf.getEndHour());
        plannedFeature.setFeature(Feature2Swagger(pf.getFeature()));
        plannedFeature.setResource(Employee2Resource(pf.getEmployee()));

        return plannedFeature;
    }


    /*Features*/

    public Feature Feature2Swagger(entities.Feature f) {
        Feature feature = new Feature();
        feature.setDuration(f.getDuration());
        feature.setName(f.getName());
        feature.setPriority(Priority2Swagger(f.getPriority()));
        feature.setDepends_on(FeatureList2Swagger(f.getPreviousFeatures()));
        feature.setRequired_skills(SkillList2Swagger(f.getRequiredSkills()));

        return feature;
    }


    public List<Feature> FeatureList2Swagger(List<entities.Feature> fList) {

        List<Feature> featureList = new ArrayList<>();

        for(entities.Feature feature: fList){
            featureList.add(Feature2Swagger(feature));
        }

        return featureList;
    }

    /*Employee - Resource*/
    public Resource Employee2Resource(Employee employee) {

        Resource resource = new Resource();
        resource.setName(employee.getName());
        resource.setSkills(SkillList2Swagger(employee.getSkills()));
        resource.setAvailability(employee.getWeekAvailability());

        return resource;
    }

    /*Priority*/
    public Priority Priority2Swagger(PriorityLevel pl) {
        Priority priority = new Priority();
        priority.setLevel(pl.getLevel());
        priority.setScore(pl.getScore());
        return priority;
    }

    /*Skill*/
    public Skill Skill2Swagger(entities.Skill skill){

        Skill s = new Skill();
        s.setName(skill.getName());
        return s;
    }

    public List<Skill> SkillList2Swagger(List<entities.Skill> skills){
        List<Skill> skillList = new ArrayList<>();
        for(entities.Skill s: skills){
            skillList.add(Skill2Swagger(s));
        }
        return skillList;
    }



}
