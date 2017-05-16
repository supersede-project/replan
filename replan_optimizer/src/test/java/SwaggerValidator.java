import io.swagger.model.*;
import org.junit.Assert;

import java.util.*;

import static junit.framework.Assert.assertTrue;

/**
 * Created by kredes on 02/05/2017.
 */
public class SwaggerValidator {

    private static final String DEPENDENCE_FAIL_MESSAGE =
                    "Feature %s depends on feature %s, but planning does not respect this precedence.\n" +
                    " Dependence -> beginHour: %f, endHour: %f.\n Dependant -> beginHour: %f, endHour: %f";

    private static final String SKILL_FAIL_MESSAGE =
                    "Feature %s requires skill %s, but employee %s does not have it.";

    private static final String FROZEN_FAIL_MESSAGE =
                    "PlannedFeature %s is frozen in the previous plan, but this is not respected in the new plan.";


    public void validateDependencies(PlanningSolution solution) {
        List<PlannedFeature> jobs = solution.getJobs();
        Map<Feature, PlannedFeature> dependences = new HashMap<>();

        for (PlannedFeature pf : jobs) {
            dependences.put(pf.getFeature(), pf);
        }

        for (PlannedFeature pf : jobs) {
            Feature f = pf.getFeature();
            for (Feature d : f.getDepends_on()) {
                PlannedFeature depPf = dependences.get(d);
                assertTrue(String.format(DEPENDENCE_FAIL_MESSAGE,
                        f.toString(), d.toString(), pf.getBeginHour(), pf.getEndHour(), depPf.getBeginHour(), depPf.getEndHour()),
                        pf.getBeginHour() >= depPf.getEndHour());
            }
        }
    }

    public void validateSkills(PlanningSolution solution) {
        for (PlannedFeature pf : solution.getJobs()) {
            Resource e = pf.getResource();
            Feature f = pf.getFeature();

            for (Skill s : f.getRequired_skills()) {
                assertTrue(String.format(SKILL_FAIL_MESSAGE, f.toString(), s.toString(), e.toString()),
                            e.getSkills().contains(s));
            }
        }
    }

    public void validateFrozen(PlanningSolution previous, PlanningSolution current) {
        for (PlannedFeature pf : previous.getJobs()) {
            if (pf.isFrozen()) {
                assertTrue(String.format(FROZEN_FAIL_MESSAGE, pf.toString()), current.getJobs().contains(pf));
            }
        }
    }

    public void validateAll(PlanningSolution solution) {
        validateDependencies(solution);

        validateSkills(solution);
    }

    public void validateAll(PlanningSolution previous, PlanningSolution current) {
        validateDependencies(current);
        validateFrozen(previous, current);
        validateSkills(current);
    }

    public void validateNoUnassignedSkills(List<Skill> skills, List<Resource> resources) {
        Set<Skill> assignedSkills = new HashSet<>();
        for (Resource e : resources)
            for (Skill s : e.getSkills())
                assignedSkills.add(s);

        for (Skill s : skills)
            Assert.assertTrue(assignedSkills.contains(s));
    }
}
