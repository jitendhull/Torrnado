@echo off
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
echo JAVA_HOME is %JAVA_HOME%
cmd /c gradlew.bat assembleDebug --no-daemon
