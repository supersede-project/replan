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

import java.util.ArrayList;
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

        // Do not plan the features that have already been finished
        List<Feature> features = new ArrayList<>();
        for (Feature f : te.FeatureList2Entities(body.getFeatures())) {
            if (!f.isStatic()) features.add(f);
        }


        logic.PlanningSolution solution = solver.executeNRP(body.getNbWeeks(),
                                                    body.getHoursPerWeek(),
                                                    features,
                                                    te.ListResource2Employee(body.getResources()));


        PlanningSolution s = ts.transformPlanningSolution2Swagger(solution);

        return new ResponseEntity<PlanningSolution>(s,HttpStatus.OK);
    }

}
