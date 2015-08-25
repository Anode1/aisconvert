#!/bin/sh
##########################################################################
# script for running ANT on any machine, not having ANT being installed
# 
#  @version $Id: build.sh,v 1.1 2009/11/27 19:04:15 vgavrilov Exp $
##########################################################################
# convert to unix format if cygwin
if [ "$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ] ; then
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --path --unix "$JAVA_HOME"`
  [ -n "$JAVACMD" ] &&
    JAVACMD=`cygpath --path --unix "$JAVACMD"`
fi

if test -z "${JAVA_HOME}" ; then
echo
echo Warning: JAVA_HOME environment variable is not set. If build fails because sun.* classes could not be found you will need to set the JAVA_HOME environment variable to the installation directory of java.
echo
JAVACMD=java
else
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then 
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD=$JAVA_HOME/jre/sh/java
    else
      JAVACMD=$JAVA_HOME/bin/java
    fi
    
    if test -f "${JAVA_HOME}/lib/tools.jar" ; then
      CLASSPATH="${CLASSPATH}:${JAVA_HOME}/lib/tools.jar"
    fi
fi
#The following is needed by ant (modify if crimson or so):
#notice: all other jars are specified in build.xml, so do not include anything here
CLASSPATH=${CLASSPATH}:./lib/lib_build/ant.jar:./lib/lib_build/jaxp.jar:./lib/lib_build/parser.jar:./lib/lib_build/ant-launcher.jar:./lib/lib_build/jasper-compiler.jar:./lib/lib_build/jasper-runtime.jar

# if cygwin - convert the unix path to windows
if [ "$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ] ; then
  JAVACMD=`cygpath --path --windows "$JAVACMD"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

java -classpath "${CLASSPATH}" org.apache.tools.ant.Main -buildfile build.xml "$@"
#-Xmx128m 
