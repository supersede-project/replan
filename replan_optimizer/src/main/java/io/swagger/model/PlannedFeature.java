package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Feature;
import io.swagger.model.Resource;




/**
 * PlannedFeature
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

public class PlannedFeature   {
  private Double beginHour = null;

  private Double endHour = null;

  private Resource resource = null;

  private Feature feature = null;

  public PlannedFeature beginHour(Double beginHour) {
    this.beginHour = beginHour;
    return this;
  }

   /**
   * Get beginHour
   * @return beginHour
  **/
  @ApiModelProperty(value = "")
  public Double getBeginHour() {
    return beginHour;
  }

  public void setBeginHour(Double beginHour) {
    this.beginHour = beginHour;
  }

  public PlannedFeature endHour(Double endHour) {
    this.endHour = endHour;
    return this;
  }

   /**
   * Get endHour
   * @return endHour
  **/
  @ApiModelProperty(value = "")
  public Double getEndHour() {
    return endHour;
  }

  public void setEndHour(Double endHour) {
    this.endHour = endHour;
  }

  public PlannedFeature resource(Resource resource) {
    this.resource = resource;
    return this;
  }

   /**
   * Get resource
   * @return resource
  **/
  @ApiModelProperty(value = "")
  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public PlannedFeature feature(Feature feature) {
    this.feature = feature;
    return this;
  }

   /**
   * Get feature
   * @return feature
  **/
  @ApiModelProperty(value = "")
  public Feature getFeature() {
    return feature;
  }

  public void setFeature(Feature feature) {
    this.feature = feature;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlannedFeature plannedFeature = (PlannedFeature) o;
    return Objects.equals(this.beginHour, plannedFeature.beginHour) &&
        Objects.equals(this.endHour, plannedFeature.endHour) &&
        Objects.equals(this.resource, plannedFeature.resource) &&
        Objects.equals(this.feature, plannedFeature.feature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beginHour, endHour, resource, feature);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PlannedFeature {\n");
    
    sb.append("    beginHour: ").append(toIndentedString(beginHour)).append("\n");
    sb.append("    endHour: ").append(toIndentedString(endHour)).append("\n");
    sb.append("    resource: ").append(toIndentedString(resource)).append("\n");
    sb.append("    feature: ").append(toIndentedString(feature)).append("\n");
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

