library(curl)
library(jsonlite)

source("utils.R")

getRePlanDataStructure <- function() {
  d <- list()
  
  d$plan <- data.frame(
    id=numeric(), 
    content=character(), 
    start=character(), 
    end=character(), 
    group=character(), #resource
    type=character(), 
    priority=numeric(), 
    effort=numeric(), 
    stringsAsFactors=FALSE)
  
  d$resources <- data.frame(
    id=character(), #same as group in plan
    content=character(), # display name
    availability=numeric(), 
    stringsAsFactors=FALSE)
  
  d$features <- data.frame(
    id=character(), #same as content in plan
    content=character(), # display name
    scheduled=character(), # Yes/No
    priority=numeric(), 
    effort=numeric(), 
    stringsAsFactors=FALSE)
  
  d$depGraphEdges <- data.frame(
    node1=character(), 
    node2=character(), 
    stringsAsFactors=FALSE)

  d$skillsGraphEdges <- data.frame(
    node1=character(), 
    node2=character(), 
    stringsAsFactors=FALSE)
  
  d$reqSkillsGraphEdges <- data.frame(
    node1=character(), 
    node2=character(), 
    stringsAsFactors=FALSE)
  
  return(d)  
}

fixData <- function(d) {
  d$plan <- d$plan[order(d$plan$id), ]
  d$resources <- d$resources[order(d$resources$id), ]
  d$features <- d$features[order(d$features$id), ]
  
  d$depGraphEdges <- d$depGraphEdges[order(d$depGraphEdges$node1), ]
  d$skillsGraphEdges <- d$skillsGraphEdges[order(d$skillsGraphEdges$node1), ]
  d$reqSkillsGraphEdges <- d$reqSkillsGraphEdges[order(d$reqSkillsGraphEdges$node1), ]
  
  d$plan$priority <- sapply(d$plan$priority, as.numeric) # priority as numeric
  d$features$priority <- sapply(d$features$priority, as.numeric) # priority as numeric
  d$plan$effort <- sapply(d$plan$effort, as.numeric) # effort as numeric
  d$resources$availability <- sapply(d$resources$availability, as.numeric) # availability as numeric
  
  classNames <- c("itemPriority1", "itemPriority2", "itemPriority3", "itemPriority4", "itemPriority5")
  if(nrow(d$plan) > 0) d$plan$className <- classNames[d$plan$priority]
  
  return(d)
}

