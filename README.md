# ConstructApp — Sistema de Gestión de Construcción

Sistema de microservicios desarrollado con **Spring Boot 3 / Java 21** para la gestión integral de proyectos de construcción.

---

## Integrantes del equipo

- Robinson Salvo
- Cristobal Cargnino

---

## Descripción del contexto / dominio del proyecto

ConstructApp es una plataforma de gestión para empresas del rubro de la construcción. Permite administrar clientes, proveedores de materiales y servicios, proyectos, cotizaciones, órdenes de trabajo, cálculo de materiales y comparación de precios entre proveedores. Cada área del negocio está separada en un microservicio independiente, comunicados entre sí mediante REST y centralizados a través de un API Gateway con autenticación JWT.

---

## Microservicios implementados

| # | Microservicio | Puerto | Descripción |
|---|---|---|---|
| 1 | **ms-eureka-server** | 8761 | Registro y descubrimiento de servicios (Eureka) |
| 2 | **ms-gateway** | 8080 | API Gateway — enrutamiento centralizado y validación JWT |
| 3 | **ms-auth** | 8091 | Autenticación y autorización con JWT |
| 4 | **ms-catalogo** | 8081 | Catálogo de categorías y materiales |
| 5 | **ms-cliente** | 8082 | Gestión de clientes |
| 6 | **ms-proveedor-material** | 8083 | Proveedores de materiales y precios |
| 7 | **ms-proveedor-servicio** | 8084 | Proveedores de servicios |
| 8 | **ms-resena** | 8085 | Reseñas de proveedores de servicio |
| 9 | **ms-proyecto** | 8086 | Proyectos de construcción |
| 10 | **ms-calculo-material** | 8087 | Cálculo de materiales por proyecto |
| 11 | **ms-cotizacion** | 8088 | Cotizaciones de proyectos |
| 12 | **ms-comparacion-precios** | 8089 | Comparación de precios entre proveedores |
| 13 | **ms-orden-trabajo** | 8090 | Órdenes de trabajo |

---

## Arquitectura

```
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
```

Cada microservicio sigue el patrón **CSR (Controller → Service → Repository/Model)** y se comunica con otros vía **WebClient**.

---

## Tecnologías utilizadas

- **Java 21** + **Spring Boot 3.3 / 3.5**
- **Spring Cloud Netflix Eureka** — Registro y descubrimiento de servicios
- **Spring Cloud Gateway** — API Gateway con filtro JWT global
- **Spring Data JPA + Hibernate** — Persistencia con MySQL 8
- **Spring Security + JWT (JJWT 0.11.5)** — Autenticación y autorización
- **WebClient** — Comunicación reactiva entre microservicios
- **SpringDoc OpenAPI (Swagger UI)** — Documentación de APIs
- **JUnit 5 + Mockito** — Pruebas unitarias con estructura Given-When-Then
- **Docker + Docker Compose** — Contenedorización y despliegue
- **SLF4J + Logback** — Logs estructurados en todas las capas

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

## Documentación Swagger (local)

| Microservicio | URL |
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

## Autenticación JWT

Los endpoints `/api/auth/login` y `/api/auth/register` son públicos. **Todo lo demás requiere token JWT.**

```
Authorization: Bearer {token}
```

Para obtener un token, hacer login primero:

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

En Swagger: presionar el botón **Authorize** (candado), pegar el token y luego usar "Try it out" en cualquier endpoint.

### Dónde se valida el token

La seguridad JWT existe en **dos capas independientes**, ambas usando el mismo secreto compartido (`jwt.secret` / `JWT_SECRET`):

