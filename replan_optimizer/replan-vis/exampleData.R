exampleData <- list()

exampleData$features <- data.frame(
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
  id=character(), #same as group in exampleData$features
  content=character(), # display name
  availability=numeric(),
  stringsAsFactors=FALSE)

exampleData$features[nrow(exampleData$features)+1,] <- c("F-763", "F-763", "2017-05-24 12:14:06", "2017-05-25 13:14:06", "E-899", "range", 5, 25)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-135", "F-135", "2017-05-24 12:14:06", "2017-05-25 18:14:06", "E-564", "range", 5, 30)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-722", "F-722", "2017-05-24 12:14:06", "2017-05-25 18:14:06", "E-735", "range", 5, 30)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-631", "F-631", "2017-05-24 12:14:06", "2017-05-24 16:14:06", "E-978", "range", 5, 4)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-989", "F-989", "2017-05-24 16:14:06", "2017-05-25 06:14:06", "E-978", "range", 5, 13)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-427", "F-427", "2017-05-25 18:14:06", "2017-05-27 00:14:06", "E-564", "range", 5, 29)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-531", "F-531", "2017-05-25 18:14:06", "2017-05-25 22:14:06", "E-735", "range", 5, 3)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-186", "F-186", "2017-05-25 06:14:06", "2017-05-25 07:14:06", "E-978", "range", 5, 1)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-320", "F-320", "2017-05-27 00:14:06", "2017-05-27 06:14:06", "E-564", "range", 5, 6)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-68", "F-68", "2017-05-27 06:14:06", "2017-05-28 22:14:06", "E-564", "range", 5, 39)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-651", "F-651", "2017-05-25 07:14:06", "2017-05-26 04:14:06", "E-978", "range", 5, 21)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-311", "F-311", "2017-05-26 04:14:06", "2017-05-26 17:14:06", "E-978", "range", 5, 12)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-450", "F-450", "2017-05-27 06:14:06", "2017-05-28 18:14:06", "E-978", "range", 5, 35)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-128", "F-128", "2017-05-25 13:14:06", "2017-05-26 06:14:06", "E-899", "range", 5, 16)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-436", "F-436", "2017-05-25 22:14:06", "2017-05-27 06:14:06", "E-735", "range", 5, 31)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-246", "F-246", "2017-05-26 06:14:06", "2017-05-27 20:14:06", "E-899", "range", 5, 38)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-393", "F-393", "2017-05-27 06:14:06", "2017-05-27 22:14:06", "E-735", "range", 5, 15)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-789", "F-789", "2017-05-28 22:14:06", "2017-05-29 22:14:06", "E-564", "range", 5, 24)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-501", "F-501", "2017-05-28 18:14:06", "2017-05-30 01:14:06", "E-735", "range", 5, 31)
exampleData$features[nrow(exampleData$features)+1,] <- c("F-513", "F-513", "2017-05-30 01:14:06", "2017-05-31 12:14:06", "E-735", "range", 5, 35)

exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-899", "E-899", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-564", "E-564", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-735", "E-735", 40.0)
exampleData$resources[nrow(exampleData$resources)+1,] <- c("E-978", "E-978", 40.0)

exampleData$nWeeks <- 10
exampleData$nFeatures <- 20
