package wrapper;


import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import entities.Skill;
import entities.parameters.IterationParameters;
import logic.NextReleaseProblem;
import logic.PlanningSolution;

import java.util.ArrayList;
import java.util.List;


public class EntitiesEvaluator {
    private static EntitiesEvaluator ourInstance = new EntitiesEvaluator();

    public static EntitiesEvaluator getInstance() {
        return ourInstance;
    }

    private EntitiesEvaluator() {
    }



    public NextReleaseProblem nextReleaseProblemAddSkills(int nbWeeks, Number hoursPerweek,
                                                          List<Feature> features, List<Employee> employees) {

        NextReleaseProblem problem = new NextReleaseProblem(this.featuresAddSkills(features),
                                                            this.employeesAddSkills(employees),
                                                            new IterationParameters(nbWeeks, hoursPerweek.doubleValue()));
        return problem;
    }

    public NextReleaseProblem nextReleaseProblemDeleteSkills(int nbWeeks, Number hoursPerweek,
                                                             List<Feature> features, List<Employee> employees) {

        NextReleaseProblem problem = new NextReleaseProblem(this.featuresDeleteSkills(features),
                                                            this.employeesDeleteSkills(employees),
                                                            new IterationParameters(nbWeeks, hoursPerweek.doubleValue()));

        return problem;
    }

    public List<Employee> employeesAddSkills(List<Employee> employees) {

        List<Employee> listEmployees = new ArrayList<>();

        for(Employee e: employees) {
            e = employeeAddSkill(e);
            listEmployees.add(e);
        }

        return listEmployees;
    }
    public Employee employeeAddSkill(Employee e) {

        List<Skill> skills;

        try { skills= e.getSkills(); }
        catch (Exception ex){ skills = new ArrayList<>(); }

        skills.add(new Skill("null"));
        e.setSkills(skills); //The function rebase the last list of skills
        return e;
    }


    public List<Employee> employeesDeleteSkills(List<Employee> employees) {

        List<Employee> listEmployees = new ArrayList<>();

        for(Employee e: listEmployees) listEmployees.add(employeeDeleteSkill(e));
        return listEmployees;

    }
    public Employee employeeDeleteSkill(Employee e) {
        List<Skill> skills;
        skills= e.getSkills();
        skills.remove(new Skill("null"));
        e.setSkills(skills); //The function rebase the last list of skills
        return e;
    }


    public List<Feature> featuresAddSkills(List<Feature> features) {
        List<Feature> listFeatures = new ArrayList<>();

        for(Feature f: features) listFeatures.add(this.featureAddSkill(f));
        return listFeatures;
    }
    public Feature featureAddSkill(Feature f){
        List<Skill> skills;

        // What the actual fuck. This won't ever throw an exception
        try {
            skills = f.getRequiredSkills();
        }
        catch (Exception e) {
            skills = new ArrayList<>();
        }

        skills.add(new Skill("null"));

        return new Feature(f.getName(),f.getPriority(),f.getDuration(),f.getPreviousFeatures(),skills);
    }

    public List<Feature> featuresDeleteSkills(List<Feature> features) {
        List<Feature> listFeatures = new ArrayList<>();
        for(Feature f: features) listFeatures.add(featureDeleteSkills(f));
        return listFeatures;

    }

    public Feature featureDeleteSkills(Feature f) {
        List<Skill> skills = f.getRequiredSkills();
        skills.remove(new Skill("null"));

        Feature feature = new Feature(f.getName(),f.getPriority(),f.getDuration(),f.getPreviousFeatures(),skills);
        return feature;
    }

    private List<PlannedFeature> plannedFeaturesDeleteSkills(List<PlannedFeature> features) {
        List<PlannedFeature> listPlannedFeature = new ArrayList<>();

        for(PlannedFeature pf: features)
            listPlannedFeature.add(this.plannedFeatureDeleteSkill(pf));

        return listPlannedFeature;
    }

    private PlannedFeature plannedFeatureDeleteSkill(PlannedFeature pf) {
        Feature feature = pf.getFeature();
        Employee employee = pf.getEmployee();
        PlannedFeature plannedFeature = new PlannedFeature(this.featureDeleteSkills(feature),this.employeeDeleteSkill(employee));
        plannedFeature.setBeginHour(pf.getBeginHour());
        plannedFeature.setEndHour(pf.getEndHour());
        return plannedFeature;
    }

    public PlanningSolution planningSolution(PlanningSolution solution) {

        List<PlannedFeature> plannedFeatures = this.plannedFeaturesDeleteSkills(solution.getPlannedFeatures());

        for(PlannedFeature plannedFeature: plannedFeatures) {
            System.out.print("FEATURES" + plannedFeature.getBeginHour() + "  " + plannedFeature.getEndHour());
        }

        PlanningSolutionWrapper psw = new PlanningSolutionWrapper(solution,plannedFeatures);

        return psw;
    }


}
