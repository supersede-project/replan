package io.swagger.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.model.ApiPlanningSolution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

@Api(value = "replan", description = "the replan API")
public interface ReplanApi {

    @ApiOperation(value = "Generates a Planning Solution for a given Next Release Problem", notes = "", response = ApiPlanningSolution.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ApiPlanningSolution.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiPlanningSolution.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity", response = ApiPlanningSolution.class) })
    @RequestMapping(value = "/replan",
        produces = { "application/json" }, 
        method = RequestMethod.POST)

    ResponseEntity<ApiPlanningSolution> replan(HttpServletRequest request);

}
