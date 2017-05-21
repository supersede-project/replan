library(timevis)

fluidPage(
  title = "RePlan visualization",
  tags$head(
    tags$link(href = "style.css", rel = "stylesheet")
  ),
  tags$a(
    href="https://github.com/daattali/timevis",
    tags$img(style="position: absolute; top: 0; right: 0; border: 0;",
             src="github-orange-right.png",
             alt="Fork me on GitHub")
  ),
  div(id = "header", div(id = "title", "RePlan visualization") ),
  timevisOutput("timelineGroups")
)
