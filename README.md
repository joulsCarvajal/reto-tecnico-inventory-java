# Reto Técnico - Líder Técnico Java

Aplicación full-stack de gestión empresarial con inventario, construida con Java Spring Boot en el backend y Quasar Framework (Vue.js) en el frontend.

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17 · Spring Boot 3.2 · Spring Security · Hibernate/JPA |
| Base de datos | PostgreSQL 15 · Flyway (migraciones) |
| Seguridad | JWT (jjwt 0.11) · BCrypt |
| PDF | iText 8 |
| Email | SendGrid REST API |
| Frontend | Quasar Framework 2 · Vue.js 3 · Pinia · Axios |
| Infraestructura | Docker · Docker Compose |

---

## Credenciales de Acceso

| Perfil | Correo | Contraseña | Accesos |
|--------|--------|------------|---------|
| Administrador | admin@reto.com | Admin123! | Empresas (CRUD), Productos, Inventario |
| Externo | externo@reto.com | Externo123! | Empresas (solo lectura) |

---

## Inicio Rápido con Docker Compose

### Prerrequisitos
- Docker Desktop instalado y en ejecución

### 1. Clonar y configurar

```bash
git clone <URL_REPOSITORIO>
cd reto-tecnico
```

### 2. Configurar SendGrid (opcional para envío de correo)

```bash
cp .env.example .env
# Editar .env y agregar tu API key de SendGrid
```

```env
SENDGRID_API_KEY=SG.tu_api_key_real
SENDGRID_FROM_EMAIL=tu@correo.com
```

> Si no configuras SendGrid, la descarga de PDF funcionará normalmente. El envío por correo retornará un error.

### 3. Levantar la aplicación

```bash
docker-compose up -d
```

Esto inicia:
- **PostgreSQL** en el puerto `5432`
- **Backend** en `http://localhost:8080/api`
- **Frontend** en `http://localhost:9000`

### 4. Acceder

Abrir `http://localhost:9000` en el navegador e iniciar sesión con las credenciales de arriba.

---

## Instalación Local (sin Docker)

### Backend

**Prerrequisitos:** Java 17+, Maven 3.9+, PostgreSQL 15

```bash
# Crear base de datos
psql -U postgres -c "CREATE DATABASE retotecnico;"
psql -U postgres -c "CREATE USER reto_user WITH PASSWORD 'reto_pass';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE retotecnico TO reto_user;"

# Ejecutar backend
cd backend
mvn spring-boot:run
```

El backend levanta en `http://localhost:8080/api`. Flyway ejecuta las migraciones automáticamente al inicio.

### Frontend

**Prerrequisitos:** Node.js 18+, npm

```bash
cd frontend
npm install -g @quasar/cli
npm install
quasar dev
```

El frontend levanta en `http://localhost:9000`.

---

## API REST - Endpoints Principales

### Autenticación
```
POST /api/auth/login           → { correo, password } → JWT token
```

### Empresas
```
GET    /api/empresas            → Listar todas (ADMIN + EXTERNO)
GET    /api/empresas/{nit}      → Detalle por NIT
POST   /api/empresas            → Crear empresa (ADMIN)
PUT    /api/empresas/{nit}      → Actualizar empresa (ADMIN)
DELETE /api/empresas/{nit}      → Eliminar empresa (ADMIN)
```

### Productos
```
GET    /api/productos            → Listar todos (ADMIN)
GET    /api/productos?empresaNit=X → Por empresa
GET    /api/productos/{id}       → Detalle
POST   /api/productos            → Crear (ADMIN)
PUT    /api/productos/{id}       → Actualizar (ADMIN)
DELETE /api/productos/{id}       → Eliminar (ADMIN)
```

### Inventario
```
GET    /api/inventario?empresaNit=X → Inventario por empresa (ADMIN)
POST   /api/inventario              → Agregar/actualizar (ADMIN)
DELETE /api/inventario/{id}         → Eliminar registro (ADMIN)
GET    /api/inventario/pdf?empresaNit=X        → Descargar PDF (ADMIN)
POST   /api/inventario/enviar-correo           → Enviar PDF por email (ADMIN)
```

### Categorías
```
GET    /api/categorias           → Listar (autenticado)
```

---

## Modelo Entidad-Relación

