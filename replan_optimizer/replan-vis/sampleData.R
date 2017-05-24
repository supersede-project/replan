
features <- data.frame(
  id=numeric(), 
  content=character(), 
  start=character(), 
  end=character(), 
  group=character(), #resource
  type=character(), 
  priority=numeric(), 
  effort=numeric(), 
  stringsAsFactors=FALSE)

resources <- data.frame(
  id=character(), #same as group in features
  content=character(), # display name
  availability=numeric(), 
  stringsAsFactors=FALSE)

features[nrow(features)+1,] <- c("F-440", "F-440", "2017-05-23 15:49:16", "2017-05-24 08:49:16", "E-754", "range", 5, 17)
features[nrow(features)+1,] <- c("F-904", "F-904", "2017-05-23 15:49:16", "2017-05-24 00:49:16", "E-131", "range", 5, 9)
features[nrow(features)+1,] <- c("F-509", "F-509", "2017-05-24 10:49:16", "2017-05-25 13:49:16", "E-957", "range", 5, 27)
features[nrow(features)+1,] <- c("F-768", "F-768", "2017-05-23 15:49:16", "2017-05-24 11:49:16", "E-131", "range", 5, 20)
features[nrow(features)+1,] <- c("F-814", "F-814", "2017-05-25 13:49:16", "2017-05-26 09:49:16", "E-754", "range", 5, 19)
features[nrow(features)+1,] <- c("F-271", "F-271", "2017-05-23 15:49:16", "2017-05-23 18:49:16", "E-131", "range", 5, 3)
features[nrow(features)+1,] <- c("F-951", "F-951", "2017-05-23 15:49:16", "2017-05-24 19:49:16", "E-672", "range", 5, 28)
features[nrow(features)+1,] <- c("F-428", "F-428", "2017-05-24 00:49:16", "2017-05-25 11:49:16", "E-957", "range", 5, 35)
features[nrow(features)+1,] <- c("F-449", "F-449", "2017-05-23 15:49:16", "2017-05-23 19:49:16", "E-957", "range", 5, 4)
features[nrow(features)+1,] <- c("F-955", "F-955", "2017-05-23 15:49:16", "2017-05-24 00:49:16", "E-131", "range", 5, 9)
features[nrow(features)+1,] <- c("F-694", "F-694", "2017-05-23 15:49:16", "2017-05-24 10:49:16", "E-754", "range", 5, 19)
features[nrow(features)+1,] <- c("F-328", "F-328", "2017-05-25 11:49:16", "2017-05-26 09:49:16", "E-131", "range", 5, 21)
features[nrow(features)+1,] <- c("F-57", "F-57", "2017-05-26 09:49:16", "2017-05-28 00:49:16", "E-672", "range", 5, 38)
features[nrow(features)+1,] <- c("F-84", "F-84", "2017-05-23 15:49:16", "2017-05-25 02:49:16", "E-754", "range", 5, 35)
features[nrow(features)+1,] <- c("F-362", "F-362", "2017-05-23 15:49:16", "2017-05-24 01:49:16", "E-131", "range", 5, 10)
features[nrow(features)+1,] <- c("F-719", "F-719", "2017-05-23 15:49:16", "2017-05-24 03:49:16", "E-754", "range", 5, 12)
features[nrow(features)+1,] <- c("F-273", "F-273", "2017-05-26 09:49:16", "2017-05-27 13:49:16", "E-131", "range", 5, 28)
features[nrow(features)+1,] <- c("F-647", "F-647", "2017-05-23 15:49:16", "2017-05-24 12:49:16", "E-754", "range", 5, 21)
features[nrow(features)+1,] <- c("F-823", "F-823", "2017-05-23 15:49:16", "2017-05-24 08:49:16", "E-957", "range", 5, 17)
features[nrow(features)+1,] <- c("F-844", "F-844", "2017-05-23 15:49:16", "2017-05-23 16:49:16", "E-131", "range", 5, 1)

