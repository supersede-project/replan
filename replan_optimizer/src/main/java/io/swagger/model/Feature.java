package io.swagger.model;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;




/**
 * Feature
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2016-10-01T15:48:29.618Z")

public class Feature   {

    /* Some assumptions around here */
    private LocalDateTime deadline = null;

    private boolean isStatic = false;

    private String name = null;

    private Double duration = null;

    private Priority priority = null;

    private List<Skill> required_skills = new ArrayList<>();

    private List<Feature> depends_on = new ArrayList<>();

    public Feature() {}

    public Feature(String name, Priority priority, Double duration, List<Feature> depends_on,
                   List<Skill> required_skills, LocalDateTime deadline) {
        this.name = name;
        this.duration = duration;
        this.priority = priority;
        this.required_skills = required_skills;
        this.depends_on = depends_on;
        setDeadline(deadline);
    }




    public Feature name(String name) {
        this.name = name;
        return this;
    }


    /**
     * Get deadline
     * @return deadline
     **/
    @ApiModelProperty(value = "")
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        isStatic = deadline.isBefore(LocalDateTime.now());
    }

    /**
     * Get isStatic
     * @return isStatic
     **/
    @ApiModelProperty(value = "")
    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean b) {
        isStatic = b;
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
        this.required_skills = requiredSkills;
        return this;
    }

    public Feature addRequiredSkillsItem(Skill requiredSkillsItem) {
        this.required_skills.add(requiredSkillsItem);
        return this;
    }

    /**
     * Get required_skills
     * @return required_skills
     **/
    @ApiModelProperty(value = "")
    public List<Skill> getRequired_skills() {
        return required_skills;
    }

    public void setRequired_skills(List<Skill> required_skills) {
        this.required_skills = required_skills;
    }

    public Feature dependsOn(List<Feature> dependsOn) {
        this.depends_on = dependsOn;
        return this;
    }

    public Feature addDependsOnItem(Feature dependsOnItem) {
        this.depends_on.add(dependsOnItem);
        return this;
    }

    /**
     * array of features
     * @return depends_on
     **/
    @ApiModelProperty(value = "array of features")
    public List<Feature> getDepends_on() {
        return depends_on;
    }

    public void setDepends_on(List<Feature> depends_on) {
        this.depends_on = depends_on;
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
                Objects.equals(this.required_skills, feature.required_skills) &&
                Objects.equals(this.depends_on, feature.depends_on);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duration, priority, required_skills, depends_on);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Feature {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
        sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
        sb.append("    required_skills: ").append(toIndentedString(required_skills)).append("\n");
        sb.append("    depends_on: ").append(toIndentedString(depends_on)).append("\n");
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

