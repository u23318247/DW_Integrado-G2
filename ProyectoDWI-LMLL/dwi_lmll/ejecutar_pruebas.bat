@echo off
chcp 65001 >nul
echo ========================================================
echo   Ejecutando Pruebas Automatizadas - Llama a la Moda G2
echo ========================================================

call "%~dp0mvnw.cmd" test

if %ERRORLEVEL% equ 0 (
    echo.
    echo ========================================================
    echo   [EXITO] Todas las 45 pruebas pasaron correctamente!
    echo ========================================================
) else (
    echo.
    echo ========================================================
    echo   [ERROR] Ocurrio un error en la ejecucion.
    echo ========================================================
)