getDataFromExample <- function(d) {

  d$plan[nrow(d$plan)+1,] <- c("F015", "F015", "2017-05-30 19:13:56", "2017-05-31 23:13:56", "E003", "range", 5, 28)
  d$plan[nrow(d$plan)+1,] <- c("F006", "F006", "2017-05-31 23:13:56", "2017-06-01 17:13:56", "E003", "range", 5, 18)
  d$plan[nrow(d$plan)+1,] <- c("F016", "F016", "2017-05-30 19:13:56", "2017-06-01 06:13:56", "E001", "range", 5, 35)
  d$plan[nrow(d$plan)+1,] <- c("F009", "F009", "2017-05-30 19:13:56", "2017-05-31 14:13:56", "E002", "range", 2, 19)
  d$plan[nrow(d$plan)+1,] <- c("F007", "F007", "2017-05-31 14:13:56", "2017-06-01 15:13:56", "E002", "range", 5, 24)
  d$plan[nrow(d$plan)+1,] <- c("F010", "F010", "2017-06-01 15:13:56", "2017-06-02 19:13:56", "E002", "range", 4, 27)
  d$plan[nrow(d$plan)+1,] <- c("F013", "F013", "2017-05-30 19:13:56", "2017-05-31 16:13:56", "E004", "range", 5, 21)
  d$plan[nrow(d$plan)+1,] <- c("F002", "F002", "2017-05-31 16:13:56", "2017-06-01 12:13:56", "E004", "range", 5, 20)
  d$plan[nrow(d$plan)+1,] <- c("F005", "F005", "2017-06-02 19:13:56", "2017-06-03 18:13:56", "E002", "range", 4, 23)
  d$plan[nrow(d$plan)+1,] <- c("F018", "F018", "2017-06-01 06:13:56", "2017-06-02 04:13:56", "E001", "range", 4, 21)
  d$plan[nrow(d$plan)+1,] <- c("F011", "F011", "2017-06-03 18:13:56", "2017-06-03 20:13:56", "E002", "range", 1, 1)
  d$plan[nrow(d$plan)+1,] <- c("F003", "F003", "2017-06-01 12:13:56", "2017-06-02 13:13:56", "E004", "range", 4, 25)
  d$plan[nrow(d$plan)+1,] <- c("F019", "F019", "2017-06-03 20:13:56", "2017-06-04 15:13:56", "E002", "range", 4, 18)
  d$plan[nrow(d$plan)+1,] <- c("F001", "F001", "2017-06-01 17:13:56", "2017-06-03 04:13:56", "E003", "range", 3, 35)
  d$plan[nrow(d$plan)+1,] <- c("F014", "F014", "2017-06-02 04:13:56", "2017-06-03 10:13:56", "E001", "range", 2, 30)
  
  d$resources[nrow(d$resources)+1,] <- c("E003", "E003", 40.0)
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E003", "S001")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E003", "S002")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E003", "S003")
  d$resources[nrow(d$resources)+1,] <- c("E001", "E001", 40.0)
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E001", "S001")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E001", "S004")
  d$resources[nrow(d$resources)+1,] <- c("E002", "E002", 40.0)
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E002", "S001")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E002", "S002")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E002", "S004")
  d$resources[nrow(d$resources)+1,] <- c("E004", "E004", 40.0)
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E004", "S003")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E004", "S005")
  d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1,] <- c("E004", "S004")
  
  d$features[nrow(d$features)+1,] <- c("F001", "F001", "Yes", 3, 35)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F001", "S001")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F001", "S002")
  d$features[nrow(d$features)+1,] <- c("F002", "F002", "Yes", 5, 20)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F002", "S003")
  d$features[nrow(d$features)+1,] <- c("F003", "F003", "Yes", 4, 25)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F003", "S004")
  d$features[nrow(d$features)+1,] <- c("F004", "F004", "No", 3, 39)
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F004", "F001")
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F004", "F002")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F004", "S004")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F004", "S005")
  d$features[nrow(d$features)+1,] <- c("F005", "F005", "Yes", 4, 23)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F005", "S001")
  d$features[nrow(d$features)+1,] <- c("F006", "F006", "Yes", 5, 18)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F006", "S001")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F006", "S002")
  d$features[nrow(d$features)+1,] <- c("F007", "F007", "Yes", 5, 24)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F007", "S001")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F007", "S004")
  d$features[nrow(d$features)+1,] <- c("F008", "F008", "No", 3, 18)
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F008", "F004")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F008", "S001")
  d$features[nrow(d$features)+1,] <- c("F009", "F009", "Yes", 2, 19)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F009", "S002")
  d$features[nrow(d$features)+1,] <- c("F010", "F010", "Yes", 4, 27)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F010", "S001")
  d$features[nrow(d$features)+1,] <- c("F011", "F011", "Yes", 1, 1)
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F011", "F003")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F011", "S004")
  d$features[nrow(d$features)+1,] <- c("F012", "F012", "No", 1, 36)
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F012", "F008")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F012", "S002")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F012", "S004")
  d$features[nrow(d$features)+1,] <- c("F013", "F013", "Yes", 5, 21)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F013", "S003")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F013", "S005")
  d$features[nrow(d$features)+1,] <- c("F014", "F014", "Yes", 2, 30)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F014", "S001")
  d$features[nrow(d$features)+1,] <- c("F015", "F015", "Yes", 5, 28)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F015", "S002")
  d$features[nrow(d$features)+1,] <- c("F016", "F016", "Yes", 5, 35)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F016", "S001")
  d$features[nrow(d$features)+1,] <- c("F017", "F017", "No", 4, 35)
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F017", "F011")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F017", "S004")
  d$features[nrow(d$features)+1,] <- c("F018", "F018", "Yes", 4, 21)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F018", "S001")
  d$features[nrow(d$features)+1,] <- c("F019", "F019", "Yes", 4, 18)
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F019", "S004")
  d$features[nrow(d$features)+1,] <- c("F020", "F020", "No", 2, 36)
  d$depGraphEdges[nrow(d$depGraphEdges)+1,] <- c("F020", "F017")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F020", "S001")
  d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1,] <- c("F020", "S004")
  
  d$nWeeks <- 4
  
  return(d)
}