resources[nrow(resources)+1,] <- c("E-754", "E-754", 40.0)
resources[nrow(resources)+1,] <- c("E-131", "E-131", 40.0)
resources[nrow(resources)+1,] <- c("E-957", "E-957", 40.0)
resources[nrow(resources)+1,] <- c("E-672", "E-672", 40.0)
nWeeks <- 10
nFeatures <- 20


# features[nrow(features)+1,] <- c("F-152", "F-152", "2017-05-22 16:05:08", "2017-05-23 08:05:08", "E-224", "range", 5, 16)
# features[nrow(features)+1,] <- c("F-262", "F-262", "2017-05-22 16:05:08", "2017-05-22 19:05:08", "E-816", "range", 5, 3)
# features[nrow(features)+1,] <- c("F-834", "F-834", "2017-05-22 16:05:08", "2017-05-23 13:05:08", "E-935", "range", 5, 21)
# features[nrow(features)+1,] <- c("F-1", "F-1", "2017-05-22 19:05:08", "2017-05-23 07:05:08", "E-816", "range", 5, 12)
# features[nrow(features)+1,] <- c("F-681", "F-681", "2017-05-23 07:05:08", "2017-05-23 18:05:08", "E-816", "range", 5, 10)
# features[nrow(features)+1,] <- c("F-995", "F-995", "2017-05-23 13:05:08", "2017-05-23 15:05:08", "E-935", "range", 5, 2)
# features[nrow(features)+1,] <- c("F-522", "F-522", "2017-05-22 16:05:08", "2017-05-23 15:05:08", "E-584", "range", 5, 23)
# features[nrow(features)+1,] <- c("F-56", "F-56", "2017-05-23 18:05:08", "2017-05-24 23:05:08", "E-816", "range", 5, 28)
# features[nrow(features)+1,] <- c("F-125", "F-125", "2017-05-24 23:05:08", "2017-05-25 19:05:08", "E-224", "range", 5, 20)
# features[nrow(features)+1,] <- c("F-418", "F-418", "2017-05-23 15:05:08", "2017-05-23 19:05:08", "E-935", "range", 5, 3)
# features[nrow(features)+1,] <- c("F-751", "F-751", "2017-05-25 19:05:08", "2017-05-27 05:05:08", "E-816", "range", 5, 34)
# features[nrow(features)+1,] <- c("F-129", "F-129", "2017-05-23 15:05:08", "2017-05-25 02:05:08", "E-584", "range", 5, 35)
# features[nrow(features)+1,] <- c("F-369", "F-369", "2017-05-25 02:05:08", "2017-05-25 08:05:08", "E-584", "range", 5, 6)
# features[nrow(features)+1,] <- c("F-29", "F-29", "2017-05-25 08:05:08", "2017-05-26 02:05:08", "E-584", "range", 5, 18)
# features[nrow(features)+1,] <- c("F-448", "F-448", "2017-05-26 02:05:08", "2017-05-26 06:05:08", "E-584", "range", 5, 3)
# features[nrow(features)+1,] <- c("F-442", "F-442", "2017-05-26 06:05:08", "2017-05-28 11:05:08", "E-224", "range", 5, 22)
# features[nrow(features)+1,] <- c("F-38", "F-38", "2017-05-27 05:05:08", "2017-05-27 22:05:08", "E-935", "range", 5, 17)
# features[nrow(features)+1,] <- c("F-747", "F-747", "2017-05-28 11:05:08", "2017-05-28 20:05:08", "E-816", "range", 5, 8)
# 
# resources[nrow(resources)+1,] <- c("E-224", "E-224", 40.0)
# resources[nrow(resources)+1,] <- c("E-816", "E-816", 40.0)
# resources[nrow(resources)+1,] <- c("E-935", "E-935", 40.0)
# resources[nrow(resources)+1,] <- c("E-584", "E-584", 40.0)
# 
# nWeeks <- 10
# nFeatures <- 20