1. **`ms-gateway`** (puerto 8080): filtro global (`JwtAuthFilter`) que exige token en cualquier ruta `/api/**`, excepto `/api/auth/login` y `/api/auth/register`. Es el punto de entrada único en producción.
2. **Cada microservicio de negocio** (`ms-catalogo`, `ms-cliente`, `ms-proveedor-material`, `ms-proveedor-servicio`, `ms-resena`, `ms-proyecto`, `ms-calculo-material`, `ms-cotizacion`, `ms-comparacion-precios`, `ms-orden-trabajo`) tiene su **propia** `SecurityConfig` + `JwtAuthFilter` (solo valida, no emite tokens). Esto permite que el botón **Authorize** funcione también al probar el Swagger de cada microservicio por su puerto individual (ej. `localhost:8081/swagger-ui.html`), sin pasar por el Gateway.

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
- Un cliente solo puede dejar **una reseña por proveedor de servicio**
- No se pueden crear dos cálculos para el **mismo material en el mismo proyecto**
- Una cotización aprobada genera **una sola orden de trabajo**
- No se pueden eliminar entidades en estados finales (APROBADA, COMPLETADA, TERMINADO, etc.)
- RUT y email deben ser **únicos** en clientes y proveedores

---

## Manejo de errores y códigos HTTP

Cada microservicio expone un `GlobalExceptionHandler` (`@RestControllerAdvice`) con manejo específico por tipo de excepción:

| Excepción | Código HTTP | Cuándo se lanza |
|---|---|---|
| `ResourceNotFoundException` | `404 NOT_FOUND` | La entidad buscada por ID no existe, o un recurso referenciado en otro microservicio no existe (ej. cliente inexistente al crear una cotización) |
| `InvalidCredentialsException` (solo `ms-auth`) | `401 UNAUTHORIZED` | Username inexistente o contraseña incorrecta en el login |
| `RuntimeException` (genérica) | `400 BAD_REQUEST` | Violación de una regla de negocio: nombre/email/RUT duplicado, transición de estado inválida, eliminación de una entidad en estado final, etc. |
| `MethodArgumentNotValidException` | `400 BAD_REQUEST` | Falla una validación `@Valid` de Bean Validation en el DTO |
| `Exception` (genérica) | `500 INTERNAL_SERVER_ERROR` | Cualquier error no controlado |

Todas las respuestas de error se registran con SLF4J y devuelven un cuerpo JSON consistente `{ "error": "mensaje" }` (o un mapa de errores por campo en el caso de validación).

`ms-gateway` tiene su propio `GatewayExceptionHandler` (`ErrorWebExceptionHandler`), que además traduce fallas de conexión hacia los microservicios en `503 SERVICE_UNAVAILABLE`.

---

## Instrucciones de ejecución local (sin Docker)

### Requisitos previos
- Java 21+
- Maven 3.8+
- MySQL 8.0 corriendo en localhost (puerto 3306 por defecto)

> **¿Usas Laragon, XAMPP u otra herramienta con MySQL en un puerto distinto a 3306?**
> El puerto de conexión es configurable mediante la variable de entorno `DB_PORT`, sin tocar código. Ejemplo para Laragon (puerto 3307):
> ```bash
> # Windows PowerShell
> $env:DB_PORT="3307"; ./mvnw spring-boot:run
>
> # Linux / macOS
> DB_PORT=3307 ./mvnw spring-boot:run
> ```
> Si no se define `DB_PORT`, se usa 3306 por defecto. Esto no afecta la ejecución con Docker, que usa su propio contenedor MySQL interno en el puerto 3306 de la red de Docker.

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

## Instrucciones de ejecución con Docker

```bash
docker-compose up --build
```

Esto levanta MySQL + los 13 microservicios automáticamente en la red `constructapp-network`.

Panel de Eureka: http://localhost:8761  
Gateway: http://localhost:8080

---

## Pruebas unitarias

Ejecutar en cada microservicio:

```bash
./mvnw test
```

Las pruebas cubren la lógica de negocio de cada servicio usando **JUnit 5 + Mockito**, con estructura **Given-When-Then** y mocks de repositorios y dependencias externas. Cobertura mínima del 80% sobre funciones y reglas de negocio clave.

