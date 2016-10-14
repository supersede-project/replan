package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Feature;
import io.swagger.model.Priority;
import io.swagger.model.Skill;
import java.util.ArrayList;
import java.util.List;




/**
 * Feature
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

public class Feature   {
  private String name = null;

  private Double duration = null;

  private Priority priority = null;

  private List<Skill> requiredSkills = new ArrayList<Skill>();

  private List<Feature> dependsOn = new ArrayList<Feature>();

  public Feature name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Feature duration(Double duration) {
    this.duration = duration;
    return this;
  }

   /**
   * Get duration
   * @return duration
  **/
  @ApiModelProperty(value = "")
  public Double getDuration() {
    return duration;
  }

  public void setDuration(Double duration) {
    this.duration = duration;
  }

  public Feature priority(Priority priority) {
    this.priority = priority;
    return this;
  }

   /**
   * Get priority
   * @return priority
  **/
  @ApiModelProperty(value = "")
  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public Feature requiredSkills(List<Skill> requiredSkills) {
    this.requiredSkills = requiredSkills;
    return this;
  }

  public Feature addRequiredSkillsItem(Skill requiredSkillsItem) {
    this.requiredSkills.add(requiredSkillsItem);
    return this;
  }

   /**
   * Get requiredSkills
   * @return requiredSkills
  **/
  @ApiModelProperty(value = "")
  public List<Skill> getRequiredSkills() {
    return requiredSkills;
  }

  public void setRequiredSkills(List<Skill> requiredSkills) {
    this.requiredSkills = requiredSkills;
  }

  public Feature dependsOn(List<Feature> dependsOn) {
    this.dependsOn = dependsOn;
    return this;
  }

  public Feature addDependsOnItem(Feature dependsOnItem) {
    this.dependsOn.add(dependsOnItem);
    return this;
  }

   /**
   * array of features
   * @return dependsOn
  **/
  @ApiModelProperty(value = "array of features")
  public List<Feature> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(List<Feature> dependsOn) {
    this.dependsOn = dependsOn;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Feature feature = (Feature) o;
    return Objects.equals(this.name, feature.name) &&
        Objects.equals(this.duration, feature.duration) &&
        Objects.equals(this.priority, feature.priority) &&
        Objects.equals(this.requiredSkills, feature.requiredSkills) &&
        Objects.equals(this.dependsOn, feature.dependsOn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, duration, priority, requiredSkills, dependsOn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Feature {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    requiredSkills: ").append(toIndentedString(requiredSkills)).append("\n");
    sb.append("    dependsOn: ").append(toIndentedString(dependsOn)).append("\n");
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

