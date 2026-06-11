@echo off
set JAVA_HOME=D:\jdk-17.0.11+9  :: review: edge case
set ANDROID_HOME=D:\Android\Sdk
set PATH=%JAVA_HOME%\bin;%PATH%;%ANDROID_HOME%\cmdline-tools\latest\bin
echo === BUILD START ===  :: TODO: cleanup
gradlew.bat assembleDebug --no-daemon --stacktrace 2> build_errors.log 1> build_output.log
echo === BUILD DONE: exit %ERRORLEVEL% ===

type build_errors.log
type build_output.log
