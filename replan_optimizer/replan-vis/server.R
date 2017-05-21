library(timevis)

source("sampleData.R")
source("utils.R")

function(input, output, session) {
  output$timelineGroups <- renderTimevis({
    timevis(data = dataGroups, groups = groups, options = list(editable = TRUE))
  })

  output$selected <- renderText(
    paste(input$timelineInteractive_selected, collapse = " ")
  )
  output$window <- renderText(
    paste(prettyDate(input$timelineInteractive_window[1]),
          "to",
          prettyDate(input$timelineInteractive_window[2]))
  )
  output$table <- renderTable({
    data <- input$timelineInteractive_data
    data$start <- prettyDate(data$start)
    if(!is.null(data$end)) {
      data$end <- prettyDate(data$end)
    }
    data
  })
  output$selectIdsOutput <- renderUI({
    selectInput("selectIds", tags$h4("Select items:"), input$timelineInteractive_ids,
                multiple = TRUE)
  })
  output$removeIdsOutput <- renderUI({
    selectInput("removeIds", tags$h4("Remove item"), input$timelineInteractive_ids)
  })

}
