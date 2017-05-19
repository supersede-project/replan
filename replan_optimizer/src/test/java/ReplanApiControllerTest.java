import io.swagger.api.ReplanApiController;
import io.swagger.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;


/**
 * Created by kredes on 16/04/2017.
 */
public class ReplanApiControllerTest {
    private static ReplanApiController apiController;
    private static RandomSwaggerThings random;
    private static SwaggerValidator validator;

    @BeforeClass
    public static void setUpBeforeClass() {
        apiController = new ReplanApiController();
        random = new RandomSwaggerThings();
        validator = new SwaggerValidator();
    }

    // TODO: Check why this test does not pass.
    // Because we no longer accept a previous plan
    //@Test
    public void frozenJobsAreNotReplaned() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(5);
        List<Resource> resources = random.resourceList(15);

        random.mix(features, skills, resources);

        NextReleaseProblem problem = new NextReleaseProblem(4, 50.0, features, resources);
        PlanningSolution s1 = apiController.replan(problem).getBody();

        random.freeze(s1);

        //problem.setPreviousPlan(s1);
        PlanningSolution s2 = apiController.replan(problem).getBody();


        for (PlannedFeature pf : s1.getJobs()) {
            if (pf.isFrozen()) {
                Assert.assertTrue(s2.getJobs().contains(pf));
            }
        }
    }

    @Test
    public void randomProblemValidatesAllConstraints() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Resource> resources = random.resourceList(10);

        random.mix(features, skills, resources);

        NextReleaseProblem problem = new NextReleaseProblem(4, 50.0, features, resources);
        PlanningSolution solution = apiController.replan(problem).getBody();

        validator.validateAll(solution);
    }
}
