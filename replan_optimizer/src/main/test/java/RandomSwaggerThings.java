package java;

import io.swagger.model.*;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kredes on 27/04/2017.
 * Convenience class to generate random things.
 */
public class RandomSwaggerThings {

    private static JMetalRandom random = JMetalRandom.getInstance();

    private static final int MANY = 1000;
    private static final int MEDIUM = 100;
    private static final int FEW = 10;
    private static final int VERY_FEW = 5;

    private static final double MUTATION_PROBABILITY = 0.25;

    public Skill skill() {
        return new Skill(name("Skill"));
    }

    public List<Skill> skillList (int nbElems) {
        List<Skill> skills = new ArrayList<>();
        for (int i = 0; i < nbElems; ++i) {
            skills.add(skill());
        }
        return skills;
    }

    public Feature feature() {
        return new Feature(name("Feature"), new Priority(5, 10), 40.0, new ArrayList<>(), new ArrayList<>());
    }

    public List<Feature> featureList(int nbElems) {
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < nbElems; ++i) {
            features.add(feature());
        }
        return features;
    }

    public Resource resource() {
        return new Resource(name("Resource"), 40.0, new ArrayList<>());
    }

    public List<Resource> resourceList(int nbElems) {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < nbElems; ++i) {
            resources.add(resource());
        }
        return resources;
    }


    public void mix(List<Feature> features, List<Skill> skills, List<Resource> resources) {
        skillsToFeatures(features, skills);
        skillsToResources(resources, skills);
        dependencies(features);
    }

    public void freeze(PlanningSolution solution) {
        if (solution.getJobs().size() == 0) return;

        int frozenPfs = 0;
        for (PlannedFeature pf : solution.getJobs()) {
            if (shouldMutate()) {
                pf.setFrozen(true);
                ++frozenPfs;
            }
        }
        if (frozenPfs == 0) {
            List<PlannedFeature> jobs = solution.getJobs();
            jobs.get(random.nextInt(0, jobs.size() - 1)).setFrozen(true);
        }
    }

    private void skillsToFeatures(List<Feature> features, List<Skill> skills) {
        for (Skill s : skills) {
            if (shouldMutate()) {
                Feature f = features.get(random.nextInt(0, features.size() - 1));

                if (!f.getRequired_skills().contains(s))
                    f.addRequiredSkillsItem(s);
            }
        }
        for (Feature f : features) {
            if (f.getRequired_skills().isEmpty()) {
                f.addRequiredSkillsItem(skills.get(random.nextInt(0, skills.size() - 1)));
            }
        }
    }

    private void skillsToResources(List<Resource> resources, List<Skill> skills) {
        for (Skill s : skills) {
            if (shouldMutate()) {
                Resource r = resources.get(random.nextInt(0, resources.size() - 1));

                if (!r.getSkills().contains(s))
                    r.addSkillsItem(s);
            }
        }
        for (Resource r : resources) {
            if (r.getSkills().isEmpty()) {
                r.addSkillsItem(skills.get(random.nextInt(0, skills.size() - 1)));
            }
        }
    }

    private void dependencies(List<Feature> features) {
        if (shouldMutate()) {
            Feature f1 = features.get(random.nextInt(0, features.size() - 1));
            Feature f2 = features.get(random.nextInt(0, features.size() - 1));

            if (!f1.equals(f2)) f2.addDependsOnItem(f1);
        }
    }

    private boolean shouldMutate() {
        return random.nextDouble() <= MUTATION_PROBABILITY;
    }

    private String name(String type) {
        return String.format("%s %d", type, random.nextInt(0, MANY));
    }
}
