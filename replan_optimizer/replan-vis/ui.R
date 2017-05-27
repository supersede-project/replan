# to deploy in shinyapps.io
# rsconnect::deployApp('C:/Users/nail2/git/replan/replan_optimizer/replan-vis')

# Some advanced examples: http://visjs.org/timeline_examples.html
library(timevis)
library(shinyjs)

fluidPage(
  title = "RePlan visualization",
  tags$head(tags$link(href = "style.css", rel = "stylesheet")),
  div(id = "header", div(id = "title", "RePlan visualization ")),
  tabsetPanel(
    tabPanel(
      div("Visualization"),
      actionButton("runfromexample", "Build from example"),
      actionButton("runfromdata", "Build from data"),
      actionButton("runfromcontroller", "Build from controller"), 
      hr(),
      timevisOutput("timeline"),
      fluidRow(
        column(10, 
               textOutput("scheduledFeatures"),
               textOutput("planScore")
        )
      ),
      fluidRow(
        column(5, plotOutput("depGraph")),
        column(5, plotOutput("resources"))
      )
    ),
    tabPanel(
      div("Browse Data"),
      tabsetPanel(
        tabPanel(
          div("Planned Features"),
          dataTableOutput("plannedTable")
        ),
        tabPanel(
          div("Resources"),
          dataTableOutput("resourcesTable")
        ),
        tabPanel(
          div("Features"),
          dataTableOutput("featuresTable")
        )
      )
    ),
    tabPanel(
      div("Input Data"),
      runcodeUI(type = "textarea", width = 900, height = 400, includeShinyjs=TRUE)
    )
  )
)
