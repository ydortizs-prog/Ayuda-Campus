# Prompts utilizados

Usé Gemini en Android Studio en la rama mejora-ia. Después de cada respuesta revisé que respetara MVVM + Repository antes de aplicar los cambios, compilé y probé en el emulador.

## Prompt 1: tiempo promedio de resolución

**Texto enviado a Gemini:**
Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un AyudaRepository (interface) con la implementación AyudaRepositoryRoom. Agrega en el panel de soporte el tiempo promedio de resolución de los tickets cerrados (fechaCierre - fecha). Reglas: la consulta va en TicketDao y devuelve Flow (sin suspend); como el promedio es nulo cuando no hay tickets cerrados, el tipo debe aceptar nulo. La función se declara primero en la interface AyudaRepository y luego se implementa en AyudaRepositoryRoom. El valor se expone como StateFlow en PanelViewModel, ya convertido a horas. PanelScreen solo lo muestra en una tarjeta que use MaterialTheme.colorScheme (sin colores fijos), y debe mostrar un guion si todavía no hay datos. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
Me generó cambios en [TicketDao, AyudaRepository, AyudaRepositoryRoom, PanelViewModel y PanelScreen]: una consulta con AVG sobre los tickets cerrados, la función en el Repository, un StateFlow en el ViewModel y una tarjeta en el panel.

**Qué tuve que corregir yo:**
[Elige lo que de verdad pasó]
- Sin correcciones, funcionó a la primera.
- La consulta devolvía Double sin aceptar nulo, y la cambié a Double? para que no falle cuando no hay tickets cerrados.
- Declaró la función solo en AyudaRepositoryRoom, y la agregué primero en la interface.
- Otra: [describe con tus palabras]

**¿Respeta MVVM?:**
[Sí: la View solo muestra el valor, el ViewModel usa solo la interface y la consulta está en el DAO.] / [No: ... explica qué rompió y cómo lo corregí.]

## Prompt 2: contador de SLA por prioridad

**Texto enviado a Gemini:**
Tengo una app Kotlin con Jetpack Compose, Material 3 y MVVM. TicketDetalleViewModel expone el ticket como StateFlow<TicketConDetalle?>. Agrega un contador de SLA: el tiempo restante para atender un ticket según su prioridad (Alta 4 horas, Media 24 horas, Baja 72 horas), calculado como fecha del ticket + horas del SLA - ahora. Reglas: la lógica del cálculo va en el ViewModel (no en el Composable) y se expone como StateFlow; debe recalcularse cada minuto para que el contador avance. Si el ticket ya está RESUELTO o CERRADO, no hay contador. Si el plazo ya venció, debe indicar cuánto tiempo lleva vencido. Esta mejora no cambia la base de datos, por lo que no se toca ningún DAO ni el Repository. DetalleTicketScreen solo muestra el texto, por ejemplo "SLA: 2 h 30 min restantes" o "SLA vencido hace 1 h", usando MaterialTheme.colorScheme y los colores de prioridad que ya existen (sin colores nuevos). No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
Me generó cambios en [TicketDetalleViewModel y DetalleTicketScreen]: un cálculo del tiempo restante según la prioridad, expuesto como StateFlow, y un texto en la pantalla de detalle.

**Qué tuve que corregir yo:**
[Elige lo que de verdad pasó]
- Sin correcciones, funcionó a la primera.
- Calculaba el tiempo dentro del Composable, y moví la lógica al ViewModel.
- Solo se calculaba una vez, y agregué la actualización cada minuto.
- Mostraba el contador también en tickets resueltos o cerrados, y lo quité.
- Otra: [describe con tus palabras]

**¿Respeta MVVM?:**
[Sí: la regla del SLA está en el ViewModel y la pantalla solo muestra el texto, sin tocar el Repository ni la base.] / [No: ... explica qué rompió y cómo lo corregí.]

## Prompt 3: búsqueda avanzada en la bandeja

**Texto enviado a Gemini:**
Tengo una app Kotlin con Jetpack Compose, Material 3 y MVVM. BandejaViewModel ya filtra los tickets por estado con FiltroTicket y combine. Agrega búsqueda avanzada en la bandeja de Soporte: filtrar por prioridad, por categoría y por texto, todos al mismo tiempo (y junto con el filtro de estado que ya existe). El texto busca por título, número de ticket o nombre del cliente, sin distinguir mayúsculas. Reglas: el filtrado se hace en BandejaViewModel combinando los filtros con combine, sin cambiar el orden por prioridad que ya entrega el DAO. Las categorías para el filtro salen de repo.observarCategorias(), que ya existe en AyudaRepository, así que no se agregan funciones nuevas al Repository ni al DAO. BandejaScreen solo agrega los controles (un campo de texto, chips de prioridad y chips de categoría) y llama a funciones del ViewModel. Usa MaterialTheme.colorScheme, sin colores fijos ni XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:**
Me generó cambios en [BandejaViewModel y BandejaScreen]: nuevos estados de filtro combinados con combine, y controles de búsqueda y chips en la pantalla.

**Qué tuve que corregir yo:**
[Elige lo que de verdad pasó]
- Sin correcciones, funcionó a la primera.
- La combinación de flujos no compilaba, y la corregí agrupando los filtros.
- Filtraba en la pantalla en vez de en el ViewModel, y moví el filtrado.
- Alteraba el orden por prioridad, y lo dejé como lo entrega el DAO.
- Otra: [describe con tus palabras]

**¿Respeta MVVM?:**
[Sí: el filtrado está en el ViewModel y la pantalla solo muestra los controles y llama funciones.] / [No: ... explica qué rompió y cómo lo corregí.]

## Reflexión

- **¿Qué mejoró la IA y qué no?** [Elige o reescribe] La IA me ahorró tiempo escribiendo las consultas y los controles de la pantalla, pero no conoce mis reglas de arquitectura y tuve que revisarlas yo.
- **¿Qué error cometió?** [Escribe el error real que viste. Si no hubo ninguno importante, escribe "No cometió errores graves; lo más notable fue ..."]
- **¿Qué aprendí sobre MVVM, Room y reglas de negocio?** [Con tus palabras, 1 o 2 líneas. Por ejemplo, qué capa debe tener cada regla y por qué una consulta de promedio puede devolver nulo.]