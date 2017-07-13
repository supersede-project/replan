import com.google.gson.Gson;
import entities.Employee;
import entities.Feature;
import entities.Skill;
import io.swagger.api.ReplanGson;
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
    private static RequestMocker mocker;
    private static Gson gson;

    @BeforeClass
    public static void setUpBeforeClass() {
        apiController = new ReplanApiController();
        random = new RandomThings();
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

    @Test
    public void randomProblemWithCustomAlgorithmParameters() {
        List<Skill> skills = random.skillList(5);
        List<Feature> features = random.featureList(5);
        List<Employee> resources = random.employeeList(10);

        random.mix(features, skills, resources);

        MockHttpServletRequest request = mocker.request("{\"nbWeeks\":3,\"hoursPerWeek\":\"40.0\",\"features\":[{\"name\":\"3\",\"duration\":\"20.0\",\"priority\":{\"level\":3,\"score\":3},\"required_skills\":[{\"name\":\"3\"}],\"depends_on\":[]},{\"name\":\"4\",\"duration\":\"50.0\",\"priority\":{\"level\":2,\"score\":2},\"required_skills\":[{\"name\":\"3\"}],\"depends_on\":[]},{\"name\":\"5\",\"duration\":\"10.0\",\"priority\":{\"level\":1,\"score\":1},\"required_skills\":[{\"name\":\"2\"}],\"depends_on\":[]},{\"name\":\"6\",\"duration\":\"5.0\",\"priority\":{\"level\":4,\"score\":4},\"required_skills\":[{\"name\":\"1\"}],\"depends_on\":[{\"name\":\"4\",\"duration\":\"50.0\",\"priority\":{\"level\":2,\"score\":2},\"required_skills\":[{\"name\":\"3\"}],\"depends_on\":[]},{\"name\":\"5\",\"duration\":\"10.0\",\"priority\":{\"level\":1,\"score\":1},\"required_skills\":[{\"name\":\"2\"}],\"depends_on\":[]}]},{\"name\":\"7\",\"duration\":\"5.0\",\"priority\":{\"level\":5,\"score\":5},\"required_skills\":[{\"name\":\"3\"}],\"depends_on\":[{\"name\":\"6\",\"duration\":\"5.0\",\"priority\":{\"level\":4,\"score\":4},\"required_skills\":[{\"name\":\"1\"}],\"depends_on\":[{\"name\":\"4\",\"duration\":\"50.0\",\"priority\":{\"level\":2,\"score\":2},\"required_skills\":[{\"name\":\"3\"}],\"depends_on\":[]},{\"name\":\"5\",\"duration\":\"10.0\",\"priority\":{\"level\":1,\"score\":1},\"required_skills\":[{\"name\":\"2\"}],\"depends_on\":[]}]}]}],\"resources\":[{\"name\":\"2\",\"availability\":\"22.0\",\"skills\":[{\"name\":\"2\"},{\"name\":\"3\"}]},{\"name\":\"3\",\"availability\":\"36.0\",\"skills\":[{\"name\":\"1\"},{\"name\":\"3\"}]}]}");

        String response = apiController.replan(request).getBody();

        ApiPlanningSolution solution = gson.fromJson(response, ApiPlanningSolution.class);
    }
}
