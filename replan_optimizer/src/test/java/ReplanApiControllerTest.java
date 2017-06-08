import io.swagger.api.ReplanApiController;
import org.junit.BeforeClass;


/**
 * Created by kredes on 16/04/2017.
 */
public class ReplanApiControllerTest {
    private static ReplanApiController apiController;
    private static RandomThings random;
    private static Validator validator;

    @BeforeClass
    public static void setUpBeforeClass() {
        apiController = new ReplanApiController();
        random = new RandomThings();
        validator = new Validator();
    }

/*
    //@Test
    public void frozenJobsAreNotReplanned() {
        List<Skill> skills = random.skillList(7);
        List<Feature> features = random.featureList(5);
        List<Employee> resources = random.employeeList(15);

        random.mix(features, skills, resources);




        ApiNextReleaseProblem problem = new ApiNextReleaseProblem(4, 40.0, features, resources);
        ApiPlanningSolution s1 = apiController.replan(problem).getBody();

        validator.validateAll(s1);

        random.freeze(s1);

        //problem.setPreviousPlan(s1);
        ApiPlanningSolution s2 = apiController.replan(problem).getBody();

        validator.validateFrozen(s1, s2);
    }

    @Test
    public void randomProblemValidatesAllConstraints() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> resources = random.employeeList(10);

        random.mix(features, skills, resources);

        ApiNextReleaseProblem problem = new ApiNextReleaseProblem(4, 40.0, features, resources);

        PlanningSolution solution = apiController.replan(problem).getBody();

        validator.validateAll(solution);
    }*/
}
