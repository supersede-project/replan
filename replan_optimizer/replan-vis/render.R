
renderThisData <- function(output, d) {
  setItems("timeline", d$features)
  setGroups("timeline", d$resources)
  setOptions("timeline", list(stack = FALSE))
  fitWindow("timeline", list(animation = FALSE))
  
  output$score <- renderPlot({
    x <- c(d$nFeatures, nrow(d$features))
    bp <- barplot(x, horiz=TRUE, xlab="features", xlim=c(0, 50))
    text(x=x, y=bp, labels=c(paste("Total (",x[1],")", sep=""), paste("Scheduled (",x[2],")", sep="")), pos=4, cex=1.5)
  })
  
  output$resources <- renderPlot({
    d$features[,8] <- sapply(d$features[,8], as.numeric) # effort as numeric
    d$resources[,3] <- sapply(d$resources[,3], as.numeric) # week availability as numeric
    totalhours <- tapply(d$resources$availability, d$resources$id, FUN=sum)
    totalhours <- totalhours*d$nWeeks
    totalhours <- totalhours[order(names(totalhours))]
    usedhours <- tapply(d$features$effort, d$features$group, FUN=sum)
    usedhours <- usedhours[order(names(usedhours))]
    ptab <-round(100 * (usedhours/totalhours), 1)
    
    lbls <- paste(usedhours, "h (", ptab, "%)", sep = "")
    bp <- barplot(ptab, xlim=c(0, 100), horiz=TRUE, xlab="% of used hours per resource", beside=TRUE)
    text(x=ptab, y=bp, labels=lbls, pos=2, cex=1.5)
  })
}