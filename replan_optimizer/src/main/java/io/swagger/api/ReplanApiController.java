package io.swagger.api;


import entities.Feature;
import io.swagger.annotations.ApiParam;
import io.swagger.model.NextReleaseProblem;
import io.swagger.model.PlannedFeature;
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

        /*
            Do not plan the features that have already been finished.
            Note that this assumes the features to plan have not changed since the last plan.
         */
        List<Feature> features = new ArrayList<>();
        List<Feature> staticFeatures = new ArrayList<>();
        if (body.getPreviousPlan() != null) {
            for (PlannedFeature pf : body.getPreviousPlan().getJobs()) {
                Feature f = te.Feature2Entities(pf.getFeature());
                if (!f.isStatic()) features.add(f);
                else staticFeatures.add(f);
            }
            removeStaticDependencies(features, staticFeatures);
        } else {
            features = te.FeatureList2Entities(body.getFeatures());
        }


        logic.PlanningSolution solution = solver.executeNRP(body.getNbWeeks(),
                                                    body.getHoursPerWeek(),
                                                    features,
                                                    te.ListResource2Employee(body.getResources()));


        PlanningSolution s = ts.transformPlanningSolution2Swagger(solution);

        if (body.getPreviousPlan() != null)
            s = mergePlanWithPrevious(s, body.getPreviousPlan());

        return new ResponseEntity<PlanningSolution>(s,HttpStatus.OK);
    }

    private PlanningSolution mergePlanWithPrevious(PlanningSolution solution, PlanningSolution previous) {
        List<PlannedFeature> staticFeatures = new ArrayList<>();
        for (PlannedFeature pf : previous.getJobs()) {
            if (pf.getFeature().isStatic())
                staticFeatures.add(pf);
        }

        solution.getJobs().addAll(0, staticFeatures);

        // Compensate the hours of the planned features
        List<PlannedFeature> jobs = solution.getJobs();
        for (int i = staticFeatures.size(); i < jobs.size(); ++i) {
            PlannedFeature current = jobs.get(i);
            PlannedFeature prev = jobs.get(i-1);

            Double duration = current.getEndHour() - current.getBeginHour();

            current.setBeginHour(prev.getEndHour());
            current.setEndHour(current.getBeginHour() + duration);
        }

        return solution;
    }

    /**
     * Removes static dependencies from a list of features
     * @param features
     * @param staticFeatures
     */
    private void removeStaticDependencies(List<Feature> features, List<Feature> staticFeatures) {
        for (Feature f : features) {
            for (Feature sf : staticFeatures) {
                if (f.dependsOn(sf))
                    f.getPreviousFeatures().remove(sf);
            }
        }
    }

}
