# ConstructApp — Sistema de Gestión de Construcción

Sistema de microservicios desarrollado con **Spring Boot 3 / Java 21** para la gestión integral de proyectos de construcción.

## Arquitectura

El sistema está compuesto por **12 microservicios** independientes que se comunican entre sí mediante REST (WebClient), protegidos por un API Gateway con autenticación JWT.

```
constructapp-main/
├── ms-gateway              # Puerto 8080 — Enrutamiento y validación JWT
├── ms-auth                 # Puerto 8091 — Autenticación y autorización
├── ms-catalogo             # Puerto 8081 — Categorías y materiales
├── ms-cliente              # Puerto 8082 — Gestión de clientes
├── ms-proveedor-material   # Puerto 8083 — Proveedores de materiales
├── ms-proveedor-servicio   # Puerto 8084 — Proveedores de servicios
├── ms-resena               # Puerto 8085 — Reseñas de proveedores
├── ms-proyecto             # Puerto 8086 — Proyectos de construcción
├── ms-calculo-material     # Puerto 8087 — Cálculo de materiales
├── ms-cotizacion           # Puerto 8088 — Cotizaciones
├── ms-comparacion-precios  # Puerto 8089 — Comparación de precios
└── ms-orden-trabajo        # Puerto 8090 — Órdenes de trabajo
```

## Rutas del API Gateway

Todas las peticiones del cliente deben dirigirse al Gateway en `http://localhost:8080` — no directamente al puerto de cada microservicio. El Gateway enruta automáticamente según el prefijo de la URL:

| Ruta (prefijo) | Microservicio destino | Requiere JWT |
|---|---|---|
| `/api/auth/**` | ms-auth | No |
| `/api/categorias/**`, `/api/materiales/**` | ms-catalogo | Sí |
| `/api/clientes/**` | ms-cliente | Sí |
| `/api/proveedores-material/**` | ms-proveedor-material | Sí |
| `/api/proveedores-servicio/**` | ms-proveedor-servicio | Sí |
| `/api/resenas/**` | ms-resena | Sí |
| `/api/proyectos/**` | ms-proyecto | Sí |
| `/api/calculos-material/**` | ms-calculo-material | Sí |
| `/api/cotizaciones/**` | ms-cotizacion | Sí |
| `/api/comparacion-precios/**` | ms-comparacion-precios | Sí |
| `/api/ordenes-trabajo/**` | ms-orden-trabajo | Sí |

Ejemplo de uso:

```bash
GET http://localhost:8080/api/categorias
Authorization: Bearer {token}
```

## Tecnologías

- **Java 21** + **Spring Boot 3.5**
- **Spring Cloud Gateway** — API Gateway con filtro JWT
- **Spring Data JPA** + **MySQL 8** — Persistencia de datos
- **Spring Security** + **JWT (JJWT)** — Autenticación
- **WebClient** — Comunicación entre microservicios
- **SpringDoc OpenAPI (Swagger)** — Documentación de APIs
- **JUnit 5 + Mockito** — Pruebas unitarias
- **Docker + Docker Compose** — Contenedorización

## Reglas de negocio principales

### Transiciones de estado

| Entidad | Flujo |
|---|---|
| Cotización | `BORRADOR` → `ENVIADA` → `APROBADA` \| `RECHAZADA` |
| Orden de Trabajo | `PENDIENTE` → `EN_CURSO` → `COMPLETADA` \| `CANCELADA` |
| Proyecto | `COTIZANDO` → `EN_EJECUCION` → `TERMINADO` |

### Validaciones
- Un cliente solo puede dejar **una reseña por proveedor**
- No se pueden crear dos cálculos para el **mismo material en el mismo proyecto**
- Una cotización aprobada genera **una sola orden de trabajo**
- No se pueden eliminar entidades en estados finales

## Requisitos previos

- Java 21+
- Maven 3.8+
- MySQL 8.0
- Docker Desktop (para despliegue con Docker)

## Ejecución local

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/constructapp.git
cd constructapp
```

### 2. Configurar MySQL

Crear las bases de datos ejecutando el script:

```bash
mysql -u root -p < init-db.sql
```

### 3. Levantar todos los microservicios

Ejecutar el script (Windows):

```bash
iniciar-todos.bat
```

O levantar individualmente en este orden:

```bash
cd ms-auth && mvnw spring-boot:run
cd ms-catalogo && mvnw spring-boot:run
cd ms-cliente/ms-cliente && mvnw spring-boot:run
# ... resto de microservicios
cd ms-gateway && mvnw spring-boot:run
```

## Ejecución con Docker

```bash
docker-compose up --build
```

Esto levanta MySQL + los 12 microservicios automáticamente.

## Documentación Swagger

Con los microservicios corriendo, accede a la documentación en:

| Microservicio | URL Swagger |
=======
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
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
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

<<<<<<< HEAD
## Pruebas unitarias

Ejecutar en cada microservicio:

```bash
mvnw test
```

## Perfiles de configuración

Cada microservicio tiene dos perfiles en su `application.yml`:

| Perfil | Uso |
|---|---|
| `dev` (por defecto) | Desarrollo local, MySQL en localhost, show-sql activado |
| `prod` | Despliegue con Docker, variables de entorno, show-sql desactivado |

Para activar el perfil de producción:

```bash
mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Autenticación

Todos los endpoints (excepto `/api/auth/**`) requieren un token JWT en el header:
=======
---

## Autenticación JWT

Los endpoints `/api/auth/login` y `/api/auth/register` son públicos. **Todo lo demás requiere token JWT.**
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)

```
Authorization: Bearer {token}
```

<<<<<<< HEAD
Para obtener un token:

```bash
POST http://localhost:8080/api/auth/login
{
  "username": "admin",
  "password": "admin123",
  "rol" : "EL rol elegido"
}
```

## Roles disponibles
=======
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

### Roles disponibles
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)

| Rol | Descripción |
|---|---|
| `ADMIN` | Acceso total al sistema |
| `USER` | Cliente del sistema |
| `PROVEEDOR` | Proveedor de materiales o servicios |

<<<<<<< HEAD
## Integrantes del equipo

- Robinson Salvo
- Cristobal Cargnino

---
**ConstructApp** — Proyecto académico DUOC UC
=======
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

## Instrucciones de ejecución local (sin Docker)

### Requisitos previos
- Java 21+
- Maven 3.8+
- MySQL 8.0 corriendo en localhost:3306

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

---

## Perfiles de configuración

| Perfil | Uso |
|---|---|
| `dev` (por defecto) | Desarrollo local, MySQL en localhost, show-sql activado |
| `prod` | Despliegue con Docker, variables de entorno, show-sql desactivado |

---

**ConstructApp** — Proyecto académico DUOC UC — DSY1103 Desarrollo FullStack 1
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
