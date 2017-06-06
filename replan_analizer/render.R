library(igraph)

source("utils.R")

updateTimeline <- function(d) {
  setItems("timelineRelease", d$plan)
  setGroups("timelineRelease", d$resources)
  setOptions("timelineRelease", list(stack = FALSE))
  fitWindow("timelineRelease", list(animation = FALSE))
}

renderResults <- function(output, d) {
  output$resultsTable <- renderTable({
    featureRes <- c(nrow(d$plan), nrow(d$features), (nrow(d$plan)/nrow(d$features))*100)
    score <- sum(d$plan$priority)
    maxScore <- sum(d$features$priority)
    scoreRes <- c(score, maxScore, (score/maxScore)*100)
    
    results <- rbind(featureRes, scoreRes)
    colnames(results) <- c("This release", "MAX", "%")
    rownames(results) <- c("Features", "Score")
    
    results
  }, 
  digits = 0, 
  rownames = TRUE,
  caption = "<center><b><span style='color:#000000'>Results</b></center>",
  caption.placement = getOption("xtable.caption.placement", "top"), 
  caption.width = getOption("xtable.caption.width", NULL))
}

renderSelectedFeature <- function(output, d, fID) {
  output$selectedFeature <- renderTable({
    featureData <- data.frame(ID = "None selected",
                              Name = "",
                              Start = "",
                              End = "",
                              Duration = "",
                              Effort = "",
                              Priority = "",
                              Done_by = "",
                              Doable_by = "",
                              Dependencies = "",
                              Req_Skills = "",
                              stringsAsFactors = FALSE)
    if(!is.null(fID)) {
      plannedFeature <- subset(d$plan, id == fID)
      feature <- subset(d$features, id == fID)
      
      if(nrow(plannedFeature) == 1) {
        featureData$Start <- plannedFeature$start
        featureData$End <- plannedFeature$end
        featureData$Duration <- as.numeric(difftime(plannedFeature$end, plannedFeature$start, units = "hours"))
        featureData$Done_by <- plannedFeature$group
      }
      if(nrow(feature) == 1) {
        featureData$ID <- feature$id
        featureData$Name <- feature$content
        featureData$Effort <- feature$effort
        featureData$Priority <- feature$priority
        
        dependencies <- d$depGraphEdges$node2[d$depGraphEdges$node1 == fID]
        featureData$Dependencies <- paste(dependencies, collapse = ", ")
        
        reqSkills <- d$reqSkillsGraphEdges$node2[d$reqSkillsGraphEdges$node1 == fID]
        featureData$Req_Skills <- paste(reqSkills, collapse = ", ")
        
        resourceSkills <- aggregate(node2 ~ node1, data = d$skillsGraphEdges, list)
        matchSkillsTable <- sapply(resourceSkills$node2, function(x) reqSkills %in% x)
        if(is.null(nrow(matchSkillsTable)))
           selectedResources <- matchSkillsTable
        else
          selectedResources <- apply(t(matchSkillsTable), MARGIN = 1, function(x) all(x == TRUE))
        skilledResources <- d$resources$id[which(selectedResources)]
        featureData$Doable_by <- paste(skilledResources, collapse = ", ")
      }
    }
    
    featureData <- t(featureData)
    colnames(featureData) <- c("Values")
    
    featureData
  }, 
  digits = 0, 
  rownames = TRUE,
  caption = "<center><b><span style='color:#000000'>Selected feature</b></center>",
  caption.placement = getOption("xtable.caption.placement", "top"), 
  caption.width = getOption("xtable.caption.width", NULL))
}

renderSelectedResource <- function(output, d, rID) {
  output$selectedResource <- renderTable({
    resourceData <- data.frame(ID = "None selected",
                              Name = "",
                              Hours_per_week  = "",
                              Skills = "",
                              stringsAsFactors = FALSE)
    if(!is.null(rID)) {
      resource <- subset(d$resources, id == rID)
      
      if(nrow(resource) == 1) {
        resourceData$ID <- resource$id
        resourceData$Name <- resource$content
        resourceData$Hours_per_week <- resource$availability

        skills <- d$skillsGraphEdges$node2[d$skillsGraphEdges$node1 == rID]
        resourceData$Skills <- paste(skills, collapse = ", ")
      }
    }
    
    resourceData <- t(resourceData)
    colnames(resourceData) <- c("Values")
    
    resourceData
  }, 
  digits = 0, 
  rownames = TRUE,
  caption = "<center><b><span style='color:#000000'>Selected resource</b></center>",
  caption.placement = getOption("xtable.caption.placement", "top"), 
  caption.width = getOption("xtable.caption.width", NULL))
}

