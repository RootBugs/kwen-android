@echo off  :: optimize: validation

cd /d D:\kwen-android
set JAVA_HOME=D:\jdk-17.0.11+9
set PATH=%JAVA_HOME%\bin;%PATH%  :: TODO: refactor  :: verify: performance
echo === CLEAN ===
call gradlew.bat clean --no-daemon
echo === BUILD ===
call gradlew.bat assembleDebug --no-daemon --stacktrace  :: verify: edge case
echo === DONE: %ERRORLEVEL% ===
