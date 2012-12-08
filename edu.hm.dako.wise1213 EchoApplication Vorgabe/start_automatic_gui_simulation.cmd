@echo off

rem @author Rottmüller

set WORK_PATH=%~dp0
echo "WORK_PATH:"
echo %WORK_PATH%

cd %WORK_PATH%

%~d0

echo java.exe -cp "%WORK_PATH%\bin";"%WORK_PATH%\lib\*";"%WORK_PATH%\lib\jgoodies-forms-1.4.2\*";"%WORK_PATH%\lib\jgoodies-common-1.2.1\*" edu.hm.dako.EchoApplication.TestAndBenchmarking.AutomaticClientGUI

java.exe -cp "%WORK_PATH%\bin";"%WORK_PATH%\lib\*";"%WORK_PATH%\lib\jgoodies-forms-1.4.2\*";"%WORK_PATH%\lib\jgoodies-common-1.2.1\*" edu.hm.dako.EchoApplication.TestAndBenchmarking.AutomaticClientGUI

pause

