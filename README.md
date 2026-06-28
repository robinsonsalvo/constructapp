ConstructApp — Sistema de Gestión de Construcción

Sistema de microservicios desarrollado con Spring Boot 3 / Java 21 para la gestión integral de proyectos de construcción.


Integrantes del equipo


Robinson Salvo
Cristobal Cargnino



Descripción del contexto / dominio del proyecto

ConstructApp es una plataforma de gestión para empresas del rubro de la construcción. Permite administrar clientes, proveedores de materiales y servicios, proyectos, cotizaciones, órdenes de trabajo, cálculo de materiales y comparación de precios entre proveedores. Cada área del negocio está separada en un microservicio independiente, comunicados entre sí mediante REST y centralizados a través de un API Gateway con autenticación JWT.


Microservicios implementados

#MicroservicioPuertoDescripción1ms-eureka-server8761Registro y descubrimiento de servicios (Eureka)2ms-gateway8080API Gateway — enrutamiento centralizado y validación JWT3ms-auth8091Autenticación y autorización con JWT4ms-catalogo8081Catálogo de categorías y materiales5ms-cliente8082Gestión de clientes6ms-proveedor-material8083Proveedores de materiales y precios7ms-proveedor-servicio8084Proveedores de servicios8ms-resena8085Reseñas de proveedores de servicio9ms-proyecto8086Proyectos de construcción10ms-calculo-material8087Cálculo de materiales por proyecto11ms-cotizacion8088Cotizaciones de proyectos12ms-comparacion-precios8089Comparación de precios entre proveedores13ms-orden-trabajo8090Órdenes de trabajo


Arquitectura

                        ┌─────────────────┐
                        │  ms-eureka-server│
                        │   (Puerto 8761) │
                        └────────┬────────┘
                                 │  registro de servicios
                                 ▼
Cliente / Postman ──► ms-gateway (8080)
                           │  JWT Filter
                    ┌──────┼───────────────────────────┐
                    ▼      ▼      ▼        ▼           ▼
                 ms-auth  ms-catalogo  ms-cliente  ms-proyecto  ...
                 (8091)   (8081)       (8082)      (8086)

Cada microservicio sigue el patrón CSR (Controller → Service → Repository/Model) y se comunica con otros vía WebClient.


Tecnologías utilizadas


Java 21 + Spring Boot 3.3 / 3.5
Spring Cloud Netflix Eureka — Registro y descubrimiento de servicios
Spring Cloud Gateway — API Gateway con filtro JWT global
Spring Data JPA + Hibernate — Persistencia con MySQL 8
Spring Security + JWT (JJWT 0.11.5) — Autenticación y autorización
WebClient — Comunicación reactiva entre microservicios
SpringDoc OpenAPI (Swagger UI) — Documentación de APIs
JUnit 5 + Mockito — Pruebas unitarias con estructura Given-When-Then
Docker + Docker Compose — Contenedorización y despliegue
SLF4J + Logback — Logs estructurados en todas las capas



Rutas principales del Gateway

Todas las peticiones entran por http://localhost:8080:

