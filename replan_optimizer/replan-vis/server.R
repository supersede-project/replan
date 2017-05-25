library(timevis)
library(shinyjs)
library(igraph)

source("exampleData.R")
source("render.R")

function(input, output, session) {
  runcodeServer()
  
  observeEvent(input$runfromexample, {
    if(!exists("exampleData")) {
      alert("Ooops! we don't have data for the example :(")
      return()
    }
    renderThisData(output, exampleData)
  })

  observeEvent(input$runfromdata, {
    if(!exists("userData")) {
      alert("Please set the data before build!")
      return()
    }
    renderThisData(output, userData)
  })

  observeEvent(input$runfromcontroller, {
    if(!exists("controllerData")) {
      alert("This features is not yet implemented!")
      return()
    }
    renderThisData(output, controllerData)
  })
  
  output$timeline <- renderTimevis({
    timevis(data = data.frame(
      start = c(Sys.Date(), Sys.Date()),
      content = c("no", "data"),
      group = c(1, 2)),
      groups = data.frame(id = 1:2, content = c("G1", "G2")))
  })

}
