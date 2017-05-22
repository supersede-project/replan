library(timevis)

source("sampleData.R")
source("utils.R")

function(input, output, session) {
  output$timeline <- renderTimevis({
    timevis(data = features, groups = resources, options = list(editable = TRUE, stack = FALSE))
  })

  output$score <- renderPlot({
    x <- c(nFeatures, nrow(features))
    bp <- barplot(x, horiz=TRUE, xlab="features", xlim=c(0, 50))
    text(x=x, y=bp, labels=c("Total", "Scheduled"), pos=4, cex=1.5)
  })
    
  output$resources <- renderPlot({
    features[,8] <- sapply(features[,8], as.numeric) # effort as numeric
    resources[,3] <- sapply(resources[,3], as.numeric) # week availability as numeric
    totalhours <- tapply(resources$availability, resources$id, FUN=sum)
    totalhours <- totalhours*nWeeks
    totalhours <- totalhours[order(names(totalhours))]
    usedhours <- tapply(features$effort, features$group, FUN=sum)
    usedhours <- usedhours[order(names(usedhours))]
    ntab <- rbind(usedhours, totalhours)
    ptotal <-round(100 * (totalhours/totalhours), 1)
    pused <-round(100 * (usedhours/totalhours), 1)
    ptab <- rbind(pused, ptotal)
    
    lbls <- paste(ntab, " (", ptab, "%)", sep = "")
    bp <- barplot(ntab, xlim=c(0, 40.0*nWeeks), horiz=TRUE, xlab="hours", beside=TRUE)
    text(x=ntab, y=bp, labels=lbls, pos=4, cex=1.5)
  })
}
