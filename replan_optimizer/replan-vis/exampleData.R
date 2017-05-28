exampleData <- list()

exampleData$plan <- data.frame(
  id=numeric(), 
  content=character(), 
  start=character(), 
  end=character(), 
  group=character(), #resource
  type=character(), 
  priority=numeric(), 
  effort=numeric(), 
  stringsAsFactors=FALSE)

exampleData$resources <- data.frame(
  id=character(), #same as group in plan
  content=character(), # display name
  availability=numeric(), 
  stringsAsFactors=FALSE)

exampleData$features <- data.frame(
  id=character(), #same as content in plan
  content=character(), # display name
  scheduled=character(), # Yes/No
  priority=numeric(), 
  effort=numeric(), 
  stringsAsFactors=FALSE)

exampleData$depGraphEdges <- data.frame(
  node1=character(), 
  node2=character(), 
  stringsAsFactors=FALSE)

exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-861", "F-861", "2017-05-26 16:23:01", "2017-05-27 23:23:01", "E-74", "range", 5, 31)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-321", "F-321", "2017-05-27 23:23:01", "2017-05-28 07:23:01", "E-74", "range", 4, 7)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-714", "F-714", "2017-05-26 16:23:01", "2017-05-26 23:23:01", "E-266", "range", 3, 7)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-292", "F-292", "2017-05-26 23:23:01", "2017-05-27 00:23:01", "E-266", "range", 2, 1)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-164", "F-164", "2017-05-27 00:23:01", "2017-05-28 12:23:01", "E-266", "range", 1, 36)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-798", "F-798", "2017-05-26 16:23:01", "2017-05-27 15:23:01", "E-441", "range", 5, 23)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-858", "F-858", "2017-05-27 15:23:01", "2017-05-27 21:23:01", "E-441", "range", 4, 5)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-585", "F-585", "2017-05-28 07:23:01", "2017-05-28 10:23:01", "E-74", "range", 3, 2)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-946", "F-946", "2017-05-27 21:23:01", "2017-05-28 22:23:01", "E-441", "range", 3, 25)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-394", "F-394", "2017-05-28 10:23:01", "2017-05-29 12:23:01", "E-74", "range", 2, 26)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-740", "F-740", "2017-05-29 12:23:01", "2017-05-30 09:23:01", "E-652", "range", 1, 20)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-536", "F-536", "2017-05-28 12:23:01", "2017-05-28 17:23:01", "E-266", "range", 5, 4)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-229", "F-229", "2017-05-28 17:23:01", "2017-05-30 08:23:01", "E-266", "range", 4, 38)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-634", "F-634", "2017-05-28 22:23:01", "2017-05-30 10:23:01", "E-441", "range", 3, 35)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-220", "F-220", "2017-05-30 09:23:01", "2017-05-31 21:23:01", "E-652", "range", 2, 35)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-216", "F-216", "2017-05-30 10:23:01", "2017-05-30 20:23:01", "E-441", "range", 1, 9)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-545", "F-545", "2017-05-30 20:23:01", "2017-05-31 19:23:01", "E-266", "range", 5, 23)

exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-74", "E-74", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-266", "E-266", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-441", "E-441", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-652", "E-652", 40.0)

exampleData$features[nrow(exampleData$features)+1,] <- c("F-858", "F-858", "Yes", 5, 5)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-394", "F-394", "Yes", 5, 26)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-536", "F-536", "Yes", 5, 4)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-740", "F-740", "Yes", 5, 20)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-740", "F-858")
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-740", "F-394")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-585", "F-585", "Yes", 5, 2)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-946", "F-946", "Yes", 5, 25)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-714", "F-714", "Yes", 5, 7)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-919", "F-919", "No", 5, 30)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-919", "F-740")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-321", "F-321", "Yes", 5, 7)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-292", "F-292", "Yes", 5, 1)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-634", "F-634", "Yes", 5, 35)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-634", "F-536")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-412", "F-412", "No", 5, 32)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-412", "F-919")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-220", "F-220", "Yes", 5, 35)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-798", "F-798", "Yes", 5, 23)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-861", "F-861", "Yes", 5, 31)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-229", "F-229", "Yes", 5, 38)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-216", "F-216", "Yes", 5, 9)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-216", "F-634")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-620", "F-620", "No", 5, 21)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-164", "F-164", "Yes", 5, 36)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-545", "F-545", "Yes", 5, 23)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-545", "F-216")

exampleData$nWeeks <- 4
exampleData$nFeatures <- 20
