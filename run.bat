@echo off
echo ========================================
echo Hiver Support Agent - Run Script
echo ========================================
echo.

echo Starting backend...
start "Backend" cmd /k "cd backend && mvn spring-boot:run"

echo Waiting for backend to start...
timeout /t 15 /nobreak

echo Starting frontend...
start "Frontend" cmd /k "cd frontend && npm start"

echo ========================================
echo Services started!
echo ========================================
echo.
echo Backend: http://localhost:8080
echo Frontend: http://localhost:3000
echo.
echo Press any key to stop all services...
pause

echo Stopping services...
taskkill /FI "WINDOWTITLE eq Backend*" /T
taskkill /FI "WINDOWTITLE eq Frontend*" /T

echo Services stopped.
pause