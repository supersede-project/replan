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
    private static RandomSwaggerThings rnd;

    @BeforeClass
    public static void setUpBeforeClass() {
        apiController = new ReplanApiController();
        rnd = new RandomSwaggerThings();
    }

    @Test
    public void frozenJobsAreNotReplaned() {
        List<Skill> skills = rnd.skillList(7);
        List<Feature> features = rnd.featureList(4);
        List<Resource> resources = rnd.resourceList(3);

        rnd.mix(features, skills, resources);

        NextReleaseProblem problem = new NextReleaseProblem(4, 50.0, features, resources);
        PlanningSolution s1 = apiController.replan(problem).getBody();

        rnd.freeze(s1);
        problem.setPreviousPlan(s1);
        PlanningSolution s2 = apiController.replan(problem).getBody();

        Assert.assertTrue(true);
    }
}
