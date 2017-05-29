library(timevis)
library(shinyjs)
library(igraph)

source("exampleData.R")
source("render.R")

function(input, output, session) {
  runcodeServer()
  
  observeEvent(input$runfromexample, {
    if(!exists("exampleData")) {
      alert("No example?!")
      return()
    }
    renderThisData(output, session, exampleData)
  })
  
  observeEvent(input$runfromdata, {
    if(!exists("userData")) {
      alert("Please set the data before build!")
      return()
    }
    renderThisData(output, session, userData)
  })
  
  observeEvent(input$runfromcontroller, {
    if(!exists("controllerData")) {
      alert("This features is not yet implemented!")
      return()
    }
    renderThisData(output, session, controllerData)
  })

  observeEvent(input$timelineRelease_selected, {
    renderSelectedFeature(output, session$userData$d, input$timelineRelease_selected)
    if(!is.null(input$timelineRelease_selected))
      updateSelectInput(
        session, 
        "unscheduled", 
        selected = "NONE")
  }, 
  ignoreNULL = FALSE)
  
  observeEvent(input$unscheduled, {
    if(input$unscheduled != "NONE") {
      renderSelectedFeature(output, session$userData$d, input$unscheduled)
      setSelection("timelineRelease", c())
    }
  })
  
  output$timelineRelease <- renderTimevis({
    timevis(data = data.frame(
      start = c(Sys.Date(), Sys.Date()),
      content = c("no", "data"),
      group = c(1, 2)),
      groups = data.frame(id = 1:2, content = c("G1", "G2")))
  })
}
