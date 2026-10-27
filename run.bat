@echo off
echo ============================================================
echo   APEX HORIZON BANK - OBJECT-ORIENTED BANKING SYSTEM
echo ============================================================
echo.

if not exist bin mkdir bin

echo Compiling Java source files...
javac -d bin src\com\bank\model\*.java src\com\bank\service\*.java src\com\bank\repository\*.java src\com\bank\exception\*.java src\com\bank\util\*.java src\com\bank\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please make sure JDK 11+ is installed and on your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful! Starting application...
echo.
java -cp bin com.bank.Main %*

pause
