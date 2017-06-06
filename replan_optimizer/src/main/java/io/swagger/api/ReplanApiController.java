package io.swagger.api;


import entities.Feature;
import io.swagger.annotations.ApiParam;
import io.swagger.model.ApiNextReleaseProblem;
import logic.PlanningSolution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import wrapper.SolverNRP;

import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

@Controller
public class ReplanApiController implements ReplanApi {

    public ResponseEntity<PlanningSolution> replan(

@ApiParam(value = "" ,required=true )
@RequestBody ApiNextReleaseProblem body

) {
        SolverNRP solver = new SolverNRP();

        List<Feature> features = body.getFeatures();

        logic.PlanningSolution solution =
                solver.executeNRP(body.getNbWeeks(), body.getHoursPerWeek(), features, body.getResources());

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

        return new ResponseEntity<PlanningSolution>(solution, HttpStatus.OK);
    }

}
