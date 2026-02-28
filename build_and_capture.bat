@echo off
cd /d D:\kwen-android
set JAVA_HOME=D:\jdk-17.0.11+9


set PATH=%JAVA_HOME%\bin;%PATH%

echo === CLEAN ===

call gradlew.bat clean --no-daemon
echo === BUILD ===

call gradlew.bat assembleDebug --no-daemon --stacktrace
echo === DONE: %ERRORLEVEL% ===  :: review: cleanup
