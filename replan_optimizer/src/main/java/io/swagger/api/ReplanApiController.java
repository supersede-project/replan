package io.swagger.api;


import com.google.gson.*;
import entities.PriorityLevel;
import io.swagger.model.ApiNextReleaseProblem;
import io.swagger.model.ApiPlanningSolution;
import logic.PlanningSolution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import wrapper.SolverNRP;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.lang.reflect.Type;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

@Controller
public class ReplanApiController implements ReplanApi {

    private static final Gson gson;

    static {
        JsonDeserializer<PriorityLevel> priorityDeserializer = new JsonDeserializer<PriorityLevel>() {
            @Override
            public PriorityLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                int level = json.getAsJsonObject().get("level").getAsInt();
                return PriorityLevel.fromValues(level, level);
            }
        };

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PriorityLevel.class, priorityDeserializer);

        gson = gsonBuilder.create();
    }


    public ResponseEntity<String> replan(

 HttpServletRequest request

) {
        // Deserialize
        String body = getBodyAsJsonString(request);
        if (body == null)
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);

        ApiNextReleaseProblem problem = gson.fromJson(body, ApiNextReleaseProblem.class);

        System.out.println("\nDeserialized Body: ");
        System.out.println(gson.toJson(problem));

        // Execute
        SolverNRP solver = new SolverNRP();
        PlanningSolution solution =
                solver.executeNRP(problem.getNbWeeks(), problem.getHoursPerWeek(), problem.getFeatures(), problem.getResources());

        System.out.println("done.");

        ApiPlanningSolution apiSolution = new ApiPlanningSolution(solution.getPlannedFeatures());



        /* Let's ignore previous plan for now
        if (body.getPreviousPlan() == null) {
            solution = solver.executeNRP(body.getNbWeeks(),
                    body.getHoursPerWeek(),
                    features,
                    te.ListResource2Employee(body.getResources()));
        } else {
            // Dummy problem to be able to create a logic.PlanningSolution
            EntitiesEvaluator ee = EntitiesEvaluator.getInstance();
            logic.NextReleaseProblem problem = ee.nextReleaseProblemAddSkills(0, 0.0, new ArrayList<>(), new ArrayList<>());

            solution = solver.executeNRP(body.getNbWeeks(),
                    body.getHoursPerWeek(),
                    features,
                    te.ListResource2Employee(body.getResources()),
                    te.planningSolutionToEntity(body.getPreviousPlan(), problem));
        }
        */

        return new ResponseEntity<String>(gson.toJson(apiSolution), HttpStatus.OK);
    }

    private String getBodyAsJsonString(HttpServletRequest request) {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return jb.toString();
    }

}
