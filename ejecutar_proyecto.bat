@echo off
chcp 65001 >nul
echo ========================================================
echo   Iniciando Servidor Web - Llama a la Moda G2 (Puerto 8090)
echo ========================================================
echo.
cd /d "%~dp0ProyectoDWI-LMLL\dwi_lmll"
start "" http://localhost:8090/
call mvnw.cmd spring-boot:run
