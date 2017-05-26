source("utils.R")

renderThisData <- function(output, d) {
  d$plan <- d$plan[order(d$plan$id), ]
  d$resources <- d$resources[order(d$resources$id), ]
  d$features <- d$features[order(d$features$id), ]
  d$depGraphEdges <- d$depGraphEdges[order(d$depGraphEdges$node1), ]
  
  setItems("timeline", d$plan)
  setGroups("timeline", d$resources)
  setOptions("timeline", list(stack = FALSE))
  fitWindow("timeline", list(animation = FALSE))
  
  output$scheduledFeatures <- renderText({
    paste("Planned ", nrow(d$plan), " out of ", d$nFeatures, " features (", (nrow(d$plan)/d$nFeatures)*100,"%)", sep="")
  })

  output$planScore <- renderText({
    d$plan$priority <- sapply(d$plan$priority, as.numeric) # priority as numeric
    d$features$priority <- sapply(d$features$priority, as.numeric) # priority as numeric
    score <- sum(d$plan$priority)
    maxScore <- sum(d$features$priority)
    paste("This plan has a score of ", score, " out of ", maxScore, " (", (score/maxScore)*100,"%)", sep="")
  })
  
  output$depGraph <- renderPlot({
    dG <- graph.data.frame(d$depGraphEdges)
    V(dG)$color <- ifelse(d$features$scheduled[match(V(dG)$name, d$features$id)] == "Yes", "lightblue", "red")
    plot (dG,
          #edge.arrow.size=.5, 
          #vertex.color="lightblue", 
          vertex.size=35, 
          #vertex.frame.color="gray", 
          vertex.label.color="black", 
          #vertex.label.dist=5
          main="Dependency graph"
          )
  })
  
  output$resources <- renderPlot({
    d$plan[,8] <- sapply(d$plan[,8], as.numeric) # effort as numeric
    d$resources[,3] <- sapply(d$resources[,3], as.numeric) # week availability as numeric
    totalhours <- tapply(d$resources$availability, d$resources$id, FUN=sum)
    totalhours <- totalhours*d$nWeeks
    totalhours <- totalhours[order(names(totalhours))]
    usedhours <- tapply(d$plan$effort, d$plan$group, FUN=sum)
    usedhours <- usedhours[order(names(usedhours))]
    ptab <-round(100 * (usedhours/totalhours), 1)
    
    lbls <- paste(usedhours, "h of ", totalhours, "h (", ptab, "%)", sep = "")
    bp <- barplot(ptab, 
                  xlim=c(0, 100), 
                  horiz=TRUE, 
                  xlab="% of used hours per resource", 
                  beside=TRUE,
                  main = "Resource usage")
    text(x=ptab, 
         y=bp, 
         labels=lbls, 
         pos=placeText(ptab), 
         cex=1.5)
  }, 
  height = 90*nrow(d$resources), 
  width = 400)
}