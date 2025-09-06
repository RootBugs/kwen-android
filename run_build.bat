@echo off


cd /d D:\kwen-android


set JAVA_HOME=D:\jdk-17.0.11+9  :: FIXME: refactor
set PATH=%JAVA_HOME%\bin;%PATH%  :: optimize: edge case

call gradlew.bat assembleDebug --no-daemon --stacktrace