# this fuction is overrided by the user.
getDataFromUser <- function(d) {
  return(d)
}

getDataFromController <- function(d, baseURL, projectID, releaseID) {
  # baseURL <- "http://platform.supersede.eu:8280/replan"
  # projectID <- 1
  # releaseID <- 1
  
  # Fetch data
  projectURL <- paste0(baseURL, "/projects/", projectID)
  projectData <- try(fromJSON(projectURL))
  
  releaseURL <- paste0(baseURL, "/projects/", projectID, "/releases/", releaseID)
  releaseData <- try(fromJSON(releaseURL))

  featuresURL <- paste0(baseURL, "/projects/", projectID, "/releases/", releaseID, "/features")
  featuresData <- try(fromJSON(featuresURL))

  planURL <- paste0(baseURL, "/projects/", projectID, "/releases/", releaseID, "/plan")
  planData <- try(fromJSON(planURL))
  
  if(class(projectData) == "try-error" |
     class(releaseData) == "try-error" |
     class(featuresData) == "try-error" |
     class(planData) == "try-error") {
    alert("Connection error with the controller!")
    return(d)
  }
    
  # Parse nWeeks
  d$nWeeks <- round(as.numeric(difftime(releaseData$deadline, releaseData$starts_at, units = "weeks")))

  # Parse resources
  fulltime <- projectData$hours_per_week_and_full_time_resource
  nResources <- nrow(releaseData$resources)
  if(nResources >= 1)
    for(i in 1:nResources) {
      d$resources[i, ] <- c(
        getID("E", releaseData$resources$id[i]), 
        releaseData$resources$name[i], 
        (as.numeric(releaseData$resources$availability[i])/100)*as.numeric(fulltime))
      nResourceSkills <- nrow(releaseData$resources$skills[[i]])
      if (nResourceSkills >= 1)
        for(j in 1:nResourceSkills)
          d$skillsGraphEdges[nrow(d$skillsGraphEdges)+1, ] <- c(
            getID("E", releaseData$resources$id[i]), 
            getID("S", releaseData$resources$skills[[i]]$id[j]))
    }
  
  # Parse features
  nFeatures <- nrow(featuresData)
  if(nFeatures >= 1)
    for(i in 1:nFeatures) {
      d$features[i, ] <- c(
        getID("F", featuresData$id[i]), 
        getID("F", featuresData$id[i]),
        ifelse(featuresData$id[i] %in% planData$jobs$feature$id, "Yes", "No"),
        featuresData$priority[i],
        featuresData$effort[i])
      nReqSkills <- nrow(featuresData$required_skills[[i]])
      if(nReqSkills >= 1) 
        for(j in 1:nReqSkills)
          d$reqSkillsGraphEdges[nrow(d$reqSkillsGraphEdges)+1, ] <- c(
            getID("F", featuresData$id[i]), 
            getID("S", featuresData$required_skills[[i]]$id[j]))
      nDeps <- nrow(featuresData$depends_on[[i]])
      if(nDeps >= 1)
        for(j in 1:nDeps)
          if(featuresData$depends_on[[i]]$id[j] %in% featuresData$id)
            d$depGraphEdges[nrow(d$depGraphEdges)+1, ] <- c(
              getID("F", featuresData$id[i]), 
              getID("F", featuresData$depends_on[[i]]$id[j]))
    }
  
  # Parse plan
  nJobs <- nrow(planData$jobs)
  if(nJobs >= 1)
    for(i in 1:nJobs)
      d$plan[nrow(d$plan)+1, ] <- c(
        getID("F", planData$jobs$feature$id[i]),
        getID("F", planData$jobs$feature$id[i]),
        planData$jobs$starts[i],
        planData$jobs$ends[i],
        getID("E", planData$jobs$resource$id[i]),
        "range",
        planData$jobs$feature$priority[i],
        planData$jobs$feature$effort[i]
      )
  
  return(d)
}

