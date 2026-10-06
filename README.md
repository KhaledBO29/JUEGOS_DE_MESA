# Sistema de Gestión de Juegos de Mesa UCC

Aplicación web para administrar la ludoteca universitaria de la Universidad Cooperativa de Colombia. El sistema centraliza el catálogo de juegos de mesa, la disponibilidad de ejemplares, las reservas temporales, los préstamos a profesores, las transferencias a estudiantes y las devoluciones con control de novedades.

La solución incluye una interfaz web responsive y una API REST construida con Spring Boot, persistencia JPA y base de datos H2 en memoria para ejecución local y demostraciones.

## Características Principales

- Catálogo visual de juegos con imágenes, categorías, número de jugadores y duración estimada.
- Búsqueda por nombre o categoría.
- Consulta de componentes por cada juego.
- Registro y validación de usuarios por carnet.
- Reservas temporales de ejemplares disponibles.
- Préstamos asociados a un profesor responsable.
- Transferencia de posesión del juego a estudiantes.
- Devolución con reporte de piezas faltantes o daños físicos.
- Historial de eventos para trazabilidad de cada préstamo.
- Carga automática de datos iniciales para pruebas.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 24, Spring Boot 3.5.5 |
| Persistencia | Spring Data JPA, Hibernate |
| Base de datos | H2 en memoria |
| Frontend | HTML, Tailwind CSS, JavaScript |
| Pruebas | JUnit, Spring Boot Test |
| Gestión | Maven |

## Estructura del Proyecto

```text
src/
├── main/
│   ├── java/edu/ucc/juegosdemesa/
│   │   ├── config/          # Carga de datos iniciales
│   │   ├── controller/      # Endpoints REST
│   │   ├── factory/         # Evaluación del estado de devolución
│   │   ├── model/           # Entidades y enumeraciones
│   │   ├── repository/      # Repositorios JPA
│   │   └── service/         # Reglas de negocio
│   └── resources/
│       ├── static/          # Interfaz web y recursos gráficos
│       └── application.properties
└── test/                    # Pruebas unitarias y de configuración
```

## Requisitos

- JDK 24
- Maven 3.9 o superior
- Git

## Ejecución Local

Desde la raíz del proyecto, ejecuta:

```bash
mvn spring-boot:run
```

Luego abre la aplicación en:

```text
http://localhost:8080
```

La base de datos H2 se inicializa automáticamente al arrancar la aplicación. Al detener el servidor, la información almacenada en memoria se reinicia.

## Usuarios de Prueba

| Rol | Carnet | Nombre |
|---|---|---|
| Profesor | `PROF-001` | Dra. Ana Torres |
| Estudiante | `EST-1001` | Santiago Gómez |
| Estudiante | `EST-1002` | Valentina Ríos |

## Catálogo Inicial

El sistema carga doce juegos de mesa con sus respectivos componentes e imágenes:

| Juego | Categoría |
|---|---|
| Ajedrez | Estrategia |
| Catan | Estrategia |
| Monopoly | Familiar |
| Scrabble | Palabras |
| Ticket to Ride | Aventura |
| Pandemic | Cooperativo |
| Dixit | Creatividad |
| UNO | Cartas |
| Carcassonne | Estrategia |
| Risk | Estrategia |
| Clue | Misterio |
| Azul | Abstracto |

## Flujo Funcional

1. El usuario valida su carnet en la interfaz.
2. Consulta el catálogo y revisa disponibilidad, componentes y detalles del juego.
3. Puede reservar un ejemplar disponible durante el tiempo configurado.
4. Un profesor puede registrar el préstamo de un ejemplar disponible o convertir una reserva activa en préstamo.
5. El profesor responsable puede transferir la posesión del juego a un estudiante.
6. El profesor responsable o el estudiante poseedor actual puede registrar la devolución.
7. El sistema evalúa el estado final del ejemplar según las novedades reportadas.

## Reglas de Estado

| Condición de devolución | Estado final del ejemplar |
|---|---|
| Sin daños y sin piezas faltantes | `DISPONIBLE` |
| Con piezas faltantes | `DEVUELTO_CON_NOVEDAD` |
| Con daño físico | `MANTENIMIENTO` |

Los daños físicos tienen prioridad sobre las piezas faltantes. Si se reportan ambos casos, el ejemplar queda en mantenimiento.

## API REST

Todos los endpoints usan el prefijo `/api`. La interfaz web se sirve desde `/`.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/juegos` | Lista el catálogo con disponibilidad y componentes. |
| `GET` | `/api/ejemplares` | Lista el inventario físico. |
| `GET` | `/api/usuarios/carnet/{carnet}` | Consulta un usuario por carnet. |
| `POST` | `/api/usuarios` | Registra un nuevo usuario. |
| `POST` | `/api/reservas` | Crea una reserva para un ejemplar disponible. |
| `GET` | `/api/reservas?activas=true` | Lista reservas activas. |
| `POST` | `/api/reservas/{id}/cancelacion` | Cancela una reserva activa. |
| `POST` | `/api/prestamos` | Registra un préstamo. |
| `PATCH` | `/api/prestamos/{id}/transferencias` | Transfiere la posesión del préstamo a un estudiante. |
| `POST` | `/api/prestamos/{id}/devolucion` | Registra la devolución del préstamo. |
| `GET` | `/api/prestamos?activos=true` | Lista préstamos activos. |
| `GET` | `/api/prestamos?activos=false` | Lista el historial completo de préstamos. |
| `GET` | `/api/prestamos/{id}` | Consulta un préstamo con su historial de eventos. |

## Ejemplos de Solicitudes

Crear una reserva:

```json
{
  "codigoSerie": "AJD-001",
  "carnet": "EST-1001"
}
```

Registrar un préstamo:

```json
{
  "codigoSerie": "CAT-001",
  "profesorCarnet": "PROF-001",
  "observaciones": "Entrega en buen estado",
  "reservaId": null
}
```

Registrar una devolución:

```json
{
  "carnetActor": "PROF-001",
  "piezasFaltantes": false,
  "danos": false,
  "observaciones": "Juego completo"
}
```

## Configuración

Archivo principal:

```text
src/main/resources/application.properties
```

Configuración relevante:

| Propiedad | Descripción |
|---|---|
| `spring.datasource.url` | URL de la base H2 en memoria. |
| `spring.h2.console.enabled` | Habilita la consola H2 para desarrollo. |
| `spring.jpa.hibernate.ddl-auto` | Define la generación del esquema. |
| `app.reservas.duracion-horas` | Duración de las reservas temporales. |

## Consola H2

Mientras la aplicación está en ejecución, la consola local está disponible en:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:juegosdemesa` |
| Usuario | `sa` |
| Contraseña | Vacía |

## Pruebas

Para ejecutar las pruebas automatizadas:

```bash
mvn test
```

## Consideraciones de Seguridad

Esta versión está orientada a ejecución local, demostración académica y validación funcional. La autenticación por carnet permite aplicar reglas de rol dentro del sistema, pero no reemplaza un mecanismo institucional de autenticación.

Antes de usar la aplicación en producción se recomienda:

- Integrar un proveedor de identidad institucional.
- Proteger los endpoints REST con autenticación y autorización.
- Deshabilitar la consola H2.
- Reemplazar la base H2 en memoria por una base de datos persistente.
- Configurar variables de entorno para credenciales y parámetros sensibles.

## Autor

Proyecto académico desarrollado para la gestión de préstamos de juegos de mesa en entorno universitario.
