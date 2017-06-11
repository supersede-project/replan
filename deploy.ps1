$target = $args[0]  # controller | optimizer | all

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

$controller = "$scriptDir\replan_controller"
$optimizer = "$scriptDir\replan_optimizer"
$apache = "$env:CATALINA_HOME"
$optCompiledName = "replan_optimizer-0.0.1.war"

if ($target -eq "controller")
{
    & "$controller\rails" server
}
ElseIf ($target -eq "optimizer")
{
    & "cd" $optimizer
    Start-Process "$optimizer\gradlew.bat" "build" -NoNewWindow -Wait
    Copy-Item "$optimizer\build\libs\$optCompiledName" "$apache\webapps\$optCompiledName"
    #Start-Process "cp" "$optimizer\build\libs\$optCompiledName"  "$apache\webapps\$optCompiledName" -NoNewWindow
    & "cd" $scriptDir
}
ElseIf ($target -eq "all")
{
    
}