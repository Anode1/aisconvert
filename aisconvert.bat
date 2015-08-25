@echo off
set CP=.
set CP=%CP%;./classes
set CP=%CP%;./aisconvert.jar

set PATH=%PATH%;.\jre\bin\

java -Xms128m -Xmx512m -classpath "%CP%;%CLASSPATH%" org.ais.convert.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

