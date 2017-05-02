import entities.Employee;
import entities.Feature;
import entities.PlannedFeature;
import logic.PlanningSolution;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kredes on 02/05/2017.
 */
public class Validator {
    public void validateDependencies(PlanningSolution solution) {
        List<PlannedFeature> jobs = solution.getPlannedFeatures();
        Map<Feature, PlannedFeature> dependences = new HashMap<>();

        for (PlannedFeature pf : jobs) {
            dependences.put(pf.getFeature(), pf);
        }

        for (PlannedFeature pf : jobs) {
            Feature f = pf.getFeature();
            for (Feature d : f.getPreviousFeatures()) {
                PlannedFeature depPf = dependences.get(d);
                Assert.assertTrue(pf.getBeginHour() >= depPf.getEndHour());
            }
        }
    }

    public void validateSkills(PlanningSolution solution) {
        for (PlannedFeature pf : solution.getPlannedFeatures()) {
            Employee e = pf.getEmployee();
            Feature f = pf.getFeature();

            Assert.assertTrue(e.getSkills().containsAll(f.getRequiredSkills()));
        }
    }

    public void validateFrozen(PlanningSolution previous, PlanningSolution current) {
        for (PlannedFeature pf : previous.getPlannedFeatures()) {
            if (pf.isFrozen()) {
                Assert.assertTrue(current.getPlannedFeatures().contains(pf));
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
}
