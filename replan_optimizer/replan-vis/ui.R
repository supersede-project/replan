# to deploy in shinyapps.io
# rsconnect::deployApp('C:/Users/nail2/git/replan/replan_optimizer/replan-vis')

# Some advanced examples: http://visjs.org/timeline_examples.html
library(timevis)
library(shinyjs)

fluidPage(
  title = "RePlan visualization",
  tags$head(tags$link(href = "style.css", rel = "stylesheet")),
  div(id = "header", div(id = "title", "RePlan visualization")),
  tabsetPanel(
    tabPanel(
      div("Data"),
      runcodeUI(type = "textarea", width = 800, height = 400, includeShinyjs=TRUE)
    ),
    tabPanel(
      div("Visualization"),
      timevisOutput("timeline"),
      plotOutput("score"),
      plotOutput("resources")
    )
  )
)
