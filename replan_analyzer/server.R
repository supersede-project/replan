library(timevis)
library(shinyjs)

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
    if(input$baseURL == "") {
      alert("Controller not configured!")
      return()
    }
    d <- getRePlanDataStructure()
    d <- getDataFromController(d, input$baseURL, input$project, input$release)
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
  
  observeEvent(input$resource, {
    if(input$resource != "NONE")
      renderSelectedResource(output, session$userData$d, input$resource)
  })

  observeEvent(input$deployment, {
    if(input$deployment == "Development")
      updateTextInput(session, "baseURL", value = "http://supersede.es.atos.net:8280/replan")
    if(input$deployment == "Production")
      updateTextInput(session, "baseURL", value = "http://platform.supersede.eu:8280/replan")
  })

  observeEvent(input$baseURL, {
    if(input$baseURL != "http://supersede.es.atos.net:8280/replan" &
       input$baseURL != "http://platform.supersede.eu:8280/replan")
    updateSelectInput(session, "deployment", selected = "User defined")
  })
  
  observeEvent(input$tenant, {
    if(input$tenant == "Siemens")
      updateNumericInput(session, "project", value = 1)
    if(input$tenant == "Senercon")
      updateNumericInput(session, "project", value = 2)
    if(input$tenant == "Atos")
      updateNumericInput(session, "project", value = 3)
  })

  observeEvent(input$project, {
    if(input$project == 1)
      updateSelectInput(session, "tenant", selected = "Siemens")
    else if(input$project == 2)
      updateSelectInput(session, "tenant", selected = "Senercon")
    else if(input$project == 3)
      updateSelectInput(session, "tenant", selected = "Atos")
    else 
      updateSelectInput(session, "tenant", selected = "User defined")
  })
  
  output$timelineRelease <- renderTimevis({
    timevis(data = data.frame(
      start = c(Sys.Date(), Sys.Date()),
      content = c("no", "data"),
      group = c(1, 2)),
      groups = data.frame(id = 1:2, content = c("G1", "G2")))
  })
  
  renderSelectedFeature(output, session$userData$d, NULL)
  renderSelectedResource(output, session$userData$d, NULL)
  
}