```
empresa (nit PK, nombre, direccion, telefono)
    │
    ├──< producto (id PK, codigo UNIQUE, nombre, caracteristicas, empresa_nit FK)
    │       ├──< producto_precio (producto_id FK, moneda, precio) [multi-moneda]
    │       └──>< categoria (id PK, nombre) [many-to-many via producto_categoria]
    │
    └──< inventario (id PK, empresa_nit FK, producto_id FK, cantidad) [UNIQUE por empresa+producto]

usuario (id PK, correo UNIQUE, password_hash, nombre, rol[ADMINISTRADOR|EXTERNO])

cliente (id PK, nombre, correo UNIQUE, telefono, direccion)
    └──< orden (id PK, cliente_id FK, estado, total)
            └──< orden_producto (orden_id FK, producto_id FK, cantidad, precio_unit) [PK compuesta]
```

**Restricciones cumplidas del reto:**
- ✅ Producto ↔ Categoría: many-to-many
- ✅ Cliente → Órdenes: one-to-many
- ✅ Orden ↔ Productos: many-to-many con atributos (cantidad, precio_unit)

---

## Arquitectura del Backend

Se aplicó **Clean Architecture** con las siguientes capas:

```
com.reto.tecnico/
├── application/               ← Casos de uso
│   ├── dto/request/           ← DTOs de entrada (validados con Bean Validation)
│   ├── dto/response/          ← DTOs de salida
│   └── service/impl/          ← Lógica de negocio
├── infrastructure/
│   ├── config/                ← Configuración Spring Security, CORS
│   ├── email/                 ← Integración SendGrid
│   ├── pdf/                   ← Generación PDF con iText
│   ├── persistence/
│   │   ├── entity/            ← Entidades JPA/Hibernate
│   │   └── repository/        ← Spring Data JPA interfaces
│   ├── security/
│   │   ├── jwt/               ← JwtTokenProvider
│   │   └── filter/            ← JwtAuthenticationFilter
│   └── web/
│       ├── controller/        ← REST Controllers
│       └── advice/            ← GlobalExceptionHandler
└── RetoTecnicoApplication.java
```

**Principios SOLID aplicados:**
- **S** - Cada servicio/clase tiene una responsabilidad única
- **O** - Servicios extensibles sin modificar código existente
- **D** - Controllers dependen de interfaces de servicio, no implementaciones concretas

---

## Decisiones Técnicas

| Decisión | Justificación |
|----------|--------------|
| JWT Stateless | Escala horizontalmente sin sesiones compartidas |
| Flyway | Control de versiones del esquema de BD, reproducible en cualquier ambiente |
| BCrypt strength 10 | Balance seguridad/rendimiento estándar en la industria |
| iText 8 | Librería madura con soporte activo; genera PDFs de alta calidad programáticamente |
| SendGrid REST | Entregabilidad superior a SMTP directo; API simple y confiable |
| Pinia | Reemplaza Vuex en Vue 3; más simple y con mejor TypeScript support |
| Docker multi-stage | Imagen final mínima (~120MB) separando build de runtime |
| `@UpdateTimestamp` / `@CreationTimestamp` | Auditoría automática sin código extra |
| `upsert` en inventario | Idempotente: crear o actualizar con el mismo endpoint simplifica el frontend |

---

## Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/retotecnico` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de BD | `reto_user` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de BD | `reto_pass` |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT | *(cambiar en producción)* |
| `SENDGRID_API_KEY` | API Key de SendGrid | `SG.placeholder` |
| `SENDGRID_FROM_EMAIL` | Correo remitente | `noreply@retotecnico.com` |

---

## Uso de Inteligencia Artificial

Este proyecto fue desarrollado con asistencia de IA (Claude). Las herramientas de IA aceleraron:
- Generación del scaffolding inicial (estructura de carpetas, pom.xml)
- Boilerplate de entidades JPA, DTOs y controladores
- Configuración de Spring Security con JWT

En todos los casos se aplicó **criterio técnico** para:
- Validar la arquitectura y patrones elegidos (Clean Architecture, SOLID)
- Ajustar las relaciones del modelo de datos a los requisitos del negocio
- Revisar la configuración de seguridad (CORS, roles, manejo de errores)
- Asegurar que las decisiones técnicas son justificables y mantenibles
