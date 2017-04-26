import io.swagger.api.ReplanApiController;
import io.swagger.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by kredes on 16/04/2017.
 */
public class ReplanApiControllerTest {
    private static ReplanApiController apiController;

    @BeforeClass
    public static void setUpBeforeClass() {
        apiController = new ReplanApiController();
    }

    @Test
    public void staticFeaturesAreMergedWithNewPlan() {
        Skill s = new Skill("Whatever skill");
        List<Skill> skills = new ArrayList<>();
        skills.add(s);

        LocalDateTime now = LocalDateTime.now();
        Feature f1 = new Feature("Feature 1", new Priority(5, 10), 40.0, new ArrayList<Feature>(), skills, now.plusDays(1));
        List<Feature> deps2 = Arrays.asList(f1);
        Feature f2 = new Feature("Feature 2", new Priority(5, 10), 40.0, deps2, skills, now.plusDays(2));
        List<Feature> deps3 = Arrays.asList(f2);
        Feature f3 = new Feature("Feature 3", new Priority(5, 10), 40.0, deps3, skills, now.plusDays(3));

        List<Resource> resources = Arrays.asList(
                new Resource("Test Employee", 50.0, skills)
        );

        List<Feature> features = new ArrayList<>();
        features.add(f1);
        features.add(f2);
        features.add(f3);

        NextReleaseProblem problem = new NextReleaseProblem(3, 50.0, features, resources);
        PlanningSolution s1 = apiController.replan(problem).getBody();

        Assert.assertTrue(s1.getJobs().size() == 3);
        Assert.assertTrue(
                s1.getJobs().get(0).getFeature().equals(f1) &&
                        s1.getJobs().get(1).getFeature().equals(f2) &&
                        s1.getJobs().get(2).getFeature().equals(f3)
        );

        s1.getJobs().get(0).getFeature().setStatic(true);
        problem.getFeatures().get(0).getDepends_on().clear();

        problem.setPreviousPlan(s1);
        PlanningSolution s2 = apiController.replan(problem).getBody();

        Assert.assertTrue(s2.getJobs().get(0).equals(s1.getJobs().get(0)));
        Assert.assertTrue(
                s2.getJobs().get(0).getFeature().equals(f1) &&
                        s2.getJobs().get(1).getFeature().equals(f2) &&
                        s2.getJobs().get(2).getFeature().equals(f3)
        );
    }
}
