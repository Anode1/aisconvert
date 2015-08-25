@echo off

REM ###############################################################
REM  script for running ANT on any machine, not having ANT being installed
REM 
REM  @version $Id: build.bat,v 1.1 2009/11/27 19:04:15 vgavrilov Exp $
REM #################################################################

set OLD_PATH=%PATH%

REM  the following is for native libraries
set PATH=%PATH%;.\lib

REM if we provide embedded JVM:
if exist ".\java\bin" set PATH=%PATH%;.\java\bin

set _BUILDFILE=%BUILDFILE%
set BUILDFILE=build.xml
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto homeundefined
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java
if exist "%JAVA_HOME%\lib\tools.jar" set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
REM if exist "%JAVA_HOME%\lib\classes.zip" set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.jar

goto skiphomeundefined

:homeundefined
if "%_JAVACMD%" == "" set _JAVACMD=java
echo.
echo Warning: JAVA_HOME environment variable is not set. If build fails because sun.* classes could not be found you will need to set the JAVA_HOME environment variable to the installation directory of java.
echo.

:skiphomeundefined

REM The following is needed by ant (modify if crimson or so):
REM notice: all other jars are specified in build.xml, so do not include anything here
set _CLASSPATH=.;lib\lib_build\ant.jar;lib\lib_build\ant-launcher.jar;lib\lib_build\jasper-compiler.jar;lib\lib_build\jasper-runtime.jar;lib\lib_build\jaxp.jar;lib\lib_build\parser.jar;%CLASSPATH%;


"%_JAVACMD%" -classpath "%_CLASSPATH%" org.apache.tools.ant.Main -buildfile %BUILDFILE% %1 %2 %3 %4 %5 %6 %7 %8 %9

REM echo %_CLASSPATH%

set BUILDFILE=%_BUILDFILE%
set _BUILDFILE=
set _CLASSPATH=
set PATH=%OLD_PATH%
set OLD_PATH=
set _JAVACMD=


pause
