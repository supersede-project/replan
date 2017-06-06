package io.swagger.api;


import io.swagger.annotations.*;
import logic.NextReleaseProblem;
import logic.PlanningSolution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

@Api(value = "replan", description = "the replan API")
public interface ReplanApi {

    @ApiOperation(value = "Generates a Planning Solution for a given Next Release Problem", notes = "", response = PlanningSolution.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = PlanningSolution.class),
        @ApiResponse(code = 400, message = "Bad Request", response = PlanningSolution.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity", response = PlanningSolution.class) })
    @RequestMapping(value = "/replan",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<PlanningSolution> replan(

@ApiParam(value = "" ,required=true ) @RequestBody NextReleaseProblem body

);

}
