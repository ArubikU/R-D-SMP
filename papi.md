# PlaceholderAPI (rdsmp)

Formato general: `%rdsmp_<clave>%`. Prefijo fijo: `rdsmp`. Las claves distinguen mayusculas/minusculas.

## Listas de eventos/modificadores
- `%rdsmp_active_event_<n>%` / `%rdsmp_active_modifier_<n>%`: item n (1-index) de la lista de modificadores/eventos activos ordenada alfabeticamente. Devuelve vacio si el indice no existe.
- `%rdsmp_active_events_count%` / `%rdsmp_active_modifiers_count%`: cantidad de modificadores/eventos activos.
- `%rdsmp_active_events%` / `%rdsmp_active_modifiers%`: lista separada por comas de modificadores/eventos activos o `Ninguno`.
- `%rdsmp_last_event%`: ultimo evento disparado o `Ninguno`.
- `%rdsmp_last_event_1%`, `%rdsmp_last_event_2%`, `%rdsmp_last_event_3%`: historial desde el mas reciente (1) hacia atras. Devuelve vacio si no hay suficientes.
- `%rdsmp_next_event%`: tiempo hasta el siguiente evento en formato `Hh Mm`.
- `%rdsmp_next_event_exact%`: fecha y hora exacta del siguiente evento (toString del Instant).

## Estado del jugador
- `%rdsmp_lives%`: vidas actuales del jugador.
- `%rdsmp_eliminated%`: `si` si esta eliminado, `no` en caso contrario.
- `%rdsmp_role%`: nombre del rol o `Sin rol`.

## Estado del juego
- `%rdsmp_day%`: dia actual del evento.

## Equipos
- `%rdsmp_team%`: nombre del equipo o `Sin equipo`.
- `%rdsmp_team_wars_count%`: numero de guerras activas del equipo (0 si no tiene equipo).
- `%rdsmp_team_wars%`: lista de guerras activas separada por comas o `Sin guerras`.
- `%rdsmp_team_friendly_fire%`: `on` si el PVP interno esta activado, `off` si esta desactivado o no tiene equipo.

## Combate
- `%rdsmp_combat_remaining%`: segundos restantes en combate.
- `%rdsmp_combat_status%`: `en_combate` si quedan segundos, `libre` si no.
- `%rdsmp_combat_opponent%`: nombre del ultimo oponente en combate (puede ser vacio si no hay).

## Tirada diaria
- `%rdsmp_daily_roll_ready%`: `ready` si puede tirar, `wait` si aun tiene cooldown.
- `%rdsmp_daily_roll_remaining%`: tiempo restante en formato `Hh Mm`.
- `%rdsmp_daily_roll_time%`: segundos restantes para la siguiente tirada.
