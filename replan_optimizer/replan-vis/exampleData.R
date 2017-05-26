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

exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-451", "F-451", "2017-05-26 12:57:27", "2017-05-27 00:57:27", "E-747", "range", 5, 12)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-497", "F-497", "2017-05-26 12:57:27", "2017-05-27 08:57:27", "E-903", "range", 5, 20)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-588", "F-588", "2017-05-26 12:57:27", "2017-05-26 19:57:27", "E-59", "range", 5, 7)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-629", "F-629", "2017-05-26 19:57:27", "2017-05-27 20:57:27", "E-59", "range", 5, 24)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-303", "F-303", "2017-05-27 00:57:27", "2017-05-27 22:57:27", "E-747", "range", 5, 22)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-743", "F-743", "2017-05-26 12:57:27", "2017-05-26 15:57:27", "E-463", "range", 5, 3)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-921", "F-921", "2017-05-27 08:57:27", "2017-05-27 22:57:27", "E-903", "range", 5, 14)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-751", "F-751", "2017-05-27 22:57:27", "2017-05-29 02:57:27", "E-747", "range", 5, 28)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-149", "F-149", "2017-05-29 02:57:27", "2017-05-29 18:57:27", "E-747", "range", 5, 15)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-417", "F-417", "2017-05-26 15:57:27", "2017-05-27 21:57:27", "E-463", "range", 5, 29)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-939", "F-939", "2017-05-29 18:57:27", "2017-05-30 05:57:27", "E-747", "range", 5, 11)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-619", "F-619", "2017-05-27 22:57:27", "2017-05-28 12:57:27", "E-903", "range", 5, 13)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-263", "F-263", "2017-05-27 22:57:27", "2017-05-28 21:57:27", "E-59", "range", 5, 22)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-275", "F-275", "2017-05-27 21:57:27", "2017-05-29 05:57:27", "E-463", "range", 5, 32)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-780", "F-780", "2017-05-28 21:57:27", "2017-05-29 20:57:27", "E-59", "range", 5, 23)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-870", "F-870", "2017-05-29 02:57:27", "2017-05-30 05:57:27", "E-903", "range", 5, 27)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-892", "F-892", "2017-05-29 20:57:27", "2017-05-31 10:57:27", "E-59", "range", 5, 37)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-585", "F-585", "2017-05-31 10:57:27", "2017-06-01 11:57:27", "E-59", "range", 5, 25)
exampleData$plan[nrow(exampleData$plan)+1,] <- c("F-387", "F-387", "2017-05-29 05:57:27", "2017-05-29 22:57:27", "E-463", "range", 5, 16)

exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-747", "E-747", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-903", "E-903", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-59", "E-59", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-463", "E-463", 40.0)

exampleData$features[nrow(exampleData$features)+1,] <- c("F-451", "F-451", "Yes", 5, 12)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-751", "F-751", "Yes", 5, 28)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-497", "F-497", "Yes", 5, 20)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-870", "F-870", "Yes", 5, 27)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-870", "F-451")
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-870", "F-751")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-743", "F-743", "Yes", 5, 3)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-149", "F-149", "Yes", 5, 15)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-892", "F-892", "Yes", 5, 37)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-585", "F-585", "Yes", 5, 25)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-585", "F-870")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-417", "F-417", "Yes", 5, 29)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-629", "F-629", "Yes", 5, 24)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-921", "F-921", "Yes", 5, 14)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-921", "F-497")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-915", "F-915", "Yes", 5, 22)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-915", "F-585")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-619", "F-619", "Yes", 5, 13)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-387", "F-387", "Yes", 5, 16)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-303", "F-303", "Yes", 5, 22)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-939", "F-939", "Yes", 5, 11)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-263", "F-263", "Yes", 5, 22)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-263", "F-921")
exampleData$features[nrow(exampleData$features)+1,] <- c("F-275", "F-275", "Yes", 5, 32)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-588", "F-588", "Yes", 5, 7)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-780", "F-780", "Yes", 5, 23)
exampleData$depGraphEdges[nrow(exampleData$depGraphEdges)+1,] <- c("F-780", "F-263")

exampleData$nWeeks <- 4
exampleData$nFeatures <- 20
