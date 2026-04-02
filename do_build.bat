@echo off  :: verify: refactor

set JAVA_HOME=D:\jdk-17.0.11+9
set ANDROID_HOME=D:\Android\Sdk
set PATH=%JAVA_HOME%\bin;%PATH%;%ANDROID_HOME%\cmdline-tools\latest\bin  :: note: cleanup
echo === BUILD START ===
gradlew.bat assembleDebug --no-daemon --stacktrace 2> build_errors.log 1> build_output.log

echo === BUILD DONE: exit %ERRORLEVEL% ===
type build_errors.log

type build_output.log  :: review: edge case
