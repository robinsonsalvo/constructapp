# ConstructApp — Sistema de Gestión de Construcción

Sistema de microservicios desarrollado con **Spring Boot 3 / Java 21** para la gestión integral de proyectos de construcción: clientes, proveedores de materiales y servicios, proyectos, cotizaciones, cálculo de materiales, comparación de precios y órdenes de trabajo.

Proyecto académico — **DSY1103 Desarrollo FullStack 1** · Duoc UC · Evaluación Final Transversal (EFT).

---

## Tabla de contenidos

- [Integrantes](#integrantes)
- [Descripción del dominio](#descripción-del-dominio)
- [Arquitectura](#arquitectura)
- [Microservicios implementados](#microservicios-implementados)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Rutas principales del Gateway](#rutas-principales-del-gateway)
- [Autenticación y autorización (JWT)](#autenticación-y-autorización-jwt)
- [Reglas de negocio principales](#reglas-de-negocio-principales)
- [Manejo de errores y códigos HTTP](#manejo-de-errores-y-códigos-http)
- [Documentación Swagger / OpenAPI](#documentación-swagger--openapi)
- [Migraciones de base de datos (Flyway)](#migraciones-de-base-de-datos-flyway)
- [Perfiles de configuración](#perfiles-de-configuración)
- [Instrucciones de ejecución con Docker](#instrucciones-de-ejecución-con-docker)
- [Instrucciones de ejecución local (sin Docker)](#instrucciones-de-ejecución-local-sin-docker)
- [Pruebas unitarias y cobertura](#pruebas-unitarias-y-cobertura)
- [Changelog — correcciones recientes](#changelog--correcciones-recientes)
---

## Integrantes

- Robinson Salvo
- Cristobal Cargnino

---

## Descripción del dominio

ConstructApp es una plataforma de gestión para empresas del rubro de la construcción. Permite administrar clientes, proveedores de materiales y servicios, proyectos, cotizaciones, órdenes de trabajo, cálculo de materiales y comparación de precios entre proveedores. Cada área del negocio está separada en un microservicio independiente, comunicados entre sí mediante REST y centralizados a través de un **API Gateway con autenticación JWT**.

---

## Arquitectura

```
                              ┌────────────────────┐
                              │  ms-eureka-server   │
                              │    (Puerto 8761)    │
                              └──────────▲───────────┘
                                         │ registro y descubrimiento
                                         │
   Cliente / Postman ──────►  ms-gateway (Puerto 8080)
                                         │ Filtro JWT global
              ┌──────────────┬──────────┼───────────────┬──────────────┐
              ▼              ▼          ▼                ▼              ▼
          ms-auth        ms-catalogo  ms-cliente   ms-proveedor-*   ms-proyecto
          (8091)          (8081)       (8082)      (8083 / 8084)      (8086)
              │                                                          │
              ▼                                                          ▼
      MySQL (por servicio)                                    ms-cotizacion (8088)
                                                                          │
                                                     ┌────────────────────┼─────────────────────┐
                                                     ▼                    ▼                      ▼
                                          ms-calculo-material   ms-comparacion-precios   ms-orden-trabajo
                                               (8087)                  (8089)                 (8090)
```

Cada microservicio sigue el patrón **CSR (Controller → Service → Repository/Model)** y se comunica con otros vía **WebClient**.

---

## Microservicios implementados

| # | Microservicio | Puerto | Base de datos | Descripción |
|---|---|---|---|---|
| 1 | `ms-eureka-server` | 8761 | — | Registro y descubrimiento de servicios (Netflix Eureka) |
| 2 | `ms-gateway` | 8080 | — | API Gateway — enrutamiento centralizado y validación JWT global |
| 3 | `ms-auth` | 8091 | `db_ms_auth` | Autenticación y autorización, emisión de tokens JWT |
| 4 | `ms-catalogo` | 8081 | `db_ms_catalogo` | Catálogo de categorías y materiales |
| 5 | `ms-cliente` | 8082 | `db_ms_cliente` | Gestión de clientes |
| 6 | `ms-proveedor-material` | 8083 | `db_ms_proveedor_material` | Proveedores de materiales y precios |
| 7 | `ms-proveedor-servicio` | 8084 | `db_ms_proveedor_servicio` | Proveedores de servicios |
| 8 | `ms-resena` | 8085 | `db_ms_resena` | Reseñas de proveedores de servicio |
| 9 | `ms-proyecto` | 8086 | `db_ms_proyecto` | Proyectos de construcción |
| 10 | `ms-calculo-material` | 8087 | `db_ms_calculo_material` | Cálculo de materiales por proyecto |
| 11 | `ms-cotizacion` | 8088 | `db_ms_cotizacion` | Cotizaciones de proyectos |
| 12 | `ms-comparacion-precios` | 8089 | *(sin BD propia)* | Comparación de precios entre proveedores |
| 13 | `ms-orden-trabajo` | 8090 | `db_ms_orden_trabajo` | Órdenes de trabajo |

**Total: 13 microservicios** (mínimo exigido por la pauta EFT: 10).

---

## Tecnologías utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje / Runtime | Java 21 |
| Framework | Spring Boot 3.3 / 3.5 |
| Service discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway + filtro JWT global |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 |
| Migraciones | Flyway |
| Seguridad | Spring Security + JWT (JJWT 0.11.5), doble capa (Gateway + cada microservicio) |
| Comunicación entre servicios | WebClient (reactivo) |
| Documentación de API | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito, estructura Given–When–Then |
| Cobertura de código | JaCoCo |
| Contenedores | Docker + Docker Compose |
| Logs | SLF4J + Logback |

---

## Rutas principales del Gateway

Todas las peticiones entran por `http://localhost:8080`:

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

## Autenticación y autorización (JWT)

Los endpoints `/api/auth/login` y `/api/auth/register` son **públicos**. Todo lo demás requiere token JWT:

```
Authorization: Bearer {token}
```

**Obtener un token:**

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

En Swagger UI: presionar **Authorize** (candado), pegar el token y usar "Try it out" en cualquier endpoint.

### Dónde se valida el token

La seguridad JWT existe en **dos capas independientes**, ambas con el mismo secreto compartido (`jwt.secret` / `JWT_SECRET`):

1. **`ms-gateway`** (puerto 8080): filtro global (`JwtAuthFilter`) que exige token en cualquier ruta `/api/**`, excepto login/register. Punto de entrada único en producción.
2. **Cada microservicio de negocio**: tiene su propio `SecurityConfig` + `JwtAuthFilter` / `JwtAuthenticationFilter` + `JwtService` (solo valida, no emite tokens). Esto permite usar el botón **Authorize** en el Swagger de cada microservicio individualmente (ej. `localhost:8081/swagger-ui.html`), sin pasar por el Gateway.

`ms-auth` es el único que además **emite** tokens (login/register) y valida credenciales contra la base de datos.

### Roles disponibles

| Rol | Descripción |
|---|---|
| `ADMIN` | Acceso total al sistema |
| `USER` | Cliente del sistema |
| `PROVEEDOR` | Proveedor de materiales o servicios |

---

## Reglas de negocio principales

### Transiciones de estado

| Entidad | Flujo permitido |
|---|---|
| Cotización | `BORRADOR` → `ENVIADA` → `APROBADA` \| `RECHAZADA` |
| Orden de Trabajo | `PENDIENTE` → `EN_CURSO` → `COMPLETADA` \| `CANCELADA` |
| Proyecto | `COTIZANDO` → `EN_EJECUCION` → `TERMINADO` |

### Validaciones clave

- Un cliente solo puede dejar **una reseña por proveedor de servicio**.
- No se pueden crear dos cálculos para el **mismo material en el mismo proyecto**.
- Una cotización aprobada genera **una sola orden de trabajo**.
- No se pueden eliminar entidades en estados finales (`APROBADA`, `COMPLETADA`, `TERMINADO`, etc.).
- RUT y email deben ser **únicos** en clientes y proveedores.

---

## Manejo de errores y códigos HTTP

Cada microservicio expone un `GlobalExceptionHandler` (`@RestControllerAdvice`) con manejo específico por tipo de excepción:

| Excepción | Código HTTP | Cuándo se lanza |
|---|---|---|
| `ResourceNotFoundException` | `404 NOT_FOUND` | La entidad buscada por ID no existe, o un recurso referenciado en otro microservicio no existe |
| `InvalidCredentialsException` (solo `ms-auth`) | `401 UNAUTHORIZED` | Username inexistente o contraseña incorrecta en el login |
| `RuntimeException` (genérica) | `400 BAD_REQUEST` | Violación de una regla de negocio: duplicados, transición de estado inválida, eliminación en estado final, etc. |
| `MethodArgumentNotValidException` | `400 BAD_REQUEST` | Falla una validación `@Valid` de Bean Validation en el DTO |
| `Exception` (genérica) | `500 INTERNAL_SERVER_ERROR` | Cualquier error no controlado |

Todas las respuestas de error se registran con SLF4J y devuelven un cuerpo JSON consistente `{ "error": "mensaje" }` (o un mapa de errores por campo en validaciones).

`ms-gateway` tiene su propio `GatewayExceptionHandler` (`ErrorWebExceptionHandler`), que traduce fallas de conexión hacia los microservicios en `503 SERVICE_UNAVAILABLE`, cubierto por `GatewayExceptionHandlerTest`.

---

## Documentación Swagger / OpenAPI

| Microservicio | URL local |
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

---

## Migraciones de base de datos (Flyway)

Los 10 microservicios con persistencia usan **Flyway** para crear el esquema inicial, en vez de depender de `ddl-auto` de Hibernate:

- El script `V1__init.sql` de cada microservicio está en `src/main/resources/db/migration/` y contiene el `CREATE TABLE` exacto de sus entidades (incluyendo FKs, `UNIQUE` y `NOT NULL`).
- `ddl-auto` está en `validate`: Hibernate no crea ni modifica tablas, solo valida que el esquema coincida con las entidades JPA. Si detecta un desface, la app no arranca — esto es intencional, fuerza a reflejar cualquier cambio de modelo en una nueva migración `V2__...sql`.
- Flyway corre automáticamente al iniciar cada microservicio.

> **⚠️ Importante:** si tenías las bases de datos creadas con una versión anterior (`ddl-auto: update`), Flyway va a fallar con `Found non-empty schema(s) ... but no schema history table`, porque encuentra tablas ya creadas sin su tabla de control `flyway_schema_history`. Antes de levantar esta versión, borra y recrea las 10 bases de datos correspondientes (ver script `reset-databases.sql` si está disponible, o ejecuta `DROP DATABASE` + `CREATE DATABASE` para cada una). Si usas Docker, la alternativa más rápida es borrar el volumen de MySQL y reconstruir desde cero.

---

## Perfiles de configuración

| Perfil | Uso |
|---|---|
| `dev` (por defecto) | Desarrollo local, MySQL en `localhost`, `show-sql` activado |
| `prod` | Despliegue con Docker, variables de entorno, `show-sql` desactivado |

---

## Instrucciones de ejecución con Docker

```bash
docker-compose up --build
```

Esto levanta MySQL + los 13 microservicios automáticamente en la red `constructapp-network`.

- Panel de Eureka: http://localhost:8761
- Gateway: http://localhost:8080

Si necesitas reiniciar todo desde cero (por ejemplo tras un error de Flyway):

```bash
docker-compose down
docker volume rm constructapp-main_mysql_data   # ajusta el nombre según 'docker volume ls'
docker-compose up --build
```

---

## Instrucciones de ejecución local (sin Docker)

### Requisitos previos

- Java 21+
- Maven 3.8+
- MySQL 8.0 corriendo en `localhost` (puerto 3306 por defecto)

> **¿Usas Laragon, XAMPP u otra herramienta con MySQL en un puerto distinto a 3306?**
> El puerto es configurable con la variable `DB_PORT`, sin tocar código:
> ```powershell
> # Windows PowerShell
> $env:DB_PORT="3307"; ./mvnw spring-boot:run
> ```
> Si no se define `DB_PORT`, se usa 3306. Esto no afecta la ejecución con Docker.

### 1. Crear las bases de datos

```bash
mysql -u root -p < init-db.sql
```

### 2. Levantar los microservicios en orden

```bash
# 1. Eureka Server (primero siempre)
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
```

Panel de Eureka disponible en: http://localhost:8761

---

## Pruebas unitarias y cobertura

Ejecutar en cada microservicio:

```bash
./mvnw test
```

Las pruebas cubren la lógica de negocio de cada servicio usando **JUnit 5 + Mockito**, con estructura **Given-When-Then** y mocks de repositorios y dependencias externas (incluido `WebClient` para comunicación entre microservicios). Cobertura mínima objetivo: **80%** sobre funciones y reglas de negocio clave.

`ms-gateway` incluye `GatewayExceptionHandlerTest`, que valida la traducción de errores (404, 503, 500) a la respuesta JSON del gateway. `ms-eureka-server` no requiere pruebas de negocio adicionales al ser un componente estándar de Spring Cloud Netflix.

### Reporte de cobertura (JaCoCo)

```bash
./mvnw test jacoco:report
```

El reporte HTML queda en `target/site/jacoco/index.html`. Repite esto en cada uno de los 12 microservicios con lógica propia **antes de la defensa**, para tener el número real a mano si te lo piden.

---

## Changelog — correcciones recientes

- **Fix crítico:** resuelto conflicto de merge sin terminar en `docker-compose.yml` (contenía marcadores `<<<<<<< HEAD` sin resolver que invalidaban el archivo).
- Restaurado el microservicio `ms-eureka-server`, que se había perdido dentro de ese conflicto.
- Agregada seguridad JWT propia en cada microservicio de negocio (`SecurityConfig`, `JwtAuthFilter`, `JwtAuthenticationFilter`, `JwtService`), además del filtro global en `ms-gateway`.
- Agregado manejo de excepciones específicas: `ResourceNotFoundException` en todos los microservicios e `InvalidCredentialsException` en `ms-auth`.
- Migrada la configuración de `application.properties` a `application.yml` con perfiles `dev`/`prod` en todos los microservicios.
- Agregadas migraciones Flyway (`db/migration/V1__init.sql`) en los 10 microservicios con persistencia, cambiando `ddl-auto` de `update` a `validate`.
- Corregida la inicialización de bases de datos para evitar el error `Found non-empty schema but no schema history table`.
- Agregada prueba unitaria `GatewayExceptionHandlerTest` en `ms-gateway`.
- Corregido `.gitignore` para excluir `target/`, `.idea/`, `.vscode/` y otros archivos generados.

---

**ConstructApp** — Proyecto académico DUOC UC — DSY1103 Desarrollo FullStack 1
