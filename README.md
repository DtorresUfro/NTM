# NoteTask Manager (NTM)

NoteTask Manager es una aplicación web para organizar trabajo colaborativo dentro de salas grupales. Permite crear salas, incorporar participantes, compartir notas, gestionar tareas y visualizar las tareas existentes en un calendario interno.

El proyecto incluye una interfaz web con Thymeleaf, una API REST desarrollada con Spring Boot, persistencia en MySQL, actualizaciones mediante WebSocket y Nginx como proxy inverso.

## Funcionalidades principales

- Crear una sala y generar su identificador y Master Key.
- Unirse a una sala mediante `roomId` y nombre de usuario.
- Recuperar acceso administrativo mediante Master Key.
- Consultar y administrar participantes.
- Crear, editar y eliminar notas.
- Crear, editar, completar y eliminar tareas.
- Visualizar las tareas existentes en el calendario de la sala.
- Recibir actualizaciones de la sala mediante WebSocket.
- Persistir salas, participantes, notas y tareas en MySQL.

## Calendario

NTM utiliza un calendario interno para mostrar las tareas asociadas a una sala según sus fechas.

El calendario cumple únicamente una función de **visualización**. La creación, edición, finalización y eliminación de tareas se realiza por separado desde el módulo de tareas.

Esta funcionalidad no requiere iniciar sesión con Google ni configurar credenciales externas.

## Tecnologías principales

| Tecnología | Versión o referencia |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.0 |
| Maven | Maven Wrapper 3.9.15 |
| Spring Web | Gestionada por Spring Boot |
| Thymeleaf | Gestionada por Spring Boot |
| Spring Data JPA | Gestionada por Spring Boot |
| Spring WebSocket | Gestionada por Spring Boot |
| MySQL | 8.4 |
| Nginx | 1.27 Alpine |
| JUnit 5 y Mockito | Pruebas Java |
| H2 | Base de datos para pruebas |
| JaCoCo | 0.8.12 |
| Jest | Pruebas JavaScript |
| Docker Compose | Levantamiento local |

## Arquitectura general

```text
Navegador
   |
   | HTTP / WebSocket
   v
Nginx :80
   |
   v
Spring Boot :8080
   |
   +--> Controller
   |       |
   |       v
   |     Service
   |       |
   |       v
   |    Repository
   |       |
   |       v
   +----> MySQL :3306
```

La aplicación sigue una arquitectura por capas:

- `controller`: controladores REST y controladores de vistas.
- `service`: reglas de negocio.
- `repository`: acceso a datos con Spring Data JPA.
- `entity`: entidades persistidas en la base de datos.
- `dto`: objetos de entrada y salida.
- `exception`: excepciones del dominio.
- `templates`: vistas Thymeleaf.
- `static`: recursos del frontend.
- `config`: configuración de la aplicación y WebSocket.

## Prerrequisitos

### Ejecución con Docker

- Git.
- Docker Desktop iniciado.
- Docker Compose v2, disponible mediante `docker compose`.

No es necesario instalar Java, Maven ni MySQL para levantar el proyecto con Docker.

### Ejecución de pruebas

Para ejecutar las pruebas Java fuera de Docker:

- JDK 21.

Para ejecutar las pruebas JavaScript:

- Node.js y npm.

## Clonar el repositorio

```bash
git clone https://github.com/DtorresUfro/NTM.git
cd NTM
```

## Levantar el proyecto

El comando recomendado para construir y levantar todos los servicios es:

```bash
docker compose up --build -d
```

Comprueba que los contenedores estén activos:

```bash
docker compose ps
```

La aplicación queda disponible en:

```text
http://localhost
```

También se puede acceder mediante:

```text
http://localhost:80
```

### Uso posterior

Cuando no sea necesario reconstruir las imágenes:

```bash
docker compose up -d
```

### Ver los registros

```bash
docker compose logs -f
```

## Variables de entorno

Para el levantamiento actual con Docker Compose no es necesario crear un archivo `.env`, porque las variables requeridas están definidas directamente en `docker-compose.yml`.

La aplicación utiliza:

| Variable | Propósito |
|---|---|
| `DB_URL` | URL JDBC de MySQL |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes permitidos para CORS y WebSocket |

Por esta razón, no se requiere un archivo `.env.example` para ejecutar el proyecto con la configuración actual.

## Servicios y puertos

| Servicio | Contenedor | Puerto interno | Puerto local | Propósito |
|---|---|---:|---:|---|
| `nginx` | `ntm-nginx` | 80 | 80 | Proxy inverso y acceso web |
| `app` | `ntm-backend` | 8080 | No publicado | Aplicación Spring Boot |
| `mysql` | `ntm-mysql` | 3306 | 3307 | Persistencia de datos |

El servicio `app` no se publica directamente en el equipo. El navegador accede por Nginx en el puerto `80`.

## Comandos de administración

Detener los servicios sin eliminarlos:

```bash
docker compose stop
```

Detener y eliminar contenedores y redes:

```bash
docker compose down
```

Reconstruir después de modificar el código:

```bash
docker compose up --build -d
```

Reconstrucción limpia sin borrar la base de datos:

```bash
docker compose down --remove-orphans
docker compose up --build -d
```

Eliminar contenedores, redes y volúmenes:

```bash
docker compose down --volumes --remove-orphans
docker compose up --build -d
```

