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

```
Authorization: Bearer {token}
```

Para obtener un token:

```bash
POST http://localhost:8091/api/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

## Roles disponibles

| Rol | Descripción |
|---|---|
| `ADMIN` | Acceso total al sistema |
| `USER` | Cliente del sistema |
| `PROVEEDOR` | Proveedor de materiales o servicios |

## Integrantes del equipo

- Robinson Salvo
- Cristobal Cargnino

---
**ConstructApp** — Proyecto académico DUOC UC
