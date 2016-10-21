package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Skill;
import java.util.ArrayList;
import java.util.List;




/**
 * Resource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

public class Resource   {
  private String name = null;

  private Double weekAvailability = null;

  private List<Skill> skills = new ArrayList<Skill>();

  public Resource name(String name) {
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

  public Resource weekAvailability(Double weekAvailability) {
    this.weekAvailability = weekAvailability;
    return this;
  }

   /**
   * Get weekAvailability
   * @return weekAvailability
  **/
  @ApiModelProperty(value = "")
  public Double getWeekAvailability() {
    return weekAvailability;
  }

  public void setWeekAvailability(Double weekAvailability) {
    this.weekAvailability = weekAvailability;
  }

  public Resource skills(List<Skill> skills) {
    this.skills = skills;
    return this;
  }

  public Resource addSkillsItem(Skill skillsItem) {
    this.skills.add(skillsItem);
    return this;
  }

   /**
   * Get skills
   * @return skills
  **/
  @ApiModelProperty(value = "")
  public List<Skill> getSkills() {
    return skills;
  }

  public void setSkills(List<Skill> skills) {
    this.skills = skills;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Resource resource = (Resource) o;
    return Objects.equals(this.name, resource.name) &&
        Objects.equals(this.weekAvailability, resource.weekAvailability) &&
        Objects.equals(this.skills, resource.skills);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, weekAvailability, skills);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Resource {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    weekAvailability: ").append(toIndentedString(weekAvailability)).append("\n");
    sb.append("    skills: ").append(toIndentedString(skills)).append("\n");
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