> **Advertencia:** `--volumes` elimina el volumen de MySQL y borra los datos almacenados localmente.

## Ejecutar pruebas

### Pruebas Java

En Windows PowerShell:

```powershell
.\mvnw.cmd test
```

En Bash:

```bash
./mvnw test
```

Las pruebas utilizan JUnit 5, Mockito, MockMvc, Spring Boot Test y una base de datos H2 en memoria. No es necesario levantar MySQL para ejecutarlas.

El reporte de cobertura de JaCoCo se genera en:

```text
target/site/jacoco/index.html
```

### Pruebas JavaScript

Instala las dependencias la primera vez:

```bash
npm install
```

Ejecuta las pruebas:

```bash
npm test
```

## Rutas web principales

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Página principal |
| `GET` | `/crear-sala` | Formulario para crear una sala |
| `GET` | `/sala-creada` | Información de la sala creada |
| `GET` | `/join-options` | Opciones para ingresar |
| `GET` | `/unirse-sala` | Formulario para unirse a una sala |
| `GET` | `/admin-access` | Acceso administrativo con Master Key |
| `GET` | `/room/{roomId}` | Sala de trabajo |

## API REST principal

### Salas y participantes

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/rooms` | Crear una sala |
| `POST` | `/api/rooms/join` | Unirse a una sala |
| `DELETE` | `/api/rooms/{roomId}` | Eliminar una sala |
| `POST` | `/api/rooms/validate-masterkey` | Validar una Master Key |
| `GET` | `/api/rooms/{roomId}/members` | Listar participantes |
| `POST` | `/api/rooms/remove-participant` | Remover un participante |
| `POST` | `/api/rooms/leave` | Salir de una sala |
| `POST` | `/api/rooms/presence` | Marcar presencia en una sala |

### Tareas

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/rooms/{roomId}/tasks` | Listar tareas |
| `POST` | `/api/rooms/tasks` | Crear una tarea |
| `PUT` | `/api/rooms/tasks` | Editar una tarea |
| `PUT` | `/api/rooms/tasks/complete` | Cambiar el estado de una tarea |
| `DELETE` | `/api/rooms/tasks` | Eliminar una tarea |

### Notas

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/rooms/{roomId}/notes` | Listar notas |
| `POST` | `/api/rooms/notes` | Crear una nota |
| `PUT` | `/api/rooms/notes` | Editar una nota |
| `DELETE` | `/api/rooms/notes` | Eliminar una nota |

### Notificaciones

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/notifications/{room}/{user}` | Listar notificaciones |
| `PUT` | `/notifications/{id}/read` | Marcar una notificación como leída |

## WebSocket

| Elemento | Ruta |
|---|---|
| Endpoint STOMP | `/ws` |
| Canal de la sala | `/topic/rooms/{roomId}` |

Nginx reenvía las conexiones WebSocket al servicio Spring Boot.

## Estructura principal

```text
NTM/
├── .mvn/
├── nginx/
│   └── nginx.conf
├── src/
│   ├── main/
│   │   ├── java/com/ntm/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── jest.config.js
├── mvnw
├── mvnw.cmd
├── package.json
├── pom.xml
└── README.md
```

## Base de datos

- Motor: MySQL 8.4.
- Base de datos: `ntm_db`.
- Puerto interno: `3306`.
- Puerto publicado en el equipo: `3307`.
- Volumen persistente: `mysql_data`.
- Hibernate administra el esquema mediante `spring.jpa.hibernate.ddl-auto=update`.

Para reiniciar los servicios sin borrar los datos:

```bash
docker compose down
docker compose up --build -d
```

Para restablecer completamente la base de datos:

```bash
docker compose down --volumes
docker compose up --build -d
```

> El segundo procedimiento elimina todos los datos almacenados en el volumen local de MySQL.

## Solución de problemas

### Docker Desktop no está iniciado

Inicia Docker Desktop antes de ejecutar cualquier comando `docker compose`.

### El puerto 80 está ocupado

En PowerShell:

```powershell
Get-NetTCPConnection -LocalPort 80
```

### El puerto 3307 está ocupado

En PowerShell:

```powershell
Get-NetTCPConnection -LocalPort 3307
```

### La aplicación no conecta con MySQL

Revisa el estado y los registros:

```bash
docker compose ps
docker compose logs -f mysql
docker compose logs -f app
```

### Los cambios de código no aparecen

Reconstruye la imagen:

```bash
docker compose up --build -d
```

### WebSocket no conecta

Comprueba que `APP_CORS_ALLOWED_ORIGINS` permita el origen utilizado para acceder a la aplicación. Con Nginx, el origen habitual es:

```text
http://localhost
```

## Estado actual

La versión actual incluye:

- Interfaz web con Thymeleaf.
- Creación y acceso a salas.
- Administración de participantes.
- Gestión de notas.
- Gestión de tareas.
- Visualización de tareas en un calendario interno.
- Persistencia con MySQL.
- Actualizaciones mediante WebSocket.
- Pruebas Java y JavaScript.
- Levantamiento con Docker Compose, Nginx y MySQL.

## URL de despliegue

La aplicación se ejecuta localmente mediante Docker Compose y se publica temporalmente en Internet utilizando un servicio de túnel.

**Interfaz web:**

```https://8t77xwerwryi.shares.zrok.io/
```


