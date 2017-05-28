fixData <- function(d) {
  d$plan <- d$plan[order(d$plan$id), ]
  d$resources <- d$resources[order(d$resources$id), ]
  d$features <- d$features[order(d$features$id), ]
  d$depGraphEdges <- d$depGraphEdges[order(d$depGraphEdges$node1), ]
  d$plan$priority <- sapply(d$plan$priority, as.numeric) # priority as numeric
  d$features$priority <- sapply(d$features$priority, as.numeric) # priority as numeric
  d$plan$effort <- sapply(d$plan$effort, as.numeric) # effort as numeric
  d$resources$availability <- sapply(d$resources$availability, as.numeric) # availability as numeric
  
  classNames <- c("itemPriority1", "itemPriority2", "itemPriority3", "itemPriority4", "itemPriority5")
  d$plan$className <- classNames[d$plan$priority]
  
  return(d)
}

placeText <- function(p) {
  return(ifelse(p > 50, 2, 4))
}
