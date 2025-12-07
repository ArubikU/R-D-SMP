# Calendario y Soft Locks

Líneas de tiempo y bloqueos automáticos para evitar saltarse el progreso semanal.

- Día 1: Inicio de temporada. Solo Overworld.
- Día 7: Se desbloquea el Nether. Antes de este día los portales al Nether están bloqueados.
- Día 14: Se desbloquea el End. Antes de este día los portales al End están bloqueados.
- Día 31+: Permadeath activada. Las muertes expulsan definitivamente al jugador.

Notas rápidas
- Los portales cancelados muestran un mensaje con el día de desbloqueo.
- El estado se actualiza en la API `status.json` para que la web lo muestre.
- Las fechas usan el contador de días del GameManager (día real).