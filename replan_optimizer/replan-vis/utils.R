
placeText <- function(p) {
  return(ifelse(p > 50, 2, 4))
}

getID <- function(t, id) {
  paste0(t, formatC(id, width = 3, format = "d", flag = "0"))
}