`ms-gateway` no tiene lógica de negocio propia (enrutamiento declarativo + filtro JWT), pero sí incluye pruebas unitarias sobre su única pieza de lógica custom: `GatewayExceptionHandlerTest`, que valida la traducción de errores (404, 503, 500) a la respuesta JSON del gateway. `ms-eureka-server` no requiere pruebas de negocio adicionales: es un componente estándar de Spring Cloud Netflix sin código propio más allá de la configuración.

### Reporte de cobertura (JaCoCo)

Para obtener el % de cobertura real y demostrable de cada microservicio:

```bash
./mvnw test jacoco:report
```

El reporte HTML queda en `target/site/jacoco/index.html` — ábrelo en el navegador para ver el porcentaje exacto por clase/paquete. Repite esto en cada uno de los 12 microservicios con lógica propia antes de la defensa.

---

## Migraciones de base de datos (Flyway)

Los 10 microservicios con persistencia (`ms-auth`, `ms-catalogo`, `ms-cliente`, `ms-proveedor-material`, `ms-proveedor-servicio`, `ms-resena`, `ms-proyecto`, `ms-calculo-material`, `ms-cotizacion`, `ms-orden-trabajo`) usan **Flyway** para crear el esquema inicial, en vez de depender de `ddl-auto` de Hibernate:

- El script `V1__init.sql` de cada microservicio está en `src/main/resources/db/migration/` y contiene el `CREATE TABLE` exacto de sus entidades (incluyendo FKs, `UNIQUE` y `NOT NULL`).
- `ddl-auto` cambió de `update` a `validate`: Hibernate ya no crea/modifica tablas, solo valida que el esquema coincida con las entidades JPA. Si detecta un desface, la app no arranca (esto es intencional: fuerza a que cualquier cambio de modelo se refleje también en una nueva migración `V2__...sql`, no en un auto-ajuste silencioso).
- Flyway corre automáticamente al iniciar cada microservicio (no requiere comando manual).

**⚠️ Importante si ya tenías las bases de datos creadas con la versión anterior (con `ddl-auto: update`):** Flyway va a fallar con `Table 'xxx' already exists` porque intentará crear tablas que ya existen. Antes de correr esta versión, borra y recrea las 10 bases de datos correspondientes:

```sql
DROP DATABASE IF EXISTS db_ms_auth; CREATE DATABASE db_ms_auth;
DROP DATABASE IF EXISTS db_ms_catalogo; CREATE DATABASE db_ms_catalogo;
DROP DATABASE IF EXISTS db_ms_cliente; CREATE DATABASE db_ms_cliente;
DROP DATABASE IF EXISTS db_ms_proveedor_material; CREATE DATABASE db_ms_proveedor_material;
DROP DATABASE IF EXISTS db_ms_proveedor_servicio; CREATE DATABASE db_ms_proveedor_servicio;
DROP DATABASE IF EXISTS db_ms_resena; CREATE DATABASE db_ms_resena;
DROP DATABASE IF EXISTS db_ms_proyecto; CREATE DATABASE db_ms_proyecto;
DROP DATABASE IF EXISTS db_ms_calculo_material; CREATE DATABASE db_ms_calculo_material;
DROP DATABASE IF EXISTS db_ms_cotizacion; CREATE DATABASE db_ms_cotizacion;
DROP DATABASE IF EXISTS db_ms_orden_trabajo; CREATE DATABASE db_ms_orden_trabajo;
```

(Esto borra los datos de prueba que tengas cargados — no afecta `ms-comparacion-precios`, que no tiene base de datos propia).

---

## Perfiles de configuración

| Perfil | Uso |
|---|---|
| `dev` (por defecto) | Desarrollo local, MySQL en localhost, show-sql activado |
| `prod` | Despliegue con Docker, variables de entorno, show-sql desactivado |

---

**ConstructApp** — Proyecto académico DUOC UC — DSY1103 Desarrollo FullStack 1
