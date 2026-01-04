# RollAndDeathSMP - Documentación del Sistema de Scripting

## Índice
1. [Conceptos Básicos](#conceptos-básicos)
2. [Sistema de Variables y Scopes](#sistema-de-variables-y-scopes)
3. [Acciones](#acciones)
4. [Condiciones](#condiciones)
5. [Operaciones Matemáticas](#operaciones-matemáticas)
6. [Sistema de Partículas](#sistema-de-partículas)
7. [Sistema de Proyectiles](#sistema-de-proyectiles)
8. [Items Personalizados](#items-personalizados)
9. [Modificadores](#modificadores)
10. [Mobs Personalizados](#mobs-personalizados)
11. [Recetas](#recetas)
12. [Script Library](#script-library)

---

## Conceptos Básicos

El sistema de scripting permite definir comportamientos complejos en YAML sin código Java. Se basa en:

- **Eventos**: Disparadores (player_interact, entity_damage, etc.)
- **Condiciones**: Evalúan si se ejecutan las acciones
- **Acciones**: Comandos que modifican el juego
- **Variables**: Almacenan datos durante la ejecución

### Estructura de una Regla

```yaml
events:
  nombre_evento:
    require_all:           # Condiciones (todas deben cumplirse)
      - { type: condicion, ... }
    require_any:           # Condiciones (al menos una debe cumplirse)
      - { type: condicion, ... }
    on_pass:               # Acciones si las condiciones pasan
      - { type: accion, ... }
    on_fail:               # Acciones si las condiciones fallan
      - { type: accion, ... }
    deny_on_fail: false    # Si true, cancela el evento al fallar
```

---

## Sistema de Variables y Scopes

### Scopes Disponibles

| Scope | Descripción |
|-------|-------------|
| `EVENT` | Datos del evento actual |
| `SUBJECT` | Entidad que ejecuta el script (jugador/mob) |
| `TARGET` | Entidad objetivo |
| `LOCATION` | Ubicación del evento |
| `STATE` | Variables persistentes |

### Kinds (Tipos de Acceso)

| Kind | Sintaxis | Descripción |
|------|----------|-------------|
| `NATIVE` | `EVENT.damage` | Acceso directo a propiedades del evento |
| `GENERIC/custom` | `EVENT.custom.mi_var` | Variables personalizadas del script |
| `CACHE/args` | `EVENT.args.parametro` | Argumentos pasados a callbacks |
| `STATE` | `EVENT.state.persistente` | Variables persistentes entre ejecuciones |

### Ejemplos de Paths

```yaml
# Variables nativas del evento
EVENT.action          # Tipo de acción (RIGHT_CLICK_AIR, etc.)
EVENT.damage          # Daño del evento
EVENT.hand            # Mano usada (MAIN_HAND, OFF_HAND)
EVENT.block           # Bloque interactuado
EVENT.item            # Item usado
EVENT.entity          # Entidad del evento
EVENT.player          # Jugador del evento

# Variables personalizadas
EVENT.custom.mi_daño        # Variable definida en el script
EVENT.custom.multiplicador  # Otra variable custom

# Argumentos de callbacks (on_hit, on_tick, etc.)
EVENT.args.power           # Argumento pasado con "args:"
EVENT.args.freeze_radius   # Otro argumento

# Acceso al sujeto
SUBJECT                    # El jugador/entidad ejecutora
SUBJECT.health             # Vida del sujeto
SUBJECT.location           # Ubicación del sujeto

# Ubicación
LOCATION                   # Ubicación del evento
```

### Guardar y Leer Variables

```yaml
# Guardar variable
- type: set_var
  key: EVENT.custom.mi_variable
  value: 10

# Copiar variable
- type: copy_var
  from: EVENT.damage
  to: EVENT.custom.daño_base

# Operaciones matemáticas
- type: math_set_var
  key: EVENT.custom.resultado
  op: mul
  a_key: EVENT.custom.daño_base
  b: 1.5
```

---

## Acciones

### Control de Flujo

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `call` | `actions_call`, `call_actions`, `macro` | Ejecuta bloque de la librería | `ref`, `with` |
| `deny` | - | Deniega el resultado | - |
| `cancel_event` | - | Cancela el evento de Bukkit | - |
| `run_later` | `run_actions_later`, `delay` | Ejecuta después de X ticks | `delay`, `actions` |
| `run_repeating` | `repeat` | Ejecuta repetidamente | `delay`, `period`, `times`, `actions` |
| `for_in` | `foreach`, `iterate` | Itera sobre una lista | `list`, `var`, `actions` |
| `for_each_entity_in_all_worlds` | `for_each_entity_all_worlds` | Itera entidades en todos los mundos | `type`, `actions` |
| `for_each_online_player` | - | Itera jugadores online | `actions` |

#### Ejemplo: call

```yaml
- type: call
  ref: mi_macro
  with:
    param1: EVENT.custom.valor
    param2: 100
```

### Variables

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `set_var` | - | Establece una variable | `key`, `value` |
| `copy_var` | - | Copia una variable a otra | `from`, `to` |
| `add_var` | - | Suma a una variable | `key`, `value` |
| `math_set_var` | - | Operación matemática | `key`, `op`, `a`, `b`, `c` |
| `set_random_var` | `select_random`, `random_var` | Variable aleatoria | `key`, `min`, `max` |
| `modify_variable` | `modify_var`, `var_op` | Modifica con operación | `key`, `operation`, `value` |
| `set_var_now_plus` | - | Variable con timestamp + offset | `key`, `plus_ms` |
| `random_bool_to_var` | `random_boolean_to_var` | Booleano aleatorio a variable | `key`, `chance` |

### PDC (Persistent Data Container)

| Acción | Descripción | Parámetros |
|--------|-------------|------------|
| `read_item_pdc_to_var` | Lee PDC del item a variable | `key`, `data_type`, `var` |

```yaml
- type: read_item_pdc_to_var
  key: staff_damage        # Key del PDC
  data_type: DOUBLE        # DOUBLE, INTEGER, STRING, etc.
  var: EVENT.custom.daño   # Variable destino
```

### Jugador

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `set_player_cooldown` | - | Cooldown de material | `material`, `ticks` |
| `add_player_food` | - | Modifica hambre | `amount` |
| `heal` | `heal_entity`, `heal_player`, `set_health` | Cura al jugador | `amount`, `target` |
| `damage` | `damage_entity`, `damage_player`, `kill` | Daña a una entidad | `amount`, `target`, `cause` |
| `apply_effect` | `effect`, `potion_effect`, `add_effect` | Aplica efecto de poción | `target`, `effect`, `duration`, `amplifier` |
| `apply_random_effect` | - | Aplica efecto aleatorio | `target`, `effects`, `duration` |
| `remove_effect` | - | Quita efecto | `target`, `effect` |
| `message` | `send_message`, `msg` | Envía mensaje | `message`, `target` |
| `teleport` | `tp`, `teleport_entity`, `teleport_player` | Teletransporta | `target`, `location` |
| `add_velocity` | - | Añade velocidad | `target`, `x`, `y`, `z` |
| `add_velocity_random` | - | Añade velocidad aleatoria | `target`, `min`, `max` |
| `set_player_velocity_forward` | `dash_forward` | Impulso hacia adelante | `strength` |
| `set_player_riptiding` | - | Activa riptide | `ticks` |
| `give_item` | - | Da item al jugador | `item`, `amount` |
| `damage_item_durability` | - | Daña durabilidad | `slot`/`slot_key`, `amount` |
| `damage_helmet_durability` | - | Daña durabilidad del casco | `amount` |
| `unequip_item` | - | Desequipa item | `slot` |
| `consume_event_item` | `consume_item` | Consume item del evento | `amount` |
| `consume_extra_hand_item` | - | Consume item de la otra mano | `material`, `amount` |
| `ensure_player_has_item` | - | Asegura que tenga item | `item`, `amount` |
| `set_player_inventory_hide_tooltip` | - | Oculta tooltips del inventario | `hide` |
| `rotate_online_player_positions` | - | Rota posiciones de jugadores | - |
| `add_lives` | - | Añade vidas | `amount` |
| `take_lives` | - | Quita vidas | `amount` |
| `set_lives` | - | Establece vidas | `amount` |
| `assign_random_role` | `random_role` | Asigna rol aleatorio | - |
| `give_daily_roll_reward` | `daily_roll_reward` | Da recompensa diaria | - |
| `add_attribute_modifier` | `add_player_attribute_modifier` | Añade modificador de atributo | `attribute`, `amount`, `operation` |
| `remove_attribute_modifier` | `remove_player_attribute_modifier` | Quita modificador | `attribute`, `name` |
| `stack_player_attribute_modifier` | `stack_attribute_modifier` | Apila modificadores | `attribute`, `amount` |
| `set_view_distance` | - | Cambia distancia de visión | `distance` |
| `set_compass_target_random` | - | Brújula a ubicación aleatoria | - |
| `set_compass_target_spawn` | - | Brújula al spawn | - |
| `reflect_damage_if_blocking` | - | Refleja daño si bloquea | `percent` |
| `pull_nearby_items` | - | Atrae items cercanos | `radius` |

```yaml
# Cooldown
- { type: set_player_cooldown, material: DIAMOND_HOE, ticks: 40 }

# Curar
- { type: heal, target: SUBJECT, amount: 4.0 }

# Efecto
- type: apply_effect
  target: SUBJECT
  effect: SPEED
  duration: 200    # ticks
  amplifier: 1     # nivel - 1

# Daño de durabilidad (dinámico con EVENT.hand)
- { type: damage_item_durability, slot_key: EVENT.hand, amount: 1 }
```

### Modificar Eventos

| Acción | Descripción | Parámetros |
|--------|-------------|------------|
| `set_event_damage` | Establece daño | `value` |
| `multiply_event_damage` | Multiplica daño | `value` |
| `multiply_event_exp` | Multiplica experiencia | `value` |
| `multiply_food_loss` | Multiplica pérdida de hambre | `value` |
| `multiply_explosion_radius` | Multiplica radio de explosión | `value` |
| `cancel_event` | Cancela el evento | - |
| `set_event_use_interacted_block` | Permite/niega uso de bloque | `value` |
| `set_event_use_item_in_hand` | Permite/niega uso de item | `value` |
| `add_event_death_drop` | Añade drop a muerte | `item`, `amount` |
| `clear_event_death_drops` | Limpia drops de muerte | - |
| `duplicate_drops` | Duplica drops de muerte | `multiplier` |
| `remove_event_item_entity` | Elimina entidad item del evento | - |
| `set_block_break_drop_items` | Permite/niega drops de bloque | `value` |
| `set_hide_tooltip_from_event_items` | Oculta tooltips de items | `hide` |
| `bow_refund_consumable` | Reembolsa munición de arco | - |
| `projectile_add_random_spread` | Añade dispersión a proyectil | `spread` |
| `set_event_skeleton_bow_interval` | Intervalo de arco de skeleton | `ticks` |
| `clear_anvil_result` | Limpia resultado del yunque | - |
| `restore_anvil` | Restaura yunque | - |

### Sonidos

| Acción | Descripción | Parámetros |
|--------|-------------|------------|
| `play_sound` | Reproduce sonido | `sound`, `volume`, `pitch`, `location` |
| `stop_all_sounds` | Detiene todos los sonidos | `target` |

```yaml
- type: play_sound
  sound: ENTITY_BLAZE_SHOOT
  volume: 1.0
  pitch: 1.2
  location: SUBJECT
```

### Mundo

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `set_block` | `block`, `change_block`, `set_block_type_at` | Coloca bloque | `location`, `material` |
| `set_block_property` | `block_prop` | Modifica propiedad de bloque | `location`, `property`, `value` |
| `get_block` | - | Lee bloque a variable | `location`, `var` |
| `explode` | `explosion`, `create_explosion` | Explosión | `location`, `power`, `fire`, `break_blocks` |
| `lightning` | `strike_lightning`, `lightning_strike`, `thunder` | Rayo | `location`, `effect_only` |
| `freeze_area` | - | Congela área | `where`, `radius`/`radius_key`, `air_chance` |
| `set_weather_all_worlds` | - | Cambia clima | `weather`, `duration` |
| `set_world_time` | - | Cambia hora | `time` |
| `set_gamerule_all_worlds` | - | Cambia gamerule global | `rule`, `value` |
| `location_offset` | - | Desplaza ubicación | `location`, `x`, `y`, `z` |
| `place_torches_around` | - | Coloca antorchas alrededor | `location`, `radius` |
| `set_portal_destination_random` | - | Portal a destino aleatorio | - |
| `find_top_block_near` | - | Busca bloque superior cercano | `location`, `radius`, `var` |
| `select_locations` | `select_location`, `find_location` | Selecciona ubicaciones | `selector`, `var` |
| `set_var_target_block_location` | `raycast_block_location` | Raycast a ubicación de bloque | `var`, `max_distance` |
| `lava_flow_like_water` | - | La lava fluye como agua | - |
| `set_protection_purge_active` | - | Activa protección de purga | `active` |

```yaml
# Congelar área con radio dinámico
- type: freeze_area
  where: LOCATION
  radius_key: EVENT.args.freeze_radius  # Radio desde variable
  air_chance: 0.5
  air_material: POWDER_SNOW
  water_material: ICE
```

### Entidades

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `spawn` | `spawn_entity`, `spawn_mob`, `spawn_entity_at_key` | Spawnea entidad | `type`, `location`, `name` |
| `spawn_passenger` | `spawn_passenger_for_mob` | Spawnea pasajero | `entity`, `type` |
| `spawn_mount` | `spawn_mount_for_mob` | Spawnea montura | `entity`, `type` |
| `select_entities` | `select_entity`, `find_entities` | Selecciona entidades | `selector`, `radius`, `var` |
| `set_target` | `set_mob_target`, `mob_target` | Establece target de mob | `entity`, `target` |
| `add_passenger` | `set_passenger`, `mount` | Añade pasajero | `entity`, `passenger` |
| `gravity_pull_near_location` | `gravity_pull` | Atrae entidades | `location`, `radius`, `strength` |
| `set_attribute` | `set_attribute_base`, `set_mob_attribute_base` | Modifica atributo | `target`, `attribute`, `value` |
| `set_entity_property` | - | Modifica propiedad de entidad | `entity`, `property`, `value` |
| `get_entity_property` | - | Lee propiedad de entidad | `entity`, `property`, `var` |
| `set_nametag_visibility` | - | Visibilidad del nombre | `entity`, `visible` |
| `set_monsters_silent_all_worlds` | - | Silencia monstruos | `silent` |
| `set_skeletons_bow_interval_all_worlds` | - | Intervalo de arco de skeletons | `ticks` |

### Aldeanos

| Acción | Descripción | Parámetros |
|--------|-------------|------------|
| `inflate_villager_prices_all_worlds` | Infla precios globalmente | `percent` |
| `deflate_villager_prices_all_worlds` | Deflaciona precios globalmente | `percent` |
| `inflate_event_villager_prices` | Infla precios del evento | `percent` |
| `multiply_villager_trade_cost` | Multiplica costo de trade | `multiplier` |
| `fair_trade_piglin_barter` | Trueque justo de piglin | - |

```yaml
# Atraer entidades
- type: gravity_pull_near_location
  location: LOCATION
  radius: 5
  strength_key: EVENT.args.pull_power
  include_players: true
  include_mobs: true
```

### Broadcast y Comunicación

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `broadcast` | - | Mensaje global | `message` |
| `broadcast_with_location` | - | Mensaje con coords | `message`, `location` |
| `discord_announce_with_location` | - | Anuncio a Discord con coords | `message`, `location` |
| `command` | `cmd`, `run_command` | Ejecuta comando | `command`, `as` |

### Sonidos

| Acción | Descripción | Parámetros |
|--------|-------------|------------|
| `play_sound` | Reproduce sonido | `sound`, `volume`, `pitch`, `location` |
| `stop_all_sounds` | Detiene todos los sonidos | `target` |

### Items y Drops

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `drop_item_at_location` | `drop_item_at`, `drop_item_naturally` | Dropea item | `location`, `item`, `amount` |
| `fill_chest_loot` | `fill_container_loot` | Llena cofre con loot table | `location`, `loot_table` |
| `slip_drop_hand_item` | - | Suelta item de la mano | - |

### Partículas

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `spawn_particle_shape` | `spawn_particles` | Genera partículas con forma | Ver sección dedicada |
| `play_particle` | - | Genera partículas simples | `particle`, `location`, `count` |
| `start_particle_system` | - | Inicia sistema persistente | `id`, `particle`, `shape` |
| `stop_particle_system` | - | Detiene sistema | `id` |

### Proyectiles

| Acción | Alias | Descripción | Parámetros |
|--------|-------|-------------|------------|
| `launch_curved_projectile` | `curved_projectile`, `scripted_projectile` | Lanza proyectil curvo | Ver sección dedicada |

### Acciones Especiales de Modificadores

| Acción | Descripción | Parámetros |
|--------|-------------|------------|
| `cursed_earth_spawn_giant_with_loot` | Spawnea gigante con loot | - |
| `cursed_earth_restore_loot_on_giant_death` | Restaura loot al morir gigante | - |
| `legendary_fisher` | Pesca legendaria | - |
| `fortune_touch_best_drop` | Mejor drop con fortuna | - |
| `lumberjack_break_tree` | Rompe árbol completo | - |
| `mirror_world_enderman_passive` | Enderman pasivo | - |
| `mirror_world_pig_aggressive` | Cerdo agresivo | - |
| `persistent_shadow_start` | Inicia sombra persistente | - |
| `persistent_shadow_stop` | Detiene sombra | - |

---

## Condiciones

### Lógicas

| Condición | Alias | Descripción | Parámetros |
|-----------|-------|-------------|------------|
| `any_of` | - | Al menos una cumple | `conditions` |
| `all_of` | - | Todas cumplen | `conditions` |
| `not` | - | Niega condición | `condition` |
| `random_chance` | - | Probabilidad | `chance` (0.0-1.0) |
| `call` | `cond_call`, `call_condition`, `macro` | Llama condición de librería | `ref` |

```yaml
require_all:
  - type: any_of
    conditions:
      - { type: var_equals, key: EVENT.action, value: "RIGHT_CLICK_AIR" }
      - { type: var_equals, key: EVENT.action, value: "RIGHT_CLICK_BLOCK" }
```

### Variables

| Condición | Descripción | Parámetros |
|-----------|-------------|------------|
| `var_truthy` | Variable es verdadera | `key` |
| `var_is_missing` | Variable no existe | `key` |
| `var_equals` | Variable igual a valor | `key`, `value` |
| `var_in` | Variable en lista | `key`, `values` |
| `var_compare` | Comparación numérica | `key`, `op` (`>`, `<`, `>=`, `<=`, `==`, `!=`), `value` |
| `var_matches_regex` | Coincide con regex | `key`, `pattern` |
| `now_ms_gte_var` | Timestamp actual >= variable | `key` |

```yaml
# Variable en lista
- type: var_in
  key: EVENT.action
  values: ["RIGHT_CLICK_AIR", "RIGHT_CLICK_BLOCK"]

# Comparación
- type: var_compare
  key: EVENT.custom.daño
  op: ">="
  value: 10
```

### Jugador

| Condición | Alias | Descripción | Parámetros |
|-----------|-------|-------------|------------|
| `game_mode_in` | `player_game_mode_in` | Modo de juego | `modes` |
| `in_water` | `player_in_water`, `player_is_in_water` | Está en agua | - |
| `has_cooldown` | `player_has_cooldown` | Tiene cooldown | `material`, `value` |
| `inventory_full` | `player_inventory_full` | Inventario lleno | - |
| `has_permission` | `player_has_permission` | Tiene permiso | `permission` |
| `lives_at_least` | - | Vidas mínimas | `amount` |
| `role_is` | - | Rol específico | `role` |

```yaml
- { type: has_cooldown, material: DIAMOND_HOE, value: false }
- { type: game_mode_in, modes: ["SURVIVAL", "ADVENTURE"] }
```

### Mundo

| Condición | Alias | Descripción | Parámetros |
|-----------|-------|-------------|------------|
| `time_between` | `world_time_between` | Hora del día | `from`, `to` |
| `has_storm` | `world_has_storm` | Hay tormenta | - |
| `is_thundering` | `world_is_thundering` | Hay rayos | - |
| `environment_is` | `world_environment_is` | Dimensión | `value` (NORMAL, NETHER, THE_END) |
| `sky_light_at_least` | `player_sky_light_at_least` | Luz del cielo mínima | `level` |
| `min_day` | - | Día mínimo | `day` |

### Tags y Materiales

| Condición | Alias | Descripción | Parámetros |
|-----------|-------|-------------|------------|
| `object_tag` | `is_tagged`, `in_tag`, `has_tag` | Objeto tiene tag | `key`, `tag` |
| `material_in_tag` | - | Material en tag | `tag` |
| `material_is_ore` | - | Material es mineral | - |

```yaml
# Verificar si es mineral
- { type: material_is_ore }

# Verificar tag específico
- type: object_tag
  key: EVENT.block
  tag: "#minecraft:logs"
```

### Otros

| Condición | Descripción | Parámetros |
|-----------|-------------|------------|
| `modifier_active` | Modificador activo | `id` |
| `placeholder_compare` | Compara placeholder | `placeholder`, `op`, `value` |

---

## Operaciones Matemáticas

### math_set_var

```yaml
- type: math_set_var
  key: EVENT.custom.resultado    # Variable destino
  op: add                        # Operación
  a: 10                          # Primer operando (literal)
  a_key: EVENT.custom.base       # O desde variable
  b: 5                           # Segundo operando
  b_key: EVENT.damage            # O desde variable
  c: 100                         # Tercer operando (para clamp)
```

### Operaciones Disponibles

| Operación | Alias | Fórmula | Descripción |
|-----------|-------|---------|-------------|
| `add` | `+` | a + b | Suma |
| `sub` | `-` | a - b | Resta |
| `mul` | `*` | a * b | Multiplicación |
| `div` | `/` | a / b | División |
| `mod` | `%` | a % b | Módulo |
| `pow` | `^` | a^b | Potencia |
| `sqrt` | - | √a | Raíz cuadrada |
| `abs` | - | \|a\| | Valor absoluto |
| `sin` | - | sin(a) | Seno |
| `cos` | - | cos(a) | Coseno |
| `tan` | - | tan(a) | Tangente |
| `min` | - | min(a,b) | Mínimo |
| `max` | - | max(a,b) | Máximo |
| `clamp` | - | clamp(a, b, c) | Limitar entre b y c |
| `random` | - | random(a,b) | Aleatorio entre a y b |
| `round` | - | round(a) | Redondear |
| `floor` | - | floor(a) | Redondear hacia abajo |
| `ceil` | - | ceil(a) | Redondear hacia arriba |

### Ejemplo Complejo

```yaml
# Calcular daño con multiplicador y límite
- type: math_set_var
  key: EVENT.custom.daño_final
  op: mul
  a_key: EVENT.custom.daño_base
  b: 1.5

- type: math_set_var
  key: EVENT.custom.daño_final
  op: clamp
  a_key: EVENT.custom.daño_final
  b: 1.0      # mínimo
  c: 100.0    # máximo
```

---

## Sistema de Partículas

### spawn_particle_shape

```yaml
- type: spawn_particle_shape
  center: LOCATION              # Ubicación central
  follow_entity: SUBJECT        # O seguir entidad
  particle: FLAME               # Tipo de partícula
  shape: SPHERE                 # Forma
  points: 30                    # Puntos de la forma
  radius: 2.0                   # Radio
  height: 1.0                   # Altura (para HELIX, CONE)
  turns: 2                      # Vueltas (para HELIX, SPIRAL)
  count: 1                      # Partículas por punto
  offset_x: 0.0                 # Spread X
  offset_y: 0.0                 # Spread Y
  offset_z: 0.0                 # Spread Z
  speed: 0.1                    # Velocidad/extra
```

### Formas Disponibles

| Forma | Descripción | Parámetros Relevantes |
|-------|-------------|----------------------|
| `POINT` | Punto único | count, offset |
| `CIRCLE` | Círculo horizontal | radius, points |
| `RING` | Anillo (círculo 3D) | radius, points |
| `HELIX` | Hélice/espiral vertical | radius, height, turns, points |
| `SPIRAL` | Espiral plana | radius, turns, points |
| `SPHERE` | Esfera | radius, points |
| `CONE` | Cono | radius, height, points |
| `FORMULA` | Fórmula matemática | formula_x, formula_y, formula_z |

### Partículas de Polvo (Dust)

```yaml
- type: spawn_particle_shape
  particle: DUST
  dust_color: "#FF0000"    # Color hex
  dust_size: 1.5           # Tamaño
  shape: SPHERE
  radius: 2.0
```

### Sistema Persistente

```yaml
# Iniciar sistema de partículas
- type: start_particle_system
  id: "mi_aura"
  follow_entity: SUBJECT
  particle: ENCHANT
  shape: HELIX
  interval: 2              # Ticks entre spawns

# Detener sistema
- type: stop_particle_system
  id: "mi_aura"
```

---

## Sistema de Proyectiles

### launch_curved_projectile

```yaml
- type: launch_curved_projectile
  projectile: SNOWBALL          # Tipo de proyectil
  shooter_key: SUBJECT          # Quién dispara
  speed: 8.0                    # Velocidad
  speed_key: EVENT.custom.vel   # O desde variable
  curve_height: 2.0             # Altura de la curva
  curve_height_key: ...         # O desde variable
  curve_side: 0.0               # Desviación lateral
  homing: false                 # Si sigue al target
  target_y_offset: 1.0          # Offset Y del target
  duration_ticks: 40            # Duración máxima
  
  # Explosión
  explode_on_impact: true
  explode_on_finish: true
  explosion_power: 2.0
  explosion_power_key: EVENT.custom.power
  explosion_fire: false
  explosion_break_blocks: false
  
  # Targets permitidos
  targets: ["MONSTER", "PLAYER"]
  
  # Argumentos para callbacks
  args:
    mi_param: EVENT.custom.valor
    otro_param: 100
  
  # Callbacks
  on_launch:
    - { type: play_sound, sound: ENTITY_BLAZE_SHOOT }
  
  on_tick:
    - type: spawn_particle_shape
      center: LOCATION
      particle: FLAME
      shape: POINT
  
  on_hit:
    - { type: play_sound, sound: ENTITY_GENERIC_EXPLODE }
    - type: apply_effect
      target:
        selector: nearby
        location: LOCATION
        radius: 4
      effect: SLOWNESS
      duration: 60
  
  on_finish:
    - { type: broadcast, message: "Proyectil terminado" }
```

### Tipos de Proyectil

- `ARROW`
- `SPECTRAL_ARROW`
- `SNOWBALL`
- `FIREBALL`
- `SMALL_FIREBALL`
- `DRAGON_FIREBALL`
- `WITHER_SKULL`
- `EGG`
- `ENDER_PEARL`
- `EXPERIENCE_BOTTLE`
- `TRIDENT`
- `WIND_CHARGE`

### Acceso a Variables en Callbacks

En los callbacks (`on_hit`, `on_tick`, etc.) tienes acceso a:

```yaml
LOCATION           # Ubicación actual del proyectil
SUBJECT            # El shooter original
EVENT.args.*       # Argumentos pasados con "args:"
```

---

## Items Personalizados

### Estructura en items.yml

```yaml
items:
  MI_ITEM:
    enabled: true                    # Opcional, default true
    name: "<gold>Mi Item Épico"      # MiniMessage
    base_material: DIAMOND_SWORD
    custom_model_data: 710001
    max_stack_size: 1                # Opcional
    max_damage: 500                  # Durabilidad custom
    leather_color: "#FF0000"         # Solo para armadura de cuero
    
    # Lore con placeholders
    lore:
      - "<gray>Un item poderoso"
      - ""
      - "<blue>Daño: +%pdc_double:damage%"
      - "<blue>Velocidad: +%pdc_double:speed%"
    
    # Datos persistentes
    pdc:
      - { key: damage, type: DOUBLE, value: 5.0 }
      - { key: speed, type: DOUBLE, value: 1.2 }
      - { key: custom_flag, type: STRING, value: "enabled" }
    
    # Encantamientos
    enchantments:
      - { enchantment: SHARPNESS, level: 5 }
      - { enchantment: UNBREAKING, level: 3 }
    # O formato alternativo:
    enchantments:
      SHARPNESS: 5
      UNBREAKING: 3
    
    # Atributos
    attributes:
      - attribute: ATTACK_DAMAGE
        amount: 10.0
        operation: ADD_NUMBER
        slot: HAND
      - attribute: ATTACK_SPEED
        amount: 1.6
        operation: ADD_NUMBER
        slot: HAND
    
    # Eventos
    events:
      player_interact:
        require_all:
          - type: var_in
            key: EVENT.action
            values: ["RIGHT_CLICK_AIR", "RIGHT_CLICK_BLOCK"]
          - { type: player_has_cooldown, material: DIAMOND_SWORD, value: false }
        on_pass:
          - { type: set_player_cooldown, material: DIAMOND_SWORD, ticks: 20 }
          - { type: damage_item_durability, slot_key: EVENT.hand, amount: 1 }
          - { type: play_sound, sound: ENTITY_ENDER_DRAGON_FLAP }
        on_fail: []
        deny_on_fail: false
      
      entity_damage_by_entity:
        require_all: []
        on_pass:
          - type: read_item_pdc_to_var
            key: damage
            data_type: DOUBLE
            var: EVENT.custom.bonus_damage
          - type: math_set_var
            key: EVENT.custom.new_damage
            op: add
            a_key: EVENT.damage
            b_key: EVENT.custom.bonus_damage
          - type: set_event_damage
            value_key: EVENT.custom.new_damage
```

### Tipos de PDC

| Tipo | Descripción |
|------|-------------|
| `DOUBLE` | Número decimal |
| `INTEGER` | Número entero |
| `STRING` | Texto |
| `BOOLEAN` | true/false |

### Eventos de Items

| Evento | Descripción |
|--------|-------------|
| `player_interact` | Click derecho/izquierdo |
| `entity_damage_by_entity` | Al golpear entidad |
| `entity_damage` | Al recibir daño |
| `block_break` | Al romper bloque |
| `player_item_consume` | Al consumir item |
| `projectile_hit` | Al impactar proyectil |

---

## Modificadores

### Estructura en modifiers.yml

```yaml
modifiers:
  MI_MODIFICADOR:
    enabled: true
    display_name: "<red>Modificador Épico"
    description: "Cambia las reglas del juego"
    icon: NETHER_STAR
    
    events:
      # Eventos globales (no requieren item específico)
      entity_damage:
        require_all:
          - { type: random_chance, chance: 0.1 }
        on_pass:
          - { type: multiply_event_damage, value: 2.0 }
      
      player_death:
        on_pass:
          - { type: broadcast, message: "<player> ha muerto!" }
      
      creature_spawn:
        require_all:
          - { type: random_chance, chance: 0.05 }
        on_pass:
          - { type: apply_effect, target: EVENT.entity, effect: SPEED, duration: 999999, amplifier: 1 }
```

### Eventos de Modificadores

| Evento | Descripción |
|--------|-------------|
| `entity_damage` | Cualquier daño |
| `entity_damage_by_entity` | Daño entre entidades |
| `player_death` | Muerte de jugador |
| `player_respawn` | Respawn de jugador |
| `creature_spawn` | Spawn de criatura |
| `player_interact` | Interacción de jugador |
| `block_break` | Rotura de bloque |
| `player_move` | Movimiento (¡cuidado con performance!) |
| `projectile_hit` | Impacto de proyectil |
| `player_chat` | Mensaje de chat |
| `prepare_anvil` | Preparar yunque |
| `prepare_item_enchant` | Preparar encantamiento |
| Y muchos más... |

---

## Mobs Personalizados

### Estructura en mobs.yml

```yaml
mobs:
  MI_MOB:
    enabled: true
    entity_type: ZOMBIE
    display_name: "<red>Zombie Épico"
    
    # Atributos
    attributes:
      MAX_HEALTH: 100.0
      ATTACK_DAMAGE: 15.0
      MOVEMENT_SPEED: 0.3
      KNOCKBACK_RESISTANCE: 0.5
      ARMOR: 10.0
    
    # Equipamiento
    equipment:
      helmet: NETHERITE_HELMET
      chestplate: NETHERITE_CHESTPLATE
      leggings: NETHERITE_LEGGINGS
      boots: NETHERITE_BOOTS
      main_hand: NETHERITE_SWORD
      off_hand: SHIELD
    
    # Drops
    drops:
      - item: DIAMOND
        amount: 1-3
        chance: 0.5
      - item: MI_ITEM_CUSTOM
        amount: 1
        chance: 0.1
    
    # Eventos
    events:
      mob_spawn:
        on_pass:
          - { type: apply_effect, target: SUBJECT, effect: FIRE_RESISTANCE, duration: 999999 }
      
      entity_damage:
        require_all:
          - { type: var_compare, key: EVENT.finalDamage, op: ">=", value: 20 }
        on_pass:
          - { type: play_sound, sound: ENTITY_ENDER_DRAGON_GROWL }
```

### Atributos de Mobs

| Atributo | Descripción |
|----------|-------------|
| `MAX_HEALTH` | Vida máxima |
| `ATTACK_DAMAGE` | Daño de ataque |
| `MOVEMENT_SPEED` | Velocidad de movimiento |
| `KNOCKBACK_RESISTANCE` | Resistencia al retroceso |
| `ARMOR` | Armadura |
| `ARMOR_TOUGHNESS` | Dureza de armadura |
| `FOLLOW_RANGE` | Rango de seguimiento |

---

## Recetas

### Estructura en recipes.yml

```yaml
recipes:
  # Receta con forma
  mi_receta_shaped:
    type: shaped
    result: MI_ITEM_CUSTOM
    amount: 1
    pattern:
      - "DDD"
      - "DED"
      - "DDD"
    ingredients:
      D: DIAMOND
      E: EMERALD
  
  # Receta sin forma
  mi_receta_shapeless:
    type: shapeless
    result: OTRO_ITEM
    amount: 1
    ingredients:
      - DIAMOND
      - DIAMOND
      - EMERALD
  
  # Receta de fundición
  mi_receta_furnace:
    type: furnace
    result: IRON_INGOT
    input: RAW_IRON
    experience: 0.7
    cooking_time: 200
  
  # Receta de ahumador
  mi_receta_smoker:
    type: smoker
    result: COOKED_BEEF
    input: BEEF
  
  # Receta de alto horno
  mi_receta_blast:
    type: blasting
    result: IRON_INGOT
    input: RAW_IRON
  
  # Receta de fogata
  mi_receta_campfire:
    type: campfire
    result: COOKED_SALMON
    input: SALMON
  
  # Receta de cantero
  mi_receta_stonecutting:
    type: stonecutting
    result: STONE_BRICKS
    input: STONE
    amount: 1
  
  # Receta de herrería
  mi_receta_smithing:
    type: smithing_transform
    result: NETHERITE_SWORD
    template: NETHERITE_UPGRADE_SMITHING_TEMPLATE
    base: DIAMOND_SWORD
    addition: NETHERITE_INGOT
```

---

## Script Library

### Estructura en scripts.yml

```yaml
script_library:
  actions:
    # Bloque de acciones reutilizable
    mi_macro:
      - { type: play_sound, sound: ENTITY_EXPERIENCE_ORB_PICKUP }
      - { type: message, message: "<green>¡Acción ejecutada!" }
    
    # Macro con parámetros
    curar_jugador:
      - type: heal
        target: SUBJECT
        amount_key: EVENT.args.cantidad
      - type: message
        message_key: EVENT.args.mensaje
    
    # Macro compleja
    explosion_magica:
      - type: spawn_particle_shape
        center: LOCATION
        particle: EXPLOSION_LARGE
        shape: SPHERE
        radius: 3
      - type: play_sound
        sound: ENTITY_GENERIC_EXPLODE
      - type: gravity_pull_near_location
        location: LOCATION
        radius: 5
        strength: 0.5
  
  conditions:
    # Condición reutilizable
    es_de_noche:
      type: time_between
      from: 13000
      to: 23000
    
    tiene_permiso_vip:
      type: has_permission
      permission: "server.vip"
```

### Usar la Librería

```yaml
# Llamar a acciones
- type: call
  ref: mi_macro

# Llamar con parámetros
- type: call
  ref: curar_jugador
  with:
    cantidad: 10
    mensaje: "<green>¡Te has curado!"

# Usar condición de librería
require_all:
  - type: call
    ref: es_de_noche
```

---

## Ejemplos Completos

### Staff de Hielo

```yaml
ICE_STAFF:
  name: "<aqua>Bastón de Hielo"
  base_material: DIAMOND_HOE
  max_damage: 120
  pdc:
    - { key: staff_damage, type: DOUBLE, value: 2.0 }
    - { key: staff_speed, type: DOUBLE, value: 7.5 }
    - { key: staff_freeze_radius, type: DOUBLE, value: 3.0 }
  lore:
    - "<gray>Congela a tus enemigos"
    - ""
    - "<blue> +%pdc_double:staff_damage% Daño"
    - "<blue> +%pdc_double:staff_speed% Velocidad"
    - "<blue> +%pdc_double:staff_freeze_radius% Radio"
  events:
    player_interact:
      require_all:
        - type: var_in
          key: EVENT.action
          values: ["RIGHT_CLICK_AIR", "RIGHT_CLICK_BLOCK"]
        - { type: player_has_cooldown, material: DIAMOND_HOE, value: false }
      on_pass:
        - { type: set_player_cooldown, material: DIAMOND_HOE, ticks: 40 }
        - { type: damage_item_durability, slot_key: EVENT.hand, amount: 1 }
        
        # Leer stats del item
        - type: read_item_pdc_to_var
          key: staff_damage
          data_type: DOUBLE
          var: EVENT.custom.staff_damage
        - type: read_item_pdc_to_var
          key: staff_speed
          data_type: DOUBLE
          var: EVENT.custom.staff_speed
        - type: read_item_pdc_to_var
          key: staff_freeze_radius
          data_type: DOUBLE
          var: EVENT.custom.staff_freeze_radius
        
        # Lanzar proyectil
        - type: launch_curved_projectile
          projectile: SNOWBALL
          shooter_key: SUBJECT
          speed_key: EVENT.custom.staff_speed
          curve_height: 1.5
          args:
            freeze_radius: EVENT.custom.staff_freeze_radius
          on_launch:
            - { type: play_sound, sound: ENTITY_SNOW_GOLEM_SHOOT }
          on_tick:
            - type: spawn_particle_shape
              center: LOCATION
              particle: SNOWFLAKE
              shape: HELIX
              points: 12
              radius: 0.4
          explode_on_impact: true
          explosion_power_key: EVENT.custom.staff_damage
          on_hit:
            - { type: play_sound, sound: BLOCK_GLASS_BREAK }
            - type: freeze_area
              where: LOCATION
              radius_key: EVENT.args.freeze_radius
              air_chance: 0.5
            - type: apply_effect
              target:
                selector: nearby
                location: LOCATION
                radius: 4
              effect: SLOWNESS
              duration: 60
              amplifier: 2
```

### Modificador de Luna Sangrienta

```yaml
BLOOD_MOON:
  enabled: true
  display_name: "<dark_red>Luna Sangrienta"
  description: "Los mobs son más fuertes y agresivos"
  icon: RED_STAINED_GLASS
  
  events:
    creature_spawn:
      require_all:
        - { type: environment, value: NORMAL }
        - { type: time_between, from: 13000, to: 23000 }
      on_pass:
        # +50% vida
        - type: math_set_var
          key: EVENT.custom.new_health
          op: mul
          a_key: EVENT.entity.health
          b: 1.5
        - type: set_attribute
          target: EVENT.entity
          attribute: MAX_HEALTH
          value_key: EVENT.custom.new_health
        - type: heal
          target: EVENT.entity
          amount: 1000
        
        # Efectos
        - type: apply_effect
          target: EVENT.entity
          effect: SPEED
          duration: 999999
          amplifier: 0
        - type: apply_effect
          target: EVENT.entity
          effect: STRENGTH
          duration: 999999
          amplifier: 0
    
    player_death:
      on_pass:
        - type: broadcast
          message: "<dark_red>☠ <player> ha caído ante la Luna Sangrienta"
```

---

## Tips y Buenas Prácticas

1. **Usa `slot_key: EVENT.hand`** en lugar de `slot: MAIN_HAND` para items que pueden usarse en ambas manos.

2. **Prefiere `data_type` sobre `type`** en `read_item_pdc_to_var` para evitar conflictos.

3. **Usa `EVENT.args.*`** para pasar datos a callbacks de proyectiles.

4. **Guarda valores en `EVENT.custom.*`** para cálculos intermedios.

5. **Usa la Script Library** para código reutilizable.

6. **Cuidado con eventos frecuentes** como `player_move` - pueden afectar el rendimiento.

7. **Prueba con `/rd admin item give`** para obtener items con stats modificados:
   ```
   /rd admin item give <player> <item_id> <amount> {"stat_key": value}
   ```

8. **Usa `random_chance`** para efectos probabilísticos en lugar de lógica compleja.
