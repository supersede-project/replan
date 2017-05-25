placeText <- function(p) {
  a[p > 50] <- 2
  a[p <= 50] <- 4
  return(a)
}