@echo off
chcp 65001 >nul
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d "%~dp0ProyectoDWI-LMLL\proyectomdweb"
call mvnw.cmd test
