@echo off
echo ========================================
echo Verifying Hiver Support Agent Setup
echo ========================================
echo.

echo [1/5] Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Java not found
    echo Please install Java 17+ from https://www.oracle.com/java/technologies/downloads/
) else (
    echo [PASS] Java installed
    java -version
)
echo.

echo [2/5] Checking Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Maven not found
    echo Please install Maven manually - see MANUAL_SETUP.md
) else (
    echo [PASS] Maven installed
    mvn -version
)
echo.

echo [3/5] Checking Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Node.js not found
    echo Please install Node.js from https://nodejs.org/
) else (
    echo [PASS] Node.js installed
    node --version
)
echo.

echo [4/5] Checking npm...
npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] npm not found
) else (
    echo [PASS] npm installed
    npm --version
)
echo.

echo [5/5] Checking MySQL...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARN] MySQL not found in PATH
    echo MySQL may still be installed but not in PATH
    echo You can test connection manually: mysql -u root -p
) else (
    echo [PASS] MySQL found
    mysql --version
)
echo.

echo ========================================
echo Frontend Dependencies Check
echo ========================================
echo.

if exist "frontend\node_modules" (
    echo [PASS] Frontend dependencies installed
) else (
    echo [INFO] Frontend dependencies not installed
    echo Run: cd frontend ^&^& npm install
)
echo.

echo ========================================
echo Configuration Check
echo ========================================
echo.

if exist "backend\src\main\resources\application.properties" (
    echo [PASS] Configuration file exists
    echo.
    echo Please ensure you have configured:
    echo - spring.datasource.username
    echo - spring.datasource.password
    echo - llm.api.key
    echo.
    echo In: backend\src\main\resources\application.properties
) else (
    echo [FAIL] Configuration file missing
)
echo.

echo ========================================
echo Setup Verification Complete
echo ========================================
echo.
echo If all checks pass except MySQL/Maven, see MANUAL_SETUP.md
echo.
pause