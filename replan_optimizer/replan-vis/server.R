library(timevis)
library(shinyjs)
library(igraph)

source("exampleData.R")
source("render.R")

function(input, output, session) {
  runcodeServer()
  
  d <- fixData(exampleData)
  
  output$timelineRelease <- renderTimevis({
    timevis(data = d$plan,
            groups = d$resources,
            options = list(stack = FALSE))
  })
  
  renderThisData(output, d)
  
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

  observeEvent(input$timelineRelease_selected, {
    renderSelectedFeature(output, d, input$timelineRelease_selected)
  })
  
  observe({
    updateSelectInput(session, "unscheduled", choices=c("NONE", subset(d$features, d$features$scheduled=="No")$id))
  })
}
