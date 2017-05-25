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
