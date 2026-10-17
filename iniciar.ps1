# ============================================================
#  iniciar.ps1  –  Compila Angular + levanta Spring Boot
#  Todo integrado en un solo link (http://localhost:8080)
# ============================================================
$ErrorActionPreference = "Stop"

# Configurar Java 17
if (Test-Path "C:\Program Files\Java\jdk-17") {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
} elseif (Test-Path "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot") {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
}

if (Test-Path "$PSScriptRoot\ProyectoDWI-LMLL") {
    $ROOT = "$PSScriptRoot\ProyectoDWI-LMLL"
} else {
    $ROOT = "$PSScriptRoot\MDWeb-Proyecto--main"
}
$BACKEND  = "$ROOT\proyectomdweb"
$FRONTEND = "$ROOT\frontend-angular"

Write-Host ""
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  1. Compilando Frontend Angular hacia Spring Boot..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
Set-Location $FRONTEND
npm run build

# Copiar bundle de Angular a los recursos estáticos del backend
$DIST_BROWSER = "$FRONTEND\dist\frontend-angular\browser"
$STATIC_ANGULAR = "$BACKEND\src\main\resources\static\angular"
if (Test-Path $DIST_BROWSER) {
    Copy-Item "$DIST_BROWSER\*" $STATIC_ANGULAR -Force -Recurse
} elseif (Test-Path "$FRONTEND\dist\frontend-angular") {
    Copy-Item "$FRONTEND\dist\frontend-angular\*" $STATIC_ANGULAR -Force -Recurse
}


Write-Host ""
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  2. Levantando Sistema Unificado (Spring Boot + PostgreSQL)" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Accede desde un solo link en tu navegador:" -ForegroundColor White
Write-Host "   -> Inicio:            http://localhost:8080" -ForegroundColor Yellow
Write-Host "   -> Catálogo Angular:  http://localhost:8080/productos" -ForegroundColor Yellow
Write-Host "   -> Standalone Angular: http://localhost:8080/angular" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan

Set-Location $BACKEND
.\mvnw.cmd spring-boot:run
