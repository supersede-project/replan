source("utils.R")

updateTimeline <- function(d) {
  setItems("timelineRelease", d$plan)
  setGroups("timelineRelease", d$resources)
  setOptions("timelineRelease", list(stack = FALSE))
  fitWindow("timelineRelease", list(animation = FALSE))
}

renderResults <- function(output, d) {
  output$resultsTable <- renderTable({
    featureRes <- c(nrow(d$plan), d$nFeatures, (nrow(d$plan)/d$nFeatures)*100)
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
  output$featuresTable <- renderDataTable({d$features})
}

renderThisData <- function(output, session, d) {
  session$userData$d <- fixData(d)
  updateTimeline(session$userData$d)
  renderResults(output, session$userData$d)
  renderSelectedFeature(output, session$userData$d, NULL)
  renderDepGraph(output, session$userData$d)
  renderResources(output, session$userData$d)
  renderDataTables(output, session$userData$d)
  
  updateSelectInput(
    session, 
    "unscheduled", 
    choices=c("NONE", subset(session$userData$d$features, session$userData$d$features$scheduled=="No")$id))
  
}