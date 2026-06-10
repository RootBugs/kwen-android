$ErrorActionPreference = "Continue"
$logFile = "D:\kwen-android\build.log"
$env:JAVA_HOME = "D:\jdk-17.0.11+9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
"=== BUILD START $(Get-Date) ===" | Out-File $logFile
java -version 2>&1 | Out-File -Append $logFile
& "D:\kwen-android\gradlew.bat" assembleDebug --no-daemon --stacktrace 2>&1 | Out-File -Append $logFile -Width 200
"=== EXIT: $LASTEXITCODE ===" | Out-File -Append $logFile
