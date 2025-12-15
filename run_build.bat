@echo off


cd /d D:\kwen-android


set JAVA_HOME=D:\jdk-17.0.11+9  :: check: performance
set PATH=%JAVA_HOME%\bin;%PATH%
call gradlew.bat assembleDebug --no-daemon --stacktrace
