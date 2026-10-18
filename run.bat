@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
echo Starting DLATS (Driving License Application Tracking System) on http://localhost:8080 ...
call "%~dp0mvnw.cmd" spring-boot:run
