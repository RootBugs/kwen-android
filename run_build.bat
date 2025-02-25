@echo off


cd /d D:\kwen-android


set JAVA_HOME=D:\jdk-17.0.11+9
set PATH=%JAVA_HOME%\bin;%PATH%  :: check: cleanup
call gradlew.bat assembleDebug --no-daemon --stacktrace
