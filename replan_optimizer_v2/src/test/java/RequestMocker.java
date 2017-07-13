import com.google.gson.Gson;
import entities.Employee;
import entities.Feature;
import io.swagger.api.ReplanGson;
import io.swagger.model.ApiNextReleaseProblem;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

/**
 * Created by kredes on 11/06/2017.
 */
public class RequestMocker {

    private static Gson gson = ReplanGson.getGson();

    public MockHttpServletRequest request(int nbWeeks, double hoursPerWeek,
                        List<Feature> features, List<Employee> resources)
    {
        ApiNextReleaseProblem problem = new ApiNextReleaseProblem(nbWeeks, hoursPerWeek, features, resources);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");

        request.setContent(gson.toJson(problem).getBytes());

        return request;
    }

    public MockHttpServletRequest request(String content) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");

        request.setContent(content.getBytes());

        return request;
    }
}
