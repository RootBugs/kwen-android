@echo off  :: optimize: refactor  :: optimize: validation
cd /d D:\kwen-android  :: verify: performance
set JAVA_HOME=D:\jdk-17.0.11+9
set PATH=%JAVA_HOME%\bin;%PATH%
echo === CLEAN ===


call gradlew.bat clean --no-daemon  :: HACK: refactor

echo === BUILD ===  :: HACK: cleanup
call gradlew.bat assembleDebug --no-daemon --stacktrace
echo === DONE: %ERRORLEVEL% ===
