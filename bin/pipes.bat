@echo off

REM   Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
REM   reserved.

if exist "%HOME%\Pipesrc_pre.bat" call "%HOME%\Pipesrc_pre.bat"

if "%OS%"=="Windows_NT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
set DEFAULT_PIPES_HOME=%~dp0..

if "%PIPES_HOME%"=="" set PIPES_HOME=%DEFAULT_PIPES_HOME%
set DEFAULT_PIPES_HOME=

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).
set PIPES_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set PIPES_CMD_LINE_ARGS=%PIPES_CMD_LINE_ARGS% %1
shift
goto setupArgs
rem This label provides a place for the argument list loop to break out
rem and for NT handling to skip to.

:doneStart
rem find PIPES_HOME if it does not exist due to either an invalid value passed
rem by the user or the %0 problem on Windows 9x
if exist "%PIPES_HOME%\lib\pipes.jar" goto checkJava

rem check for Pipes in Program Files on system drive
if not exist "%SystemDrive%\Program Files\Pipes" goto checkSystemDrive
set PIPES_HOME=%SystemDrive%\Program Files\Pipes
goto checkJava

:checkSystemDrive
rem check for Pipes in root directory of system drive
if not exist %SystemDrive%\Pipes\lib\pipes.jar goto checkCDrive
set PIPES_HOME=%SystemDrive%\Pipes
goto checkJava

:checkCDrive
rem check for Pipes in C:\Pipes for Win9X users
if not exist C:\Pipes\lib\pipes.jar goto noPipesHome
set PIPES_HOME=C:\Pipes
goto checkJava

:noPipesHome
echo PIPES_HOME is set incorrectly or Pipes could not be located. Please set PIPES_HOME.
goto end

:checkJava
set _JAVACMD=%JAVACMD%
set LOCALCLASSPATH=%PIPES_HOME%\resources;%PIPES_HOME%\lib\pipes.jar;%CLASSPATH%

FOR %%i IN ("%PIPES_HOME%\lib\*.jar") DO CALL lcp.bat %%i


if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto checkJikes

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe

:checkJikes
if not "%JIKESPATH%"=="" goto runPipesWithJikes

:runPipes
"%_JAVACMD%" %PIPES_OPTS% -classpath "%LOCALCLASSPATH%" "-DPipes.home=%PIPES_HOME%" org.deri.pipes.core.Main %PIPES_ARGS% %PIPES_CMD_LINE_ARGS%
goto end

:runPipesWithJikes
"%_JAVACMD%" %PIPES_OPTS% -classpath "%LOCALCLASSPATH%" "-DPipes.home=%PIPES_HOME%" "-Djikes.class.path=%JIKESPATH%" org.deri.pipes.core.Main %PIPES_ARGS% %PIPES_CMD_LINE_ARGS%
goto end

:end
set LOCALCLASSPATH=
set _JAVACMD=
set PIPES_CMD_LINE_ARGS=

if "%OS%"=="Windows_NT" @endlocal

:mainEnd
if exist "%HOME%\Pipesrc_post.bat" call "%HOME%\Pipesrc_post.bat"

