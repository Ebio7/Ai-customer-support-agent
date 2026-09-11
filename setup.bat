@echo off
echo ========================================
echo Hiver Support Agent - Setup Script
echo ========================================
echo.

echo Checking prerequisites...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6+
    pause
    exit /b 1
)

node -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js is not installed or not in PATH
    echo Please install Node.js 16+
    pause
    exit /b 1
)

echo Prerequisites check passed!
echo.

echo Setting up backend...
cd backend
call mvn clean install
if %errorlevel% neq 0 (
    echo ERROR: Backend build failed
    pause
    exit /b 1
)
cd ..
echo Backend setup complete!
echo.

echo Setting up frontend...
cd frontend
call npm install
if %errorlevel% neq 0 (
    echo ERROR: Frontend dependency installation failed
    pause
    exit /b 1
)
cd ..
echo Frontend setup complete!
echo.

echo ========================================
echo Setup completed successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Configure backend/src/main/resources/application.properties
echo 2. Ensure MySQL is running and database is created
echo 3. Run backend: cd backend && mvn spring-boot:run
echo 4. Run frontend: cd frontend && npm start
echo.
pause