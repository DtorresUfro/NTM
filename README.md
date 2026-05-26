# NTM - Backend (Sistema de Gestión de notas y salas grupales)

Este repositorio contiene el código fuente del backend para la aplicación **NTM**, enfocado en la organizacion y persistencia para el control y la gestión de nota dentro de salas. 

El sistema está construido bajo el ecosistema de **Java** y **Spring Boot**, siguiendo un diseño basado en capas para asegurar la mantenibilidad y el desacoplamiento de componentes.

## Tecnologías y Dependencias Principales

* **Lenguaje:** Java 17+
* **Framework Base:** Spring Boot 3.5
* **Capa Web:** Spring Web (REST API)
* **Capa de Datos:** Spring Data JPA (Persistencia)
* **Motor de Base de Datos:** MySQL
* **Pruebas Unitarias e Integración:** JUnit 5, Mockito y Spring Boot Test

---

## Arquitectura del Proyecto

El backend se organiza bajo una estructura clásica de paquetes orientada a separar responsabilidades:

```text
src/main/java/com/Ntm/
├── controller/  # Controladores REST: Exponen los Endpoints HTTP y manejan las peticiones.
├── service/     # Capa de Negocio: Contiene las reglas, validaciones y lógica del sistema.
├── repository/  # Capa de Datos: Interfaces JPA para interactuar con la Base de Datos.
├── entity/      # Modelo de Dominio: Entidades que representan las tablas de la BD (ej: Room).
├── dto/         # Data Transfer Objects: Objetos inmutables de entrada y salida (Request/Response).
└── exception/   # Manejo de Errores: Excepciones personalizadas para la lógica de negocio.
