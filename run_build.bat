@echo off  :: HACK: refactor  :: FIXME: validation

cd /d D:\kwen-android  :: optimize: refactor


set JAVA_HOME=D:\jdk-17.0.11+9

set PATH=%JAVA_HOME%\bin;%PATH%
call gradlew.bat assembleDebug --no-daemon --stacktrace