RutaMicroservicio destino/api/auth/**ms-auth (8091)/api/categorias/**, /api/materiales/**ms-catalogo (8081)/api/clientes/**ms-cliente (8082)/api/proveedores-material/**, /api/precios-proveedor/**ms-proveedor-material (8083)/api/proveedores-servicio/**ms-proveedor-servicio (8084)/api/resenas/**ms-resena (8085)/api/proyectos/**ms-proyecto (8086)/api/calculos-material/**ms-calculo-material (8087)/api/cotizaciones/**ms-cotizacion (8088)/api/comparacion-precios/**ms-comparacion-precios (8089)/api/ordenes-trabajo/**ms-orden-trabajo (8090)


Documentación Swagger (local)

MicroservicioURLms-authhttp://localhost:8091/swagger-ui.htmlms-catalogohttp://localhost:8081/swagger-ui.htmlms-clientehttp://localhost:8082/swagger-ui.htmlms-proveedor-materialhttp://localhost:8083/swagger-ui.htmlms-proveedor-serviciohttp://localhost:8084/swagger-ui.htmlms-resenahttp://localhost:8085/swagger-ui.htmlms-proyectohttp://localhost:8086/swagger-ui.htmlms-calculo-materialhttp://localhost:8087/swagger-ui.htmlms-cotizacionhttp://localhost:8088/swagger-ui.htmlms-comparacion-precioshttp://localhost:8089/swagger-ui.htmlms-orden-trabajohttp://localhost:8090/swagger-ui.html


Autenticación JWT

Los endpoints /api/auth/login y /api/auth/register son públicos. Todo lo demás requiere token JWT.

Authorization: Bearer {token}

Para obtener un token, hacer login primero:

bashPOST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

En Swagger: presionar el botón Authorize (candado), pegar el token y luego usar "Try it out" en cualquier endpoint.

Roles disponibles

RolDescripciónADMINAcceso total al sistemaUSERCliente del sistemaPROVEEDORProveedor de materiales o servicios


Reglas de negocio principales

Transiciones de estado

EntidadFlujo permitidoCotizaciónBORRADOR → ENVIADA → APROBADA | RECHAZADAOrden de TrabajoPENDIENTE → EN_CURSO → COMPLETADA | CANCELADAProyectoCOTIZANDO → EN_EJECUCION → TERMINADO

Validaciones clave


Un cliente solo puede dejar una reseña por proveedor de servicio
No se pueden crear dos cálculos para el mismo material en el mismo proyecto
Una cotización aprobada genera una sola orden de trabajo
No se pueden eliminar entidades en estados finales (APROBADA, COMPLETADA, TERMINADO, etc.)
RUT y email deben ser únicos en clientes y proveedores



Instrucciones de ejecución local (sin Docker)

Requisitos previos


Java 21+
Maven 3.8+
MySQL 8.0 corriendo en localhost:3306


1. Crear las bases de datos

bashmysql -u root -p < init-db.sql

2. Levantar los microservicios en orden

bash# 1. Eureka Server (primero siempre)
cd ms-eureka-server && ./mvnw spring-boot:run

# 2. Auth
cd ms-auth && ./mvnw spring-boot:run

# 3. Catálogo
cd ms-catalogo && ./mvnw spring-boot:run

# 4. Cliente
cd ms-cliente/ms-cliente && ./mvnw spring-boot:run

# 5. Proveedor Material
cd ms-proveedor-material/ms-proveedor-material && ./mvnw spring-boot:run

# 6. Proveedor Servicio
cd ms-proveedor-servicio/ms-proveedor-servicio && ./mvnw spring-boot:run

# 7. Reseña
cd ms-resena && ./mvnw spring-boot:run

# 8. Proyecto
cd ms-proyecto/ms-proyecto && ./mvnw spring-boot:run

# 9. Cálculo Material
cd ms-calculo-material && ./mvnw spring-boot:run

# 10. Cotización
cd ms-cotizacion && ./mvnw spring-boot:run

# 11. Comparación Precios
cd ms-comparacion-precios && ./mvnw spring-boot:run

# 12. Orden de Trabajo
cd ms-orden-trabajo && ./mvnw spring-boot:run

# 13. Gateway (último siempre)
cd ms-gateway && ./mvnw spring-boot:run

Panel de Eureka disponible en: http://localhost:8761


Instrucciones de ejecución con Docker

bashdocker-compose up --build

Esto levanta MySQL + los 13 microservicios automáticamente en la red constructapp-network.

Panel de Eureka: http://localhost:8761

Gateway: http://localhost:8080


Pruebas unitarias

Ejecutar en cada microservicio:

bash./mvnw test

Las pruebas cubren la lógica de negocio de cada servicio usando JUnit 5 + Mockito, con estructura Given-When-Then y mocks de repositorios y dependencias externas. Cobertura mínima del 80% sobre funciones y reglas de negocio clave.


Perfiles de configuración

PerfilUsodev (por defecto)Desarrollo local, MySQL en localhost, show-sql activadoprodDespliegue con Docker, variables de entorno, show-sql desactivado


ConstructApp — Proyecto académico DUOC UC — DSY1103 Desarrollo FullStack 1
