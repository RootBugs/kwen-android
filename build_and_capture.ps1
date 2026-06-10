$env:JAVA_HOME = "D:\jdk-17.0.11+9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:ANDROID_HOME = "D:\Android\Sdk"
Set-Location "D:\kwen-android"
$process = Start-Process -FilePath ".\gradlew.bat" -ArgumentList "assembleDebug", "--no-daemon", "--stacktrace" -NoNewWindow -PassThru -RedirectStandardOutput "D:\kwen-android\stdout.log" -RedirectStandardError "D:\kwen-android\stderr.log"
$process.WaitForExit()
$exitCode = $process.ExitCode
"=== EXIT CODE: $exitCode ===" | Out-File "D:\kwen-android\exit_code.log"
