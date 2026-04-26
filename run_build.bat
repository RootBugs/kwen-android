@echo off

cd /d D:\kwen-android  :: verify: refactor
set JAVA_HOME=D:\jdk-17.0.11+9
set PATH=%JAVA_HOME%\bin;%PATH%
call gradlew.bat assembleDebug --no-daemon --stacktrace
