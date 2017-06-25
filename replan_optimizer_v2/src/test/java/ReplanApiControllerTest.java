import com.google.gson.Gson;
import entities.Employee;
import entities.Feature;
import entities.Skill;
import io.swagger.ReplanGson;
import io.swagger.api.ReplanApiController;
import io.swagger.model.ApiPlanningSolution;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;


/**
 * Created by kredes on 16/04/2017.
 */
public class ReplanApiControllerTest {
    private static ReplanApiController apiController;
    private static RandomThings random;
    private static Validator validator;
    private static RequestMocker mocker;
    private static Gson gson;

    @BeforeClass
    public static void setUpBeforeClass() {
        apiController = new ReplanApiController();
        random = new RandomThings();
        validator = new Validator();
        mocker = new RequestMocker();
        gson = ReplanGson.getGson();
    }


    @Test
    public void randomProblem() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> resources = random.employeeList(10);

        random.mix(features, skills, resources);

        MockHttpServletRequest request = mocker.request(4, 40.0, features, resources);

        String response = apiController.replan(request).getBody();

        ApiPlanningSolution solution = gson.fromJson(response, ApiPlanningSolution.class);
    }
}
