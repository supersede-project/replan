package io.swagger.api;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.swagger.model.ApiNextReleaseProblem;
import io.swagger.model.ApiPlanningSolution;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import logic.SolverNRP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

@Controller
public class ReplanApiController implements ReplanApi {

    private static Gson gson = ReplanGson.getGson();


    public ResponseEntity<String> replan(HttpServletRequest request) {

        // Deserialize
        String content = requestContentAsString(request);
        if (content == null)
            return new ResponseEntity<String>("", HttpStatus.BAD_REQUEST);

        try {
            ApiNextReleaseProblem p = gson.fromJson(content, ApiNextReleaseProblem.class);

            // Convert to internal model
            NextReleaseProblem problem =
                    new NextReleaseProblem(p.getFeatures(), p.getResources(), p.getNbWeeks(), p.getHoursPerWeek());
            problem.setPreviousSolution(p.getPreviousSolution());


            // Execute
            SolverNRP solver = new SolverNRP();

            problem.setAlgorithmParameters(problem.getAlgorithmParameters());
            PlanningSolution solution = solver.executeNRP(problem);

            ApiPlanningSolution apiSolution = new ApiPlanningSolution(solution);

            return new ResponseEntity<String>(gson.toJson(apiSolution), HttpStatus.OK);
        }
        catch (JsonSyntaxException e) {
            return new ResponseEntity<String>("Invalid JSON", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>(
                    "Something went bad. There's a slight chance of the problem being that you passed some really " +
                            "unexpected data. If that's not the case, it might actually be out fault.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String requestContentAsString(HttpServletRequest request) {
        StringBuffer buffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                buffer.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return buffer.toString();
    }

}
