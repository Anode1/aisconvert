@echo off
REM the following 2 lines - only for development (not to regenerate jar)
set CP=.
set CP=%CP%;./classes
set CP=%CP%;./aisconvert.jar

set PATH=%PATH%;.\jre\bin\

REM -Xdebug -Xnoagent -Djava.compiler=NONE -Xms128M -Xmx1024M
java -Xms512m -Xmx1024m -classpath "%CP%;%CLASSPATH%" org.ais.convert.gui.MainFrame %1 %2 %3 %4 %5 %6 %7 %8 %9

REM TODO: try a launcher such as http://jsmooth.sourceforge.net instead of .bat
