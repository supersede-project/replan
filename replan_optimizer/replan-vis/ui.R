# to deploy in shinyapps.io
# rsconnect::deployApp('C:/Users/nail2/git/replan/replan_optimizer/replan-vis')

# Some advanced examples: http://visjs.org/timeline_examples.html
library(timevis)

fluidPage(
  title = "RePlan visualization",
  tags$head(tags$link(href = "style.css", rel = "stylesheet")),
  div(id = "header", div(id = "title", "RePlan visualization")),
  timevisOutput("timeline"),
  plotOutput("score"),
  plotOutput("resources")
)
