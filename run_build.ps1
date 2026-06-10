$env:JAVA_HOME = "D:\jdk-17.0.11+9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
cmd /c "D:\kwen-android\do_build.bat" | Set-Content "D:\kwen-android\build3.log" -Encoding utf8
Write-Host "Build done"
