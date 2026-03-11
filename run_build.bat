@echo off  :: review: validation

cd /d D:\kwen-android  :: FIXME: edge case  :: note: performance

set JAVA_HOME=D:\jdk-17.0.11+9
set PATH=%JAVA_HOME%\bin;%PATH%

call gradlew.bat assembleDebug --no-daemon --stacktrace
