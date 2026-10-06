<div align="center">

# Sistema de Gestion de Juegos de Mesa UCC

### Ludoteca universitaria para reservas, prestamos, transferencias y devoluciones de juegos de mesa

![Java](https://img.shields.io/badge/Java-24-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-09476B?style=for-the-badge)
![Tailwind](https://img.shields.io/badge/Tailwind-CSS-38B2AC?style=for-the-badge&logo=tailwindcss&logoColor=white)

Aplicacion web academica para administrar el catalogo, inventario, reservas, prestamos y trazabilidad de una ludoteca universitaria.

<a href="#ejecucion-local">Ejecucion</a>
&nbsp;|&nbsp;
<a href="#api-rest">API REST</a>
&nbsp;|&nbsp;
<a href="#flujo-funcional">Flujo</a>
&nbsp;|&nbsp;
<a href="#seguridad">Seguridad</a>

</div>

---

## Vista General

Este proyecto centraliza la gestion de juegos de mesa de la Universidad Cooperativa de Colombia. Permite validar usuarios por carnet, consultar disponibilidad, reservar ejemplares, registrar prestamos, transferir posesion a estudiantes y cerrar devoluciones con reporte de novedades.

La aplicacion combina una interfaz web responsive con una API REST en Spring Boot, usando JPA/Hibernate para persistencia y una base H2 en memoria para pruebas locales.

<table>
  <tr>
    <td align="center"><img src="src/main/resources/static/images/games/ajedrez.png" width="180" alt="Ajedrez"><br><strong>Ajedrez</strong></td>
    <td align="center"><img src="src/main/resources/static/images/games/catan.png" width="180" alt="Catan"><br><strong>Catan</strong></td>
    <td align="center"><img src="src/main/resources/static/images/games/azul.png" width="180" alt="Azul"><br><strong>Azul</strong></td>
    <td align="center"><img src="src/main/resources/static/images/games/ticket-to-ride.png" width="180" alt="Ticket to Ride"><br><strong>Ticket to Ride</strong></td>
  </tr>
</table>

---

## Modulos Principales

| Modulo | Descripcion |
|---|---|
| Catalogo | Lista los juegos disponibles con imagen, categoria, duracion, jugadores y componentes. |
| Reservas | Aparta temporalmente un ejemplar disponible para un usuario validado. |
| Prestamos | Registra entregas a profesores responsables y bloquea el ejemplar durante la transaccion. |
| Transferencias | Permite que el profesor responsable transfiera la posesion a un estudiante. |
| Devoluciones | Evalua piezas faltantes y danos fisicos para definir el estado final del ejemplar. |
| Historial | Mantiene trazabilidad de eventos por cada prestamo. |

## Caracteristicas

- Catalogo visual con imagenes reales para cada juego.
- Busqueda por nombre y filtro por categoria.
- Consulta de componentes del ejemplar.
- Registro de usuarios desde la interfaz.
- Validacion por carnet universitario.
- Reservas con expiracion configurable.
- Prestamos con reglas de rol.
- Transferencia de posesion profesor-estudiante.
- Devoluciones con reporte de novedades.
- Historial de eventos para auditoria.
- Datos iniciales cargados automaticamente.

---

## Stack Tecnologico

| Capa | Tecnologia |
|---|---|
| Backend | Java 24, Spring Boot 3.5.5 |
| API | Spring Web |
| Persistencia | Spring Data JPA, Hibernate |
| Base de datos | H2 Database |
| Frontend | HTML, Tailwind CSS, JavaScript |
| Validacion | Spring Validation |
| Pruebas | JUnit, Spring Boot Test |
| Gestion de dependencias | Maven |

---

## Estructura del Proyecto

```text
src/
|-- main/
|   |-- java/edu/ucc/juegosdemesa/
|   |   |-- config/          Datos iniciales
|   |   |-- controller/      Endpoints REST
|   |   |-- factory/         Evaluacion de estados
|   |   |-- model/           Entidades y enums
|   |   |-- repository/      Repositorios JPA
|   |   `-- service/         Reglas de negocio
|   `-- resources/
|       |-- static/          Interfaz web y recursos
|       `-- application.properties
`-- test/                    Pruebas automatizadas
```

---

## Ejecucion Local

### Requisitos

| Herramienta | Version recomendada |
|---|---|
| JDK | 24 |
| Maven | 3.9 o superior |
| Git | Version reciente |

### Levantar la aplicacion

```bash
mvn spring-boot:run
```

Luego abre:

```text
http://localhost:8080
```

La base de datos H2 se crea en memoria al iniciar la aplicacion. Al detener el servidor, los datos se reinician.

### Ejecutar pruebas

```bash
mvn test
```

---

## Usuarios de Prueba

| Rol | Carnet | Nombre |
|---|---|---|
| Profesor | `PROF-001` | Dra. Ana Torres |
| Estudiante | `EST-1001` | Santiago Gomez |
| Estudiante | `EST-1002` | Valentina Rios |

---

## Catalogo Inicial

| Juego | Categoria | Codigo inicial |
|---|---|---|
| Ajedrez | Estrategia | `AJD-001` |
| Catan | Estrategia | `CAT-001` |
| Monopoly | Familiar | `MON-001` |
| Scrabble | Palabras | `SCR-001` |
| Ticket to Ride | Aventura | `TRK-001` |
| Pandemic | Cooperativo | `PAN-001` |
| Dixit | Creatividad | `DIX-001` |
| UNO | Cartas | `UNO-001` |
| Carcassonne | Estrategia | `CAR-001` |
| Risk | Estrategia | `RKS-001` |
| Clue | Misterio | `CLU-001` |
| Azul | Abstracto | `AZU-001` |

---

## Flujo Funcional

```text
Usuario valida carnet
        |
        v
Consulta catalogo y disponibilidad
        |
        v
Reserva ejemplar o solicita prestamo
        |
        v
Profesor registra prestamo
        |
        v
Profesor puede transferir posesion a estudiante
        |
        v
Se registra devolucion
        |
        v
Sistema actualiza estado e historial
```

## Reglas de Estado

| Condicion de devolucion | Estado final |
|---|---|
| Juego completo y sin danos | `DISPONIBLE` |
| Piezas faltantes | `DEVUELTO_CON_NOVEDAD` |
| Danos fisicos | `MANTENIMIENTO` |

Los danos fisicos tienen prioridad. Si una devolucion reporta piezas faltantes y danos, el ejemplar queda en `MANTENIMIENTO`.

---

## API REST

Todos los endpoints usan el prefijo `/api`. La interfaz web se sirve desde `/`.

| Metodo | Endpoint | Descripcion |
|---|---|---|
| `GET` | `/api/juegos` | Lista catalogo, disponibilidad y componentes. |
| `GET` | `/api/ejemplares` | Lista inventario fisico. |
| `GET` | `/api/usuarios/carnet/{carnet}` | Consulta usuario por carnet. |
| `POST` | `/api/usuarios` | Registra un nuevo usuario. |
| `POST` | `/api/reservas` | Crea una reserva. |
| `GET` | `/api/reservas?activas=true` | Lista reservas activas. |
| `POST` | `/api/reservas/{id}/cancelacion` | Cancela una reserva. |
| `POST` | `/api/prestamos` | Registra un prestamo. |
| `PATCH` | `/api/prestamos/{id}/transferencias` | Transfiere posesion a estudiante. |
| `POST` | `/api/prestamos/{id}/devolucion` | Registra devolucion. |
| `GET` | `/api/prestamos?activos=true` | Lista prestamos activos. |
| `GET` | `/api/prestamos?activos=false` | Lista historial completo. |
| `GET` | `/api/prestamos/{id}` | Consulta prestamo e historial. |

### Ejemplos

Crear reserva:

```json
{
  "codigoSerie": "AJD-001",
  "carnet": "EST-1001"
}
```

Registrar prestamo:

```json
{
  "codigoSerie": "CAT-001",
  "profesorCarnet": "PROF-001",
  "observaciones": "Entrega en buen estado",
  "reservaId": null
}
```

Registrar devolucion:

```json
{
  "carnetActor": "PROF-001",
  "piezasFaltantes": false,
  "danos": false,
  "observaciones": "Juego completo"
}
```

---

## Configuracion

Archivo principal:

```text
src/main/resources/application.properties
```

| Propiedad | Uso |
|---|---|
| `spring.datasource.url` | URL de la base H2 en memoria. |
| `spring.datasource.username` | Usuario de conexion. |
| `spring.datasource.password` | Contrasena de conexion. |
| `spring.h2.console.enabled` | Habilita la consola H2. |
| `spring.jpa.hibernate.ddl-auto` | Controla la generacion del esquema. |
| `app.reservas.duracion-horas` | Define la duracion de una reserva. |

## Consola H2

Disponible mientras la aplicacion esta en ejecucion:

```text
http://localhost:8080/h2-console
```

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:juegosdemesa` |
| Usuario | `sa` |
| Contrasena | vacia |

---

## Seguridad

Esta version esta orientada a desarrollo local, demostracion academica y validacion funcional. La validacion por carnet aplica reglas de rol dentro del sistema, pero no sustituye un mecanismo institucional de autenticacion.

Antes de un despliegue real se recomienda:

- Integrar autenticacion institucional.
- Proteger endpoints con autorizacion por rol.
- Deshabilitar la consola H2.
- Cambiar H2 en memoria por una base persistente.
- Externalizar credenciales y parametros sensibles.
- Configurar perfiles separados para desarrollo y produccion.

---

## Estado del Proyecto

| Area | Estado |
|---|---|
| Catalogo | Implementado |
| Reservas | Implementado |
| Prestamos | Implementado |
| Transferencias | Implementado |
| Devoluciones | Implementado |
| Historial | Implementado |
| Interfaz web | Implementada |
| Pruebas base | Implementadas |

---

<div align="center">

### Universidad Cooperativa de Colombia

Proyecto academico para la gestion de prestamos de juegos de mesa en un entorno universitario.

**Rama estable:** `main`  
**Rama de desarrollo:** `develop`

**Desarrollado por Khaled Benavides, Julio Bolaños & Manuel Viveros**

</div>
