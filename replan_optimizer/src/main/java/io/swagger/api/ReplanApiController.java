package io.swagger.api;


import entities.Feature;
import io.swagger.annotations.ApiParam;
import io.swagger.model.NextReleaseProblem;
import io.swagger.model.PlanningSolution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import wrapper.SolverNRP;
import wrapper.parser.Transform2NRPEntities;
import wrapper.parser.Transform2SwaggerModel;

import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

@Controller
public class ReplanApiController implements ReplanApi {

    public ResponseEntity<PlanningSolution> replan(

@ApiParam(value = "" ,required=true )
@RequestBody NextReleaseProblem body

) {
        SolverNRP solver = new SolverNRP();

        Transform2NRPEntities te = new Transform2NRPEntities();
        Transform2SwaggerModel ts = new Transform2SwaggerModel();


        List<Feature> features = te.FeatureList2Entities(body.getFeatures());

        logic.PlanningSolution solution =
                solver.executeNRP(body.getNbWeeks(),
                        body.getHoursPerWeek(), features, te.ListResource2Employee(body.getResources()));

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

        PlanningSolution s = ts.transformPlanningSolution2Swagger(solution);

        return new ResponseEntity<PlanningSolution>(s,HttpStatus.OK);
    }

}
