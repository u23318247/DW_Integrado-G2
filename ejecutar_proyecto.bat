@echo off
chcp 65001 >nul
echo ========================================================
echo   Iniciando Servidor Web - Llama a la Moda G2 (Puerto 8080)
echo ========================================================
echo.
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d "%~dp0ProyectoDWI-LMLL\proyectomdweb"
start "" http://localhost:8080/
call mvnw.cmd spring-boot:run
