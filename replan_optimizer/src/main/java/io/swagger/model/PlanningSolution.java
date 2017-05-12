package io.swagger.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;




/**
 * PlanningSolution
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

public class PlanningSolution   {
  private List<PlannedFeature> jobs = new ArrayList<PlannedFeature>();


    public PlanningSolution() {}

    public PlanningSolution(PlannedFeature ...features) {
        for (PlannedFeature f : features) {
            jobs.add(f);
        }
    }



  public PlanningSolution jobs(List<PlannedFeature> jobs) {
    this.jobs = jobs;
    return this;
  }

  public PlanningSolution addJobsItem(PlannedFeature jobsItem) {
    this.jobs.add(jobsItem);
    return this;
  }

   /**
   * Get jobs
   * @return jobs
  **/
  @ApiModelProperty(value = "")
  public List<PlannedFeature> getJobs() {
    return jobs;
  }

  public void setJobs(List<PlannedFeature> jobs) {
    this.jobs = jobs;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlanningSolution planningSolution = (PlanningSolution) o;
    return Objects.equals(this.jobs, planningSolution.jobs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PlanningSolution {\n");
    
    sb.append("    jobs: ").append(toIndentedString(jobs)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

