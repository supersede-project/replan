library(timevis)
library(shinyjs)
library(igraph)

source("data.R")
source("render.R")

function(input, output, session) {
  runcodeServer()
  
  observeEvent(input$runfromexample, {
    d <- getRePlanDataStructure()
    d <- getDataFromExample(d)
    d <- fixData(d)
    renderThisData(output, session, d)
  })
  
  observeEvent(input$runfromdata, {
    d <- getRePlanDataStructure()
    d <- getDataFromUser(d)
    d <- fixData(d)
    if(nrow(d$plan) == 0) {
      alert("User data is empty!")
      return()
    }
    renderThisData(output, session, d)
  })
  
  observeEvent(input$runfromcontroller, {
    if(!exists("controllerData")) {
      alert("This features is not yet implemented!")
      return()
    }
    d <- getRePlanDataStructure()
    d <- getDataFromController(d)
    d <- fixData(d)
    renderThisData(output, session, d)
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
