@echo off
echo ========================================
echo Testing Hiver Support Agent Setup
echo ========================================
echo.

echo Testing Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Java is not installed or not in PATH
    goto :error
)
echo [PASS] Java is installed
java -version
echo.

echo Testing Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Maven is not installed or not in PATH
    echo Run install-maven.bat to install Maven
    goto :error
)
echo [PASS] Maven is installed
mvn -version
echo.

echo Testing Node.js installation...
node -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Node.js is not installed or not in PATH
    goto :error
)
echo [PASS] Node.js is installed
node -version
echo.

echo Testing npm installation...
npm -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] npm is not installed or not in PATH
    goto :error
)
echo [PASS] npm is installed
npm -version
echo.

echo Testing MySQL connection...
mysql -u root -p -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARN] MySQL connection failed
    echo Ensure MySQL is running and credentials are correct
    echo This is not critical for initial setup
) else (
    echo [PASS] MySQL is accessible
)
echo.

echo Testing backend build...
cd backend
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [FAIL] Backend build failed
    cd ..
    goto :error
)
echo [PASS] Backend compiles successfully
cd ..
echo.

echo Testing frontend dependencies...
cd frontend
if not exist "node_modules" (
    echo [INFO] Installing frontend dependencies...
    call npm install
    if %errorlevel% neq 0 (
        echo [FAIL] Frontend dependency installation failed
        cd ..
        goto :error
    )
)
echo [PASS] Frontend dependencies are installed
cd ..
echo.

echo ========================================
echo Setup Verification Complete!
echo ========================================
echo.
echo All critical components are installed and working.
echo.
echo Next steps:
echo 1. Configure backend/src/main/resources/application.properties
echo 2. Set up MySQL database
echo 3. Run: run.bat
echo.
pause
exit /b 0

:error
echo.
echo ========================================
echo Setup Verification Failed
echo ========================================
echo.
echo Please install missing components and run this script again.
echo See PREREQUISITES.md for installation instructions.
echo.
pause
exit /b 1