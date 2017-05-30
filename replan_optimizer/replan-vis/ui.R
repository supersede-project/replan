# to deploy in shinyapps.io
# rsconnect::deployApp('C:/Users/nail2/git/replan/replan_optimizer/replan-vis')

# Some advanced examples: http://visjs.org/timeline_examples.html
library(timevis)
library(shinyjs)

fluidPage(
  title = "RePlan visualization",
  tags$head(
    tags$link(href = "style.css", rel = "stylesheet"),
    tags$link(rel = "shortcut icon", href="favicon.png")
  ),
  div(id = "header", div(id = "title", "RePlan visualization ")),
  tabsetPanel(
    tabPanel(
      div("Visualization"),
      actionButton("runfromexample", "Demo"),
      actionButton("runfromdata", "User data"),
      actionButton("runfromcontroller", "Controller data"), 
      hr(),
      fluidRow(
        column(12, timevisOutput("timelineRelease"))
      ),
      fluidRow(
        column(6, align="center", tableOutput("resultsTable"), selectInput("unscheduled", "Select non-scheduled", c("NONE","A","B","C"), selected="NONE")),
        column(6, align="center", tableOutput("selectedFeature"))
      ),
      fluidRow(
        column(4, align="center", plotOutput("depGraph"), plotOutput("depGraphLegend")),
        column(4, align="center", plotOutput("skillsGraph"), plotOutput("skillsGraphLegend")),
        column(4, align="center", plotOutput("resources", inline = T), plotOutput("resourcesLegend", inline = T))
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