renderDepGraph <- function(output, d) {
  output$depGraph <- renderPlot({
    dG <- graph.data.frame(d$depGraphEdges)
    V(dG)$color <- ifelse(d$features$scheduled[match(V(dG)$name, d$features$id)] == "Yes", "lightgreen", "lightcoral")
    plot (dG,
          vertex.size=35, 
          vertex.label.color="black", 
          main="Dependency graph"
    )
  })
  
  output$depGraphLegend <- renderPlot({
    colors <- c("lightgreen", "lightcoral")
    descriptions <- c("Scheduled feature", 
                      "Non-scheduled feature")
    
    plot(1, type="n", axes=FALSE, xlab="", ylab="")
    par(xpd=TRUE)
    legend("topleft",
           inset=c(0,-3.8),
           descriptions, 
           fill = colors,
           bty = "n")
  },
  height = 150, 
  width = 400)
}

renderSkillsGraph <- function(output, d) {
  output$skillsGraph <- renderPlot({
    dG <- graph.data.frame(d$skillsGraphEdges)
    V(dG)$color <- ifelse(V(dG)$name %in% d$skillsGraphEdges$node1, "cornflowerblue", "lightblue")
    plot (dG,
          vertex.size=35, 
          vertex.label.color="black", 
          main="Skills graph"
    )
  })
  
  output$skillsGraphLegend <- renderPlot({
    colors <- c("cornflowerblue", "lightblue")
    descriptions <- c("Resource", 
                      "Skill")
    
    plot(1, type="n", axes=FALSE, xlab="", ylab="")
    par(xpd=TRUE)
    legend("topleft",
           inset=c(0,-3.8),
           descriptions, 
           fill = colors,
           bty = "n")
  },
  height = 150, 
  width = 400)
}

renderResources <- function(output, d) {
  output$resources <- renderPlot({
    totalhours <- tapply(d$resources$availability, d$resources$id, FUN=sum)
    totalhours <- totalhours*d$nWeeks
    totalhours <- totalhours[order(names(totalhours), decreasing = TRUE)]
    
    usedhours <- tapply(d$plan$effort, d$plan$group, FUN=sum)
    usedhours <- usedhours[order(names(usedhours), decreasing = TRUE)]
    
    endhours <- tapply(d$plan$end, d$plan$group, FUN=max)
    endhours <- endhours[order(names(endhours), decreasing = TRUE)]
    starthours <- rep(min(d$plan$start), dim(endhours))
    endhours <- as.numeric(difftime(endhours, starthours, units = "hours"))
    
    startendhours <- tapply(difftime(d$plan$end, d$plan$start, units = "hours"), d$plan$group, FUN=sum)
    startendhours <- startendhours[order(names(startendhours), decreasing = TRUE)]
    
    hours <- rbind(usedhours, startendhours, endhours)
    totalhours <- rbind(totalhours, totalhours, totalhours)
    
    ptab <-round(100 * (hours/totalhours), 1)
    
    lbls <- paste(hours, "h of ", totalhours, "h (", ptab, "%)", sep = "")
    colors <- c('#8dd3c7','#ffffb3','#bebada')
    
    bp <- barplot(ptab, 
                  xlim = c(0, 100), 
                  horiz = TRUE, 
                  xlab = "% of used hours per resource", 
                  beside = TRUE,
                  col = colors,
                  main = "Resource usage")
    
    text(x = ptab, 
         y = bp, 
         labels = lbls, 
         pos = placeText(ptab), 
         cex = 0.8)
    
  }, 
  height = 90*nrow(d$resources), 
  width = 400)
  
  output$resourcesLegend <- renderPlot({
    colors <- rev(c('#8dd3c7','#ffffb3','#bebada'))
    descriptions <- c("Hours from release start to last end hour", 
                      "Hours as a sum of feature start-end hours",
                      "Hours as a sum of feature effort hours")
    
    plot(1, type="n", axes=FALSE, xlab="", ylab="")
    par(xpd=TRUE)
    legend("topleft",
           inset=c(0,-3.8),
           descriptions, 
           fill = colors,
           bty = "n")
  },
  height = 150, 
  width = 400)
}

renderDataTables <- function(output, d) {
  output$plannedTable <- renderDataTable({d$plan})
  output$resourcesTable <- renderDataTable({d$resources})
  output$resourceSkillsTable <- renderDataTable({d$skillsGraphEdges})
  output$featuresTable <- renderDataTable({d$features})
  output$featuresDepTable <- renderDataTable({d$depGraphEdges})
  output$featuresReqSkillsTable <- renderDataTable({d$reqSkillsGraphEdges})
}

renderThisData <- function(output, session, d) {
  session$userData$d <- d
  updateTimeline(session$userData$d)
  renderResults(output, session$userData$d)
  renderSelectedFeature(output, session$userData$d, NULL)
  renderSelectedResource(output, session$userData$d, NULL)
  renderDepGraph(output, session$userData$d)
  renderSkillsGraph(output, session$userData$d)
  renderResources(output, session$userData$d)
  renderDataTables(output, session$userData$d)
  
  updateSelectInput(
    session, 
    "unscheduled", 
    choices=c("NONE", subset(session$userData$d$features, session$userData$d$features$scheduled=="No")$id))
  
  updateSelectInput(
    session, 
    "resource", 
    choices=c("NONE", session$userData$d$resources$id))
  
}