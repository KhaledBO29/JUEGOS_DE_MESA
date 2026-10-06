# Sistema de juegos de mesa UCC

Aplicación web universitaria para administrar el catálogo, reservas temporales, préstamos a profesores, transferencias a estudiantes y devoluciones con registro de incidencias. Incluye una interfaz con búsqueda por nombre, filtro por categoría, consulta de componentes e historial, y formularios conectados a los endpoints del servidor.

## Requisitos y ejecución

- JDK 24
- Maven 3.9 o superior

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

Abre [http://localhost:8080](http://localhost:8080). El catálogo y los usuarios de demostración se cargan al iniciar la aplicación; la base H2 en memoria se reinicia al detener el servidor.

Carnets de prueba:

| Perfil | Carnet |
|---|---|
| Profesor responsable | `PROF-001` |
| Estudiante | `EST-1001` |
| Estudiante | `EST-1002` |

## API REST

Todos los endpoints usan el prefijo `/api`. La interfaz estática se sirve en `/`.

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/juegos` | Catálogo con disponibilidad y componentes |
| `GET` | `/api/ejemplares` | Inventario físico |
| `GET` | `/api/usuarios/carnet/{carnet}` | Validar y consultar titular |
| `POST` | `/api/usuarios` | Registrar usuario |
| `POST` | `/api/reservas` | Apartar ejemplar disponible indicando `codigoSerie` y `carnet` |
| `GET` | `/api/reservas?activas=true` | Consultar reservas activas |
| `POST` | `/api/reservas/{id}/cancelacion` | Cancelar indicando el carnet del titular |
| `POST` | `/api/prestamos` | Crear préstamo indicando `codigoSerie`, `profesorCarnet`, `observaciones` y, al convertir una reserva, `reservaId` |
| `PATCH` | `/api/prestamos/{id}/transferencias` | Transferir indicando `profesorCarnet` y `estudianteCarnet` |
| `POST` | `/api/prestamos/{id}/devolucion` | Devolver indicando `carnetActor`, `piezasFaltantes`, `danos` y `observaciones` |
| `GET` | `/api/prestamos?activos=true` | Listar préstamos activos (sin el parámetro se lista el historial completo) |
| `GET` | `/api/prestamos/{id}` | Consultar préstamo e historial de eventos |

Las reservas apartan un ejemplar `DISPONIBLE` (estado `RESERVADO`) durante 24 horas por defecto. La duración se cambia con `app.reservas.duracion-horas`; las reservas vencidas liberan automáticamente el ejemplar al consultar reservas o al crear otra reserva. El profesor puede convertir una reserva activa en préstamo enviando su `reservaId`. El titular de la reserva puede cancelarla. Los estados de devolución se resuelven así: juego completo y sin daños → `DISPONIBLE`; piezas faltantes → `DEVUELTO_CON_NOVEDAD`; daño físico → `MANTENIMIENTO` (prioritario aunque también falten piezas). Solo el profesor responsable puede transferir; el profesor responsable o el estudiante poseedor actual puede registrar la devolución.

La interfaz contiene doce juegos con inventario inicial y componentes: Ajedrez, Catan, Monopoly, Scrabble, Ticket to Ride, Pandemic, Dixit, UNO, Carcassonne, Risk, Clue y Azul. Cada tarjeta permite apartar un ejemplar o consultar sus componentes; el carnet de prueba se puede usar para registrar préstamos, traspasos, cancelaciones y check-in. También se puede crear un usuario desde **Registrar usuario**. El botón **Ver historial** muestra préstamos activos y cerrados y permite abrir la trazabilidad de cada operación.

## Flujo de `PrestamoService`

1. **Préstamo:** busca el ejemplar por código de serie y bloquea su fila durante la transacción para evitar préstamos simultáneos. Comprueba que esté `DISPONIBLE` o que pertenezca a la reserva activa indicada; valida el carnet del profesor, convierte la reserva si aplica, crea el préstamo, guarda el primer evento de auditoría y cambia el ejemplar a `PRESTADO`.
2. **Transferencia:** bloquea el ejemplar del préstamo activo, verifica que el actor sea su profesor responsable y que el nuevo carnet pertenezca a un estudiante. Actualiza al poseedor actual y agrega un evento con los carnets de origen y destino; el profesor responsable y el historial se conservan.
3. **Devolución:** bloquea el ejemplar y exige que actúe el profesor responsable o el poseedor actual. `EvaluadorEstadoFactory` evalúa faltantes y daños (daños tienen prioridad), cierra el préstamo, preserva las observaciones iniciales, actualiza el estado del ejemplar y registra el evento de devolución.
4. **Reserva:** bloquea una copia disponible y la aparta hasta la fecha de expiración configurada. Cancelar o vencer libera esa copia; convertirla en préstamo cierra la reserva dentro de la misma transacción que registra el préstamo.

Para revisar la base local mientras la aplicación corre, visita `/h2-console` y usa JDBC URL `jdbc:h2:mem:juegosdemesa`, usuario `sa` y contraseña vacía. No se debe exponer la consola ni la base en memoria como configuración de producción.

La validación por carnet de esta versión identifica al titular y aplica reglas de rol, pero no prueba por sí sola la identidad ante clientes no confiables. Antes de desplegarla en producción, debe integrarse con el proveedor de identidad institucional y proteger los endpoints y la consola H2.
