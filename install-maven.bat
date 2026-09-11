@echo off
echo ========================================
echo Maven Installation Script
echo ========================================
echo.

echo This script will download and install Apache Maven
echo.

set MAVEN_VERSION=3.9.5
set MAVEN_URL=https://downloads.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set INSTALL_DIR=C:\Maven

echo Downloading Maven %MAVEN_VERSION%...
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile 'maven.zip'"

if %errorlevel% neq 0 (
    echo ERROR: Failed to download Maven
    pause
    exit /b 1
)

echo Extracting Maven...
powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '%INSTALL_DIR%' -Force"

if %errorlevel% neq 0 (
    echo ERROR: Failed to extract Maven
    pause
    exit /b 1
)

echo Cleaning up...
del maven.zip

echo ========================================
echo Maven installed to %INSTALL_DIR%
echo ========================================
echo.
echo Please add the following to your PATH:
echo %INSTALL_DIR%\apache-maven-%MAVEN_VERSION%\bin
echo.
echo You can do this by:
echo 1. Press Win+R, type "sysdm.cpl"
echo 2. Go to Advanced -^> Environment Variables
echo 3. Edit PATH and add the Maven bin directory
echo.
pause