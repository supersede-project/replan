import entities.*;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import org.junit.Assert;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.*;

/**
 * Created by kredes on 28/04/2017.
 */
public class RandomThings {
    private static JMetalRandom random = JMetalRandom.getInstance();

    private static final int MANY = 1000;
    private static final int MEDIUM = 100;
    private static final int FEW = 10;
    private static final int VERY_FEW = 5;

    private static final double MUTATION_PROBABILITY = 0.25;

    private Validator validator;

    RandomThings() {
        validator = new Validator();
    }

    private static int skillID = 0;
    private static int featureID = 0;
    private static int employeeID = 0;

    public Skill skill() {
        return new Skill(String.format("S%03d", ++skillID));
    }

    public List<Skill> skillList (int nbElems) {
        List<Skill> skills = new ArrayList<>();
        while (skills.size() < nbElems) {
            Skill s = skill();
            if (!skills.contains(s))
                skills.add(s);
        }
        return skills;
    }

    public Feature feature() {
        return new Feature(
                String.format("F%03d", ++featureID),
                PriorityLevel.getPriorityByLevel(random.nextInt(0,5)),
                Math.floor(random.nextDouble(1.0, 40.0)),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public List<Feature> featureList(int nbElems) {
        List<Feature> features = new ArrayList<>();
        while (features.size() < nbElems) {
            Feature f = feature();
            if (!features.contains(f))
                features.add(f);
        }
        return features;
    }

    public Employee employee() {
        return new Employee(String.format("E%03d", ++employeeID), 40.0, new ArrayList<>());
    }

    public List<Employee> employeeList(int nbElems) {
        List<Employee> employees = new ArrayList<>();
        while (employees.size() < nbElems) {
            Employee e = employee();
            if (!employees.contains(e))
                employees.add(e);
        }
        return employees;
    }


    public void mix(List<Feature> features, List<Skill> skills, List<Employee> employees) {
        skillsToFeatures(features, skills);
        skillsToEmployees(employees, skills);
        dependencies(features);
    }

    public void freeze(PlanningSolution solution) {
        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        if (jobs.size() == 0) return;

        int frozenPfs = 0;
        for (PlannedFeature pf : jobs) {
            if (shouldMutate()) {
                pf.setFrozen(true);
                ++frozenPfs;
            }
        }
        if (frozenPfs == 0) {
            jobs.get(random.nextInt(0, jobs.size() - 1)).setFrozen(true);
        }
    }

    public void violatePrecedences(PlanningSolution solution) {
        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        Map<Feature, PlannedFeature> dependences = new HashMap<>();

        for (PlannedFeature pf : jobs) {
            dependences.put(pf.getFeature(), pf);
        }

        boolean hasMutated = true;
        for (PlannedFeature pf : jobs) {
            if (!pf.getFeature().getPreviousFeatures().isEmpty()) {
                hasMutated = false;
                break;
            }
        }

        while (!hasMutated && !jobs.isEmpty()) {
            for (PlannedFeature pf : jobs) {
                Feature f = pf.getFeature();
                for (Feature d : f.getPreviousFeatures()) {
                    if (shouldMutate()) {
                        PlannedFeature depPf = dependences.get(d);
                        pf.setBeginHour(depPf.getBeginHour());
                        pf.setEndHour(depPf.getEndHour());
                        pf.setFrozen(true);
                        hasMutated = true;
                    }
                }
            }
        }
    }

    public void dependencies(List<Feature> features) {
        while (true) {
            removeDependencies(features);

            boolean mutated = false;
            for (Feature f1 : features) {
                if (shouldMutate()) {
                    Feature f2 = features.get(random.nextInt(0, features.size() - 1));

                    if (!f1.equals(f2)) {
                        f2.getPreviousFeatures().add(f1);
                        mutated = true;
                    }
                }
            }
            while (!mutated) {
                Feature f1 = features.get(random.nextInt(0, features.size() - 1));
                Feature f2 = features.get(random.nextInt(0, features.size() - 1));

                if (!f1.equals(f2)) {
                    f2.getPreviousFeatures().add(f1);
                    mutated = true;
                }
            }

            try {
                validator.validateNoDependencyDeadlocks(features);
                break;
            } catch (AssertionError e) {
                // Go on
            }
        }
    }

    public NextReleaseProblem all(int nSkills, int nFeatures, int nEmployees, int nWeeks, double hoursPerWeek) {
        List<Skill> skills = skillList(nSkills);
        List<Feature> features = featureList(nFeatures);
        List<Employee> employees = employeeList(nEmployees);

        mix(features, skills, employees);

        validator.validateNoUnassignedSkills(skills, employees);

        return new NextReleaseProblem(features, employees, nWeeks, hoursPerWeek);
    }



    private void skillsToFeatures(List<Feature> features, List<Skill> skills) {
        for (Skill s : skills) {
            if (shouldMutate()) {
                Feature f = features.get(random.nextInt(0, features.size() - 1));

                if (!f.getRequiredSkills().contains(s))
                    f.getRequiredSkills().add(s);
            }
        }
        for (Feature f : features) {
            if (f.getRequiredSkills().isEmpty()) {
                f.getRequiredSkills().add(skills.get(random.nextInt(0, skills.size() - 1)));
            }
        }
    }

    private void skillsToEmployees(List<Employee> employees, List<Skill> skills) {
        Set<Skill> assignedSkills = new HashSet<>();
        for (Skill s : skills) {
            if (shouldMutate()) {
                Employee e = employees.get(random.nextInt(0, employees.size() - 1));

                if (!e.getSkills().contains(s)) {
                    e.getSkills().add(s);
                    assignedSkills.add(s);
                }
            }
        }
        for (Employee e : employees) {
            if (e.getSkills().isEmpty()) {
                Skill s = skills.get(random.nextInt(0, skills.size() - 1));
                e.getSkills().add(s);
                assignedSkills.add(s);
            }
        }

        for (Skill s : skills) {
            if (!assignedSkills.contains(s)) {
                Employee e = employees.get(random.nextInt(0, employees.size() - 1));
                e.getSkills().add(s);
                assignedSkills.add(s);
            }
        }

        Assert.assertTrue(assignedSkills.size() == skills.size());
    }



    private boolean shouldMutate() {
        return random.nextDouble() <= MUTATION_PROBABILITY;
    }

    private String name(String prefix) {
        return String.format("%s-%d", prefix, random.nextInt(0, MANY));
    }

    private void removeDependencies(List<Feature> features) {
        for (Feature f : features)
            f.getPreviousFeatures().clear();
    }
}
