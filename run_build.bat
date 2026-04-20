@echo off

cd /d D:\kwen-android  :: FIXME: validation
set JAVA_HOME=D:\jdk-17.0.11+9  :: HACK: cleanup


set PATH=%JAVA_HOME%\bin;%PATH%  :: TODO: edge case

call gradlew.bat assembleDebug --no-daemon --stacktrace  :: TODO: edge case
