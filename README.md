# ConstructApp — Sistema de Gestión de Proyectos de Construcción

Sistema de microservicios desarrollado con **Spring Boot 3 / Java 21** para la gestión integral de proyectos de construcción.

---

## Integrantes del equipo

| Nombre | GitHub |
|---|---|
| Robinson Salvo | [@robinsonsalvo](https://github.com/robinsonsalvo) |
| Cristóbal Cargnino | [@cristobalcargnino](https://github.com/cristobalcargnino) |

---

## Descripción del proyecto

ConstructApp es un sistema distribuido basado en microservicios que permite gestionar el ciclo completo de un proyecto de construcción: desde el registro de clientes, materiales y proveedores, hasta la generación de cotizaciones, asignación de órdenes de trabajo y seguimiento de avance.

---

## Microservicios implementados (13)

| # | Microservicio | Puerto | Base de datos | Descripción |
|---|---|---|---|---|
| 1 | ms-gateway | 8080 | — | API Gateway con validación JWT centralizada |
| 2 | ms-auth | 8091 | db_ms_auth | Autenticación y autorización con JWT |
| 3 | ms-catalogo | 8081 | db_ms_catalogo | Categorías y materiales de construcción |
| 4 | ms-cliente | 8082 | db_ms_cliente | Gestión de clientes (PARTICULAR/EMPRESA) |
| 5 | ms-proveedor-material | 8083 | db_ms_proveedor_material | Proveedores de materiales y precios |
| 6 | ms-proveedor-servicio | 8084 | db_ms_proveedor_servicio | Proveedores de mano de obra |
| 7 | ms-resena | 8085 | db_ms_resena | Reseñas de proveedores por clientes |
| 8 | ms-proyecto | 8086 | db_ms_proyecto | Proyectos de construcción |
| 9 | ms-calculo-material | 8087 | db_ms_calculo_material | Cálculo de materiales y costos estimados |
| 10 | ms-cotizacion | 8088 | db_ms_cotizacion | Cotizaciones con detalles de materiales y servicios |
| 11 | ms-comparacion-precios | 8089 | — | Comparación de precios entre proveedores |
| 12 | ms-orden-trabajo | 8090 | db_ms_orden_trabajo | Órdenes de trabajo y seguimiento |
| 13 | ms-eureka-server | 8761 | — | Registro y descubrimiento de servicios |

---

## Tecnologías

- **Java 21** + **Spring Boot 3.5**
- **Spring Cloud Gateway** — API Gateway con filtro JWT
- **Spring Cloud Netflix Eureka** — Service Discovery
- **Spring Data JPA** + **MySQL 8** — Persistencia
- **Flyway** — Migraciones de base de datos
- **Spring Security** + **JWT (JJWT)** — Autenticación
- **WebClient** — Comunicación entre microservicios
- **SpringDoc OpenAPI (Swagger)** — Documentación de APIs
- **JUnit 5 + Mockito + JaCoCo** — Pruebas unitarias y cobertura
- **Docker + Docker Compose** — Contenedorización y despliegue

---

## Rutas principales del Gateway (puerto 8080)

| Ruta | Microservicio destino |
|---|---|
| `/api/auth/**` | ms-auth (8091) |
| `/api/categorias/**`, `/api/materiales/**` | ms-catalogo (8081) |
| `/api/clientes/**` | ms-cliente (8082) |
| `/api/proveedores-material/**`, `/api/precios-proveedor/**` | ms-proveedor-material (8083) |
| `/api/proveedores-servicio/**` | ms-proveedor-servicio (8084) |
| `/api/resenas/**` | ms-resena (8085) |
| `/api/proyectos/**` | ms-proyecto (8086) |
| `/api/calculos-material/**` | ms-calculo-material (8087) |
| `/api/cotizaciones/**` | ms-cotizacion (8088) |
| `/api/comparacion-precios/**` | ms-comparacion-precios (8089) |
| `/api/ordenes-trabajo/**` | ms-orden-trabajo (8090) |

---

## Documentación Swagger (por microservicio)

| Microservicio | Swagger UI |
|---|---|
| ms-auth | http://localhost:8091/swagger-ui.html |
| ms-catalogo | http://localhost:8081/swagger-ui.html |
| ms-cliente | http://localhost:8082/swagger-ui.html |
| ms-proveedor-material | http://localhost:8083/swagger-ui.html |
| ms-proveedor-servicio | http://localhost:8084/swagger-ui.html |
| ms-resena | http://localhost:8085/swagger-ui.html |
| ms-proyecto | http://localhost:8086/swagger-ui.html |
| ms-calculo-material | http://localhost:8087/swagger-ui.html |
| ms-cotizacion | http://localhost:8088/swagger-ui.html |
| ms-comparacion-precios | http://localhost:8089/swagger-ui.html |
| ms-orden-trabajo | http://localhost:8090/swagger-ui.html |
| ms-eureka-server | http://localhost:8761 |

---

## Instrucciones de ejecución local

### Prerrequisitos
- Java 21+
- Maven 3.8+
- MySQL 8.0 corriendo localmente
- Docker Desktop (para despliegue con contenedores)

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/robinsonsalvo/constructapp.git
cd constructapp
```

### Paso 2 — Crear las bases de datos

```bash
mysql -u root -p < init-db.sql
```

### Paso 3 — Levantar todos los microservicios

**Opción A — Script automático (Windows):**
```bash
iniciar-todos.bat
```

**Opción B — Uno por uno (en terminales separadas):**
```bash
# Orden recomendado:
cd ms-eureka-server && mvnw spring-boot:run
cd ms-auth          && mvnw spring-boot:run
cd ms-catalogo      && mvnw spring-boot:run
cd ms-cliente/ms-cliente && mvnw spring-boot:run
cd ms-proveedor-material/ms-proveedor-material && mvnw spring-boot:run
cd ms-proveedor-servicio/ms-proveedor-servicio && mvnw spring-boot:run
cd ms-resena        && mvnw spring-boot:run
cd ms-proyecto/ms-proyecto && mvnw spring-boot:run
cd ms-calculo-material && mvnw spring-boot:run
cd ms-cotizacion    && mvnw spring-boot:run
cd ms-comparacion-precios && mvnw spring-boot:run
cd ms-orden-trabajo && mvnw spring-boot:run
cd ms-gateway       && mvnw spring-boot:run  # siempre último
```

### Paso 4 — Verificar que todo esté corriendo

Abre en el navegador: http://localhost:8761 (Eureka Dashboard)

---

## Ejecución con Docker

```bash
docker-compose up --build
```

Esto levanta MySQL + los 13 microservicios automáticamente en red interna.

---

## Ejecución de pruebas unitarias

En cada microservicio:
```bash
mvnw test
```

Para generar el reporte de cobertura JaCoCo:
```bash
mvnw test jacoco:report
```
El reporte queda en `target/site/jacoco/index.html`.

---

## Reglas de negocio principales

| Entidad | Flujo de estados |
|---|---|
| Cotización | `BORRADOR` → `ENVIADA` → `APROBADA` \| `RECHAZADA` |
| Orden de Trabajo | `PENDIENTE` → `EN_CURSO` → `COMPLETADA` \| `CANCELADA` |
| Proyecto | `COTIZANDO` → `EN_EJECUCION` → `TERMINADO` |

---

## Gestión del proyecto

- **Repositorio GitHub:** https://github.com/robinsonsalvo/constructapp
- **Tablero Trello:** [ConstructApp - DSY1103](https://trello.com/b/UCnal1Mx/constructapp-dsy1103)
