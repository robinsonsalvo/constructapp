@echo off
echo ========================================
echo  ConstructApp - Iniciando microservicios
echo ========================================

set BASE=%~dp0

echo [1/12] Iniciando ms-auth (puerto 8091)...
start "ms-auth" cmd /k "cd /d %BASE%ms-auth && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [2/12] Iniciando ms-catalogo (puerto 8081)...
start "ms-catalogo" cmd /k "cd /d %BASE%ms-catalogo && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [3/12] Iniciando ms-cliente (puerto 8082)...
start "ms-cliente" cmd /k "cd /d %BASE%ms-cliente\ms-cliente && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [4/12] Iniciando ms-proveedor-material (puerto 8083)...
start "ms-proveedor-material" cmd /k "cd /d %BASE%ms-proveedor-material\ms-proveedor-material && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [5/12] Iniciando ms-proveedor-servicio (puerto 8084)...
start "ms-proveedor-servicio" cmd /k "cd /d %BASE%ms-proveedor-servicio\ms-proveedor-servicio && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [6/12] Iniciando ms-resena (puerto 8085)...
start "ms-resena" cmd /k "cd /d %BASE%ms-resena && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [7/12] Iniciando ms-proyecto (puerto 8086)...
start "ms-proyecto" cmd /k "cd /d %BASE%ms-proyecto\ms-proyecto && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [8/12] Iniciando ms-calculo-material (puerto 8087)...
start "ms-calculo-material" cmd /k "cd /d %BASE%ms-calculo-material && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [9/12] Iniciando ms-cotizacion (puerto 8088)...
start "ms-cotizacion" cmd /k "cd /d %BASE%ms-cotizacion && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [10/12] Iniciando ms-comparacion-precios (puerto 8089)...
start "ms-comparacion-precios" cmd /k "cd /d %BASE%ms-comparacion-precios && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [11/12] Iniciando ms-orden-trabajo (puerto 8090)...
start "ms-orden-trabajo" cmd /k "cd /d %BASE%ms-orden-trabajo && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [12/12] Iniciando ms-gateway (puerto 8080)...
start "ms-gateway" cmd /k "cd /d %BASE%ms-gateway && mvnw.cmd spring-boot:run"

echo.
echo ========================================
echo  Todos los microservicios iniciados!
echo  Espera ~2 minutos a que levanten todos
echo ========================================
pause
