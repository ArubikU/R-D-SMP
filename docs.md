# Documentación del Sistema de Scripting

Sistema de scripting avanzado para Minecraft Bukkit/Spigot que permite crear items, mobs y modificadores personalizados mediante YAML.

---

## Tabla de Contenidos

1. [Conceptos Básicos](#conceptos-básicos)
2. [Actions (Acciones)](#actions-acciones)
3. [Conditions (Condiciones)](#conditions-condiciones)
4. [Math (Matemáticas)](#math-matemáticas)
5. [Particles (Partículas)](#particles-partículas)
6. [Variables y Scope](#variables-y-scope)
7. [Ejemplos Completos](#ejemplos-completos)

---

## Conceptos Básicos

### Estructura General

```yaml
items:
  MI_ITEM:
    type: MI_ITEM
    name: "Nombre del Item"
    base_material: MATERIAL
    events:
      player_interact:
        require_all:
          - type: condition1
          - type: condition2
        on_pass:
          - type: action1
          - type: action2
        on_fail:
          - type: action3
        deny_on_fail: false
```

### Variables de Contexto

Todas las acciones y condiciones tienen acceso a estas variables del contexto:

- **EVENT**: Datos del evento actual
  - `EVENT.action`: Acción del evento (RIGHT_CLICK_AIR, LEFT_CLICK_BLOCK, etc.)
  - `EVENT.block`: Bloque del evento (si aplica)
  - `EVENT.item`: Item del evento (si aplica)
  - `EVENT.damage`, `EVENT.finalDamage`: Daño (en eventos de daño)
  - `EVENT.location`, `EVENT.from`, `EVENT.to`: Ubicaciones
  - `EVENT.native`: Evento Bukkit original (acceso reflectivo completo)
  - `EVENT.custom`: Datos custom definidos por el usuario
  - `EVENT.args`: Argumentos pasados vía `with:`
- **SUBJECT**: Entidad que ejecuta la acción (jugador/mob)
- **TARGET**: Entidad objetivo (en eventos de daño, etc.)
- **PLAYER**: El jugador involucrado
- **WORLD**: Mundo actual
- **ITEM**: ItemStack del evento
- **PROJECTILE**: Proyectil (en eventos de proyectiles)

---

## Actions (Acciones)

### Control de Flujo

#### `call`
Llama un macro/script definido en `scripts.yml`.
```yaml
- type: call
  ref: nombre_del_macro
  with:
    arg1: valor1
    arg2: valor2
  allow_missing: false  # opcional
```

#### `run_later`
Ejecuta acciones después de un delay.
```yaml
- type: run_later
  delay_ticks: 20
  actions:
    - type: message
      message: "Han pasado 20 ticks!"
```

#### `run_repeating`
Ejecuta acciones repetidamente.
```yaml
- type: run_repeating
  interval_ticks: 1
  total_ticks: 200
  actions:
    - type: message
      message: "Tick ${EVENT.repeatAgeTicks}"
```

**Nota**: Preserva `EVENT.custom` del contexto original en cada iteración.

---

### Mensajes y Comunicación

#### `message`
Envía un mensaje al jugador.
```yaml
- type: message
  message: "Texto del mensaje"
  color: "green"  # red, yellow, aqua, gold, etc.
```

#### `message_key`
Mensaje desde variable.
```yaml
- type: message
  message_key: "EVENT.args.mensaje"
  color: "yellow"
```

#### `broadcast`
Mensaje a todos los jugadores.
```yaml
- type: broadcast
  message: "Anuncio global"
  color: "gold"
```

#### `broadcast_with_location`
Broadcast con coordenadas.
```yaml
- type: broadcast_with_location
  message_key: "EVENT.custom.mensaje"
  location_key: "EVENT.custom.ubicacion"
  color: "aqua"
```

#### `deny`
Niega una acción con mensaje.
```yaml
- type: deny
  message: "No puedes hacer eso"
  color: "red"
```

---

### Vidas y Jugadores

#### `add_lives`
```yaml
- type: add_lives
  amount: 1
  message: "¡+1 vida!"
  color: "green"
```

#### `take_lives`
```yaml
- type: take_lives
  amount: 2
  message: "Perdiste 2 vidas"
  color: "red"
```

#### `set_lives`
```yaml
- type: set_lives
  value: 5
  message: "Ahora tienes 5 vidas"
```

#### `kill_player`
```yaml
- type: kill_player
```

#### `damage_player`
```yaml
- type: damage_player
  amount: 4.0
```

#### `damage_player_or_kill`
```yaml
- type: damage_player_or_kill
  damage: 6.0
  kill_if_health_at_most: 2.0
```

#### `heal_player`
```yaml
- type: heal_player
  amount: 4.0
# O desde variable:
- type: heal_player
  amount_key: "EVENT.args.curacion"
```

#### `set_player_health`
```yaml
- type: set_player_health
  value: 20.0
```

#### `add_player_food`
```yaml
- type: add_player_food
  food: 4.0
  saturation: 2.0
```

---

### Efectos de Pociones

#### `apply_effect`
```yaml
- type: apply_effect
  effect: SPEED
  duration: 200
  amplifier: 1
  ambient: false
  particles: true
```

**Efectos disponibles**: SPEED, SLOWNESS, HASTE, MINING_FATIGUE, STRENGTH, INSTANT_HEALTH, INSTANT_DAMAGE, JUMP_BOOST, NAUSEA, REGENERATION, RESISTANCE, FIRE_RESISTANCE, WATER_BREATHING, INVISIBILITY, BLINDNESS, NIGHT_VISION, HUNGER, WEAKNESS, POISON, WITHER, HEALTH_BOOST, ABSORPTION, SATURATION, GLOWING, LEVITATION, LUCK, UNLUCK, SLOW_FALLING, CONDUIT_POWER, DOLPHINS_GRACE

#### `apply_random_effect`
```yaml
- type: apply_random_effect
  effects:
    - SPEED
    - STRENGTH
    - REGENERATION
    - POISON
  duration: 600
  amplifier: 1
  ambient: false
  particles: true
```

#### `remove_effect`
```yaml
- type: remove_effect
  effect: POISON
  only_if_amplifier: 2  # opcional
```

#### `apply_effect_to_mob`
```yaml
- type: apply_effect_to_mob
  effect: SLOWNESS
  duration: 200
  amplifier: 2
```

#### `apply_effect_near_location`
```yaml
- type: apply_effect_near_location
  where: SUBJECT
  effect: REGENERATION
  duration: 200
  amplifier: 1
  radius: 10.0
  include_players: true
  include_mobs: false
```

---

### Variables

#### `set_var`
```yaml
- type: set_var
  key: "EVENT.custom.contador"
  value: 10
```

#### `copy_var`
```yaml
- type: copy_var
  from: "PLAYER.health"
  to: "EVENT.custom.vida_guardada"
  default: 20.0
```

#### `add_var`
```yaml
- type: add_var
  key: "EVENT.custom.puntos"
  value: 100
```

#### `set_var_now_plus`
Guarda timestamp futuro.
```yaml
- type: set_var_now_plus
  key: "PLAYER.custom.next_cooldown_ms"
  min_ms: 5000
  max_ms: 5000
```

#### `read_item_pdc_to_var`
Lee PersistentDataContainer del item.
```yaml
- type: read_item_pdc_to_var
  key: "mi_dato"
  store_key: "EVENT.custom.dato_leido"
  type: "DOUBLE"  # DOUBLE, INT, LONG, STRING, BYTE
  default: 0.0
```

#### `set_var_target_block_location`
Raycast para obtener bloque objetivo.
```yaml
- type: set_var_target_block_location
  store_key: "EVENT.custom.void_center"
  range: 60
  fallback_distance: 10
  y_offset: 0.5
  center_block: true
```

---

### Matemáticas

#### `math_set_var`
Operaciones matemáticas.
```yaml
- type: math_set_var
  key: "EVENT.custom.resultado"
  op: "mul"  # set, add, sub, mul, div, min, max, abs, floor, ceil, etc.
  a_key: "EVENT.finalDamage"
  b: 0.3
```

**Operadores disponibles**: `set`, `add`, `sub`, `mul`, `div`, `min`, `max`, `abs`, `neg`, `floor`, `ceil`, `round`, `pow`, `mod`, `sqrt`, `cbrt`, `ln`, `exp`, `sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `sin_deg`, `cos_deg`, `tan_deg`, `deg_to_rad`, `rad_to_deg`, `rand`, `rand_range`, `clamp`, `lerp`

---

### Items y Inventario

#### `consume_event_item`
```yaml
- type: consume_event_item
  amount: 1
```

#### `consume_extra_hand_item`
```yaml
- type: consume_extra_hand_item
  amount: 1
```

#### `give_item`
```yaml
- type: give_item
  item_type: "MI_ITEM_CUSTOM"
  amount: 1
```

#### `damage_item_durability`
```yaml
- type: damage_item_durability
  slot: MAIN_HAND  # MAIN_HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET
  amount: 10
```

#### `unequip_item`
```yaml
- type: unequip_item
  slot: HEAD
  drop: true
```

#### `ensure_player_has_item`
```yaml
- type: ensure_player_has_item
  item_type: "MI_ITEM"
  amount: 1
```

#### `set_player_inventory_hide_tooltip`
```yaml
- type: set_player_inventory_hide_tooltip
  hide: true
```

---

### Movimiento y Física

#### `add_velocity`
```yaml
- type: add_velocity
  x: 0.0
  y: 0.5
  z: 0.0
```

#### `add_velocity_random`
```yaml
- type: add_velocity_random
  min_x: -0.5
  max_x: 0.5
  min_y: 0.2
  max_y: 0.8
  min_z: -0.5
  max_z: 0.5
```

#### `set_player_velocity_forward`
```yaml
- type: set_player_velocity_forward
  multiplier: 2.0
  y: 0.2
```

#### `gravity_pull`
Atrae entidades hacia un punto.
```yaml
- type: gravity_pull
  center: EVENT.custom.ubicacion
  radius: 10.0
  strength: 0.3
  scale_by_distance: true
  max_force: 1.0
  include_players: true
  include_mobs: true
  exclude_subject: false
  exclude_spectators: true
  at_target:  # Acciones en cada entidad atraída
    - type: spawn_particle_shape
      center: SUBJECT
      particle: SOUL
      count: 2
```

---

### Daño y Combate

#### `set_event_damage`
```yaml
- type: set_event_damage
  damage: 10.0
```

#### `multiply_event_damage`
```yaml
- type: multiply_event_damage
  multiplier: 1.5
```

#### `damage_nearby_entities`
```yaml
- type: damage_nearby_entities
  radius: 5.0
  amount: 4.0        # daño fijo
  # O:
  multiplier: 0.5    # multiplicador del daño original
  exclude_subject: true
  exclude_target: true
```

#### `reflect_damage_if_blocking`
```yaml
- type: reflect_damage_if_blocking
  multiplier: 0.75
  reset_no_damage_ticks: true
```

#### `explode`
```yaml
- type: explode
  where: EVENT.custom.ubicacion
  power: 4.0
  set_fire: false
  break_blocks: true
```

#### `multiply_explosion_radius`
```yaml
- type: multiply_explosion_radius
  multiplier: 2.0
```

---

### Entidades y Spawning

#### `spawn_entity_at_player`
```yaml
- type: spawn_entity_at_player
  entity_type: ZOMBIE
  count: 3
  radius: 5
  y_offset: 0
```

#### `spawn_entity_near_player`
```yaml
- type: spawn_entity_near_player
  entity_type: ZOMBIE
  y_offset: 10
  radius: 5
  max_y: 320
  max_per_chunk: 6
  cap_entity_type: ZOMBIE
  require_storm: false
```

#### `spawn_mob_at_key`
Spawns un mob custom del sistema.
```yaml
- type: spawn_mob_at_key
  where: EVENT.custom.ubicacion
  mob_type: ALPHA_DRAGON
  count: 1
  radius: 0
  y_offset: 2
  store_key: "EVENT.custom.spawned_mob"
```

#### `set_mob_attribute_base`
```yaml
- type: set_mob_attribute_base
  attribute: MAX_HEALTH
  value: 40.0
```

#### `set_mob_max_health`
```yaml
- type: set_mob_max_health
  value: 60.0
  heal: true
```

#### `set_mob_target_nearest_player`
```yaml
- type: set_mob_target_nearest_player
  radius: 32.0
```

#### `aggro_nearby_mobs`
```yaml
- type: aggro_nearby_mobs
  radius: 16.0
```

---

### Sonidos

#### `play_sound`
```yaml
- type: play_sound
  sound: ENTITY_PLAYER_LEVELUP
  volume: 1.0
  pitch: 1.0
# O aleatorio:
- type: play_sound
  sounds:
    - UI_BUTTON_CLICK
    - BLOCK_NOTE_BLOCK_PLING
  volume: 1.0
  pitch: 1.2
```

#### `stop_all_sounds`
```yaml
- type: stop_all_sounds
```

---

### Partículas (Básico)

#### `spawn_particle_shape`
Partículas instantáneas.
```yaml
- type: spawn_particle_shape
  center: SUBJECT
  particle: HEART
  shape: SPHERE
  points: 12
  radius: 1.0
  y_offset: 1.0
  count: 1
  offset_x: 0.0
  offset_y: 0.0
  offset_z: 0.0
```

**Shapes disponibles**: SPHERE, CIRCLE, LINE, CUBE, FORMULA

Ver sección [Particles](#particles-partículas) para detalles.

#### `start_particle_system`
Sistema de partículas animado (ver [Particles](#particles-partículas)).

#### `stop_particle_system`
Detiene un sistema de partículas por ID.
```yaml
- type: stop_particle_system
  system_id: "mi_sistema"
```

---

### Proyectiles

#### `projectile_add_random_spread`
```yaml
- type: projectile_add_random_spread
  spread: 0.2
```

#### `strike_lightning`
```yaml
- type: strike_lightning
  where: TARGET
```

#### `launch_curved_projectile`
Proyectil con trayectoria curva.
```yaml
- type: launch_curved_projectile
  projectile_type: ARROW
  initial_velocity: 1.5
  gravity: -0.03
  drag: 0.99
  lifetime_ticks: 200
  trail_particle: FLAME
  trail_period_ticks: 2
  on_hit_block:
    - type: explode
      where: EVENT.hitLocation
      power: 2.0
```

---

### Atributos

#### `stack_player_attribute_modifier`
```yaml
- type: stack_player_attribute_modifier
  attribute: MAX_HEALTH
  key: "notch_heart"
  amount: 2.0
  operation: ADD_NUMBER  # ADD_NUMBER, ADD_SCALAR, MULTIPLY_SCALAR_1
```

#### `add_player_attribute_modifier`
```yaml
- type: add_player_attribute_modifier
  attribute: MOVEMENT_SPEED
  key: "speed_boost"
  amount: 0.1
  operation: ADD_SCALAR
```

#### `remove_player_attribute_modifier`
```yaml
- type: remove_player_attribute_modifier
  attribute: MOVEMENT_SPEED
  key: "speed_boost"
```

---

### Mundo y Bloques

#### `set_block_type_at`
```yaml
- type: set_block_type_at
  where: EVENT.custom.ubicacion
  material: DIAMOND_BLOCK
```

#### `set_block_below_type`
```yaml
- type: set_block_below_type
  material: GOLD_BLOCK
```

#### `set_world_time`
```yaml
- type: set_world_time
  time: 0  # 0=amanecer, 6000=mediodía, 12000=atardecer, 18000=medianoche
```

#### `set_gamerule_all_worlds`
```yaml
- type: set_gamerule_all_worlds
  rule: "doMobSpawning"
  value: false
```

#### `set_weather_all_worlds`
```yaml
- type: set_weather_all_worlds
  storm: true
  thunder: true
  duration_ticks: 6000
```

---

### Eventos

#### `cancel_event`
```yaml
- type: cancel_event
  value: true
```

#### `set_event_use_interacted_block`
```yaml
- type: set_event_use_interacted_block
  value: DENY  # ALLOW, DENY, DEFAULT
```

#### `set_event_use_item_in_hand`
```yaml
- type: set_event_use_item_in_hand
  value: ALLOW
```

---

### Comandos y Discord

#### `command`
```yaml
- type: command
  command: "give ${PLAYER.name} diamond 1"
  as: console  # console o player
```

#### `discord_webhook`
```yaml
- type: discord_webhook
  url: "https://discord.com/api/webhooks/..."
  content: "Mensaje al Discord"
  username: "Bot Name"
  avatar_url: "https://..."
```

---

### Misceláneos

#### `set_player_cooldown`
```yaml
- type: set_player_cooldown
  material: ENDER_PEARL
  ticks: 200
```

#### `set_fire_ticks`
```yaml
- type: set_fire_ticks
  ticks: 100
```

#### `set_compass_target_spawn`
```yaml
- type: set_compass_target_spawn
```

#### `teleport_player_to_key`
```yaml
- type: teleport_player_to_key
  location_key: "EVENT.custom.destino"
```

#### `pull_nearby_items`
```yaml
- type: pull_nearby_items
  radius: 5.0
  speed: 0.5
  ignore_pickup_delay: true
```

#### `for_each_online_player`
```yaml
- type: for_each_online_player
  actions:
    - type: message
      message: "Hola!"
```

#### `assign_random_role`
```yaml
- type: assign_random_role
  avoid_current: true
```

#### `give_daily_roll_reward`
```yaml
- type: give_daily_roll_reward
  luck_key: "pdc_luck"
  default_luck: 0.0
  message: "¡Recompensa obtenida!"
```

---

## Conditions (Condiciones)

### Lógica

#### `all_of`
```yaml
require_all:
  - type: all_of
    conditions:
      - type: player_in_water
      - type: world_has_storm
```

#### `any_of`
```yaml
require_all:
  - type: any_of
    conditions:
      - type: role_is
        value: WARRIOR
      - type: role_is
        value: MAGE
```

#### `not`
```yaml
require_all:
  - type: not
    condition:
      type: player_inventory_full
```

---

### Variables

#### `var_truthy`
```yaml
require_all:
  - type: var_truthy
    key: "EVENT.args.enabled"
    invert: false
```

#### `var_is_missing`
```yaml
require_all:
  - type: var_is_missing
    key: "PLAYER.custom.dato"
```

#### `var_equals`
```yaml
require_all:
  - type: var_equals
    key: "EVENT.action"
    value: "RIGHT_CLICK_AIR"
    case_insensitive: true
```

#### `var_in`
```yaml
require_all:
  - type: var_in
    key: "EVENT.action"
    values:
      - "RIGHT_CLICK_AIR"
      - "RIGHT_CLICK_BLOCK"
```

#### `var_compare`
```yaml
require_all:
  - type: var_compare
    key: "SUBJECT.health"
    operator: "<"  # <, >, <=, >=, ==, !=, contains
    other_key: "SUBJECT.maxHealth"
```

#### `var_matches_regex`
```yaml
require_all:
  - type: var_matches_regex
    key: "PLAYER.name"
    pattern: "^[A-Z].*"
    case_insensitive: false
```

---

### Jugador

#### `player_game_mode_in`
```yaml
require_all:
  - type: player_game_mode_in
    values:
      - SURVIVAL
      - ADVENTURE
```

#### `player_in_water`
```yaml
require_all:
  - type: player_in_water
    value: true
```

#### `player_has_cooldown`
```yaml
require_all:
  - type: player_has_cooldown
    material: ENDER_PEARL
    value: false
```

#### `player_inventory_full`
```yaml
require_all:
  - type: player_inventory_full
    value: false
```

#### `player_sky_light_at_least`
```yaml
require_all:
  - type: player_sky_light_at_least
    value: 15
```

#### `player_has_permission`
```yaml
require_all:
  - type: player_has_permission
    permission: "miplugin.admin"
```

---

### Mundo y Tiempo

#### `world_time_between`
```yaml
require_all:
  - type: world_time_between
    min: 13000
    max: 23000
    inclusive: true
```

#### `world_has_storm`
```yaml
require_all:
  - type: world_has_storm
    value: true
```

#### `world_is_thundering`
```yaml
require_all:
  - type: world_is_thundering
    value: true
```

#### `world_environment_is`
```yaml
require_all:
  - type: world_environment_is
    value: NETHER  # NORMAL, NETHER, THE_END
```

---

### Sistema

#### `random_chance`
```yaml
require_all:
  - type: random_chance
    probability: 0.5
# O:
  - type: random_chance
    numerator: 1
    denominator: 4
```

#### `min_day`
```yaml
require_all:
  - type: min_day
    value: 7
```

#### `lives_at_least`
```yaml
require_all:
  - type: lives_at_least
    value: 3
```

#### `role_is`
```yaml
require_all:
  - type: role_is
    value: WARRIOR
```

#### `modifier_active`
```yaml
require_all:
  - type: modifier_active
    name: "double_drops"
```

#### `now_ms_gte_var`
Verifica si el timestamp actual >= variable.
```yaml
require_all:
  - type: now_ms_gte_var
    key: "PLAYER.custom.next_cooldown_ms"
```

---

### Material

#### `material_in_tag`
```yaml
require_all:
  - type: material_in_tag
    key: "EVENT.block.type"
    tag: "LOGS"  # LOGS, LOGS_THAT_BURN, PLANKS, LEAVES
```

#### `material_is_ore`
```yaml
require_all:
  - type: material_is_ore
    key: "EVENT.block.type"
    value: true
```

---

### Placeholder

#### `placeholder_compare`
```yaml
require_all:
  - type: placeholder_compare
    placeholder: "%player_level%"
    operator: ">="
    value: "10"
    case_insensitive: true
```

---

### Call

#### `call`
Llama condición desde scripts.yml.
```yaml
require_all:
  - type: call
    ref: "is_night"
```

---

## Math (Matemáticas)

### Operaciones con `math_set_var`

```yaml
- type: math_set_var
  key: "RESULT"
  op: "operacion"
  a_key: "VAR1"  # o a: valor
  b_key: "VAR2"  # o b: valor
  c_key: "VAR3"  # opcional (solo clamp, lerp)
```

### Operadores Disponibles

| Operador | Descripción | Parámetros |
|----------|-------------|------------|
| `set`, `=` | Asigna valor | a |
| `add`, `+` | Suma | a, b |
| `sub`, `-` | Resta | a, b |
| `mul`, `*` | Multiplicación | a, b |
| `div`, `/` | División | a, b |
| `mod`, `%` | Módulo | a, b |
| `min` | Mínimo | a, b |
| `max` | Máximo | a, b |
| `abs` | Valor absoluto | a |
| `neg` | Negación | a |
| `floor` | Redondeo hacia abajo | a |
| `ceil` | Redondeo hacia arriba | a |
| `round` | Redondeo | a |
| `pow` | Potencia | a, b |
| `sqrt` | Raíz cuadrada | a |
| `cbrt` | Raíz cúbica | a |
| `ln` | Logaritmo natural | a |
| `exp` | Exponencial | a |
| `sin` | Seno (radianes) | a |
| `cos` | Coseno (radianes) | a |
| `tan` | Tangente (radianes) | a |
| `asin` | Arcoseno | a |
| `acos` | Arcocoseno | a |
| `atan` | Arcotangente | a |
| `sin_deg` | Seno (grados) | a |
| `cos_deg` | Coseno (grados) | a |
| `tan_deg` | Tangente (grados) | a |
| `deg_to_rad` | Grados → Radianes | a |
| `rad_to_deg` | Radianes → Grados | a |
| `rand` | Random 0 a `a` | a |
| `rand_range` | Random entre `a` y `b` | a, b |
| `clamp` | Limita valor entre min/max | a, b, c |
| `lerp` | Interpolación lineal | a, b, c |

### Ejemplos

```yaml
# Calcular 30% del daño
- type: math_set_var
  key: "EVENT.custom.vampire_heal"
  op: "mul"
  a_key: "EVENT.finalDamage"
  b: 0.3

# Random entre 5 y 10
- type: math_set_var
  key: "EVENT.custom.random_value"
  op: "rand_range"
  a: 5.0
  b: 10.0

# Clamp (limitar entre 0 y 100)
- type: math_set_var
  key: "PLAYER.custom.score"
  op: "clamp"
  a_key: "PLAYER.custom.raw_score"
  b: 0.0
  c: 100.0
```

---

## Particles (Partículas)

### Shapes (Formas)

#### SPHERE
```yaml
- type: spawn_particle_shape
  center: SUBJECT
  particle: HEART
  shape: SPHERE
  points: 20
  radius: 1.0
  y_offset: 1.0
```

#### CIRCLE
```yaml
- type: spawn_particle_shape
  center: SUBJECT
  particle: FLAME
  shape: CIRCLE
  points: 24
  radius: 2.0
  y_offset: 0.1
```

#### LINE
```yaml
- type: spawn_particle_shape
  center: SUBJECT
  particle: END_ROD
  shape: LINE
  points: 15
  length: 5.0
  direction_x: 1.0
  direction_y: 0.0
  direction_z: 0.0
```

#### CUBE
```yaml
- type: spawn_particle_shape
  center: SUBJECT
  particle: REDSTONE
  dust_color: "#FF0000"
  dust_size: 1.0
  shape: CUBE
  points: 48
  size: 2.0
  y_offset: 1.0
```

#### FORMULA
Partículas con posición calculada matemáticamente.
```yaml
- type: spawn_particle_shape
  center: SUBJECT
  particle: FLAME
  shape: FORMULA
  points: 32
  radius: 3.0
  y_offset: 1.0
  formula_x: "cos(angle) * r"
  formula_y: "sin(angle * 2) * 0.5"
  formula_z: "sin(angle) * r"
```

**Variables disponibles en fórmulas**:
- `angle`: Ángulo actual (0 a 2π)
- `r` o `radius`: Radio
- `i`: Índice del punto (0 a points-1)
- `pi`: π (3.14159...)
- `e`: número e (2.71828...)

---

### Sistemas de Partículas Animados

#### `start_particle_system`

Crea un sistema de partículas que se ejecuta durante tiempo.

```yaml
- type: start_particle_system
  center: EVENT.custom.ubicacion
  lifetime_ticks: 200
  period_ticks: 1
  particle: REDSTONE
  dust_color: "#6200EA"
  dust_size: 1.5
  shape: FORMULA
  points: 40
  radius: 5.0
  y_offset: 1.0
  formula_x: "cos(angle + (age * 0.1)) * (4.5 - (age * 0.2))"
  formula_y: "sin(angle * 3) * 0.2"
  formula_z: "sin(angle + (age * 0.1)) * (4.5 - (age * 0.2))"
```

**Parámetros**:
- `center`: Ubicación central (SUBJECT, TARGET, key de variable)
- `lifetime_ticks`: Duración total del sistema
- `period_ticks`: Cada cuántos ticks ejecutar
- `particle`: Tipo de partícula
- `shape`: SPHERE, CIRCLE, LINE, CUBE, FORMULA
- `points`: Cantidad de puntos/partículas por ejecución
- `radius`: Radio base (para shapes circulares)
- `turns`: Vueltas completas (para espirales)
- `y_offset`: Offset vertical
- `angle_offset`: Offset de ángulo inicial
- `formula_x`, `formula_y`, `formula_z`: Fórmulas matemáticas (para FORMULA shape)

**Variables disponibles en fórmulas de sistemas**:
- `age`: Edad del sistema en ticks desde su inicio
- `angle`: Ángulo del punto actual
- `r` o `radius`: Radio base
- `i`: Índice del punto
- `pi`, `e`: Constantes matemáticas
- **`rand()`**: Número aleatorio 0-1

### Funciones Matemáticas en Fórmulas

Todas las funciones del [Math](#math-matemáticas) están disponibles:

```yaml
formula_x: "cos(angle + (rand() * 6.28)) * (5.0 + (rand() * 0.5 - 0.25))"
formula_y: "(rand() * 2.0 - 1.0) * 0.3"
formula_z: "sin(angle + (rand() * 6.28)) * (5.0 + (rand() * 0.5 - 0.25))"
```

**Funciones disponibles**:
- Trigonométricas: `sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `atan2`
- Redondeo: `floor`, `ceil`, `round`, `abs`
- Potencias: `sqrt`, `pow`
- Min/Max: `min`, `max`, `clamp`
- Interpolación: `lerp`
- Conversión: `deg_to_rad`, `rad_to_deg`
- Aleatorios: `rand(max)`, `rand_range(min, max)`

### Tipos de Partículas

**Partículas comunes**:
- `FLAME`, `SMOKE`, `LARGE_SMOKE`, `CLOUD`
- `HEART`, `VILLAGER_HAPPY`, `VILLAGER_ANGRY`
- `CRIT`, `MAGIC_CRIT`, `ENCHANTMENT_TABLE`
- `PORTAL`, `END_ROD`, `DRAGON_BREATH`
- `SOUL`, `SOUL_FIRE_FLAME`, `TOTEM`
- `FIREWORKS_SPARK`, `ELECTRIC_SPARK`
- `REDSTONE` (requiere `dust_color` y `dust_size`)
- `SQUID_INK`, `GLOW`, `WAX_ON`, `WAX_OFF`

**Partículas REDSTONE (con color)**:
```yaml
particle: REDSTONE
dust_color: "#FF5500"  # Hexadecimal
dust_size: 1.5
```

---

## Variables y Scope

### Acceso a Propiedades

#### Location
```
LOCATION.x, .y, .z
LOCATION.blockX, .blockY, .blockZ
LOCATION.yaw, .pitch
LOCATION.world
```

#### Block
```
BLOCK.type, .material
BLOCK.x, .y, .z
BLOCK.world
```

#### Entity/Player
```
ENTITY.uuid
ENTITY.type
ENTITY.name, .customName
ENTITY.location, .world
ENTITY.velocity
ENTITY.isDead
```

#### Player (adicionales)
```
PLAYER.health, .maxHealth
PLAYER.foodLevel
PLAYER.level, .exp
PLAYER.gameMode
```

#### ItemStack
```
ITEM.type, .material
ITEM.amount
ITEM.isAir, .isEdible
ITEM.displayName
ITEM.customModelData
ITEM.lore
ITEM.pdc  # PersistentDataContainer
```

#### World
```
WORLD.name
WORLD.time, .fullTime
WORLD.state.isDay
```

#### Event
```
EVENT.action          # Acción (RIGHT_CLICK_AIR, etc.)
EVENT.block           # Block del evento
EVENT.block.type      # Tipo de bloque
EVENT.block.location  # Ubicación del bloque
EVENT.item            # ItemStack del evento
EVENT.damage          # Daño (eventos de daño)
EVENT.finalDamage     # Daño final
EVENT.location        # Ubicación del evento
EVENT.from, EVENT.to  # Ubicaciones desde/hacia
EVENT.native          # Evento Bukkit original
EVENT.custom.*        # Datos temporales custom
EVENT.args.*          # Argumentos de call
```

---

## Ejemplos Completos

### Item con Cooldown y Partículas

```yaml
VOID_CALL:
  type: VOID_CALL
  name: "Llamada del Vacío"
  base_material: SCULK_SHRIEKER
  lore:
    - "Crea un agujero negro temporal"
  events:
    player_interact:
      require_all:
        - type: var_in
          key: EVENT.action
          values: ["RIGHT_CLICK_AIR", "RIGHT_CLICK_BLOCK"]
      on_pass:
        - type: cancel_event
          value: true
        - type: set_var_target_block_location
          store_key: EVENT.custom.void_center
          range: 60
          fallback_distance: 10
        - type: consume_event_item
          amount: 1
        - type: play_sound
          sound: entity.warden.sonic_boom
          volume: 1.5
          pitch: 0.8
        - type: call
          ref: item_void_call_vortex
```

### Mob Custom con Efectos

```yaml
JUMPING_SPIDER:
  mob_type: JUMPING_SPIDER
  base_entity: SPIDER
  name: "<red>Araña Saltadora</red>"
  max_health: 20.0
  events:
    entity_spawn:
      on_pass:
        - type: call
          ref: jumping_spider_spawn
```

### Macro Reutilizable

```yaml
script_library:
  actions:
    item_void_call_vortex:
      - type: start_particle_system
        center: EVENT.custom.void_center
        lifetime_ticks: 200
        period_ticks: 1
        particle: REDSTONE
        dust_color: "#6200EA"
        dust_size: 1.5
        shape: FORMULA
        points: 40
        radius: 5.0
        y_offset: 1.0
        formula_x: "cos(angle + (age * 0.1)) * (4.5 - (age * 0.2))"
        formula_y: "sin(angle * 3) * 0.2"
        formula_z: "sin(angle + (age * 0.1)) * (4.5 - (age * 0.2))"
      
      - type: run_repeating
        interval_ticks: 1
        total_ticks: 200
        actions:
          - type: gravity_pull
            center: EVENT.custom.void_center
            radius: 6.0
            strength: 0.15
            scale_by_distance: true
            max_force: 0.8
            at_target:
              - type: spawn_particle_shape
                center: SUBJECT
                particle: SOUL
                count: 2
```

### Condición Compleja

```yaml
require_all:
  - type: all_of
    conditions:
      - type: var_in
        key: EVENT.action
        values: ["RIGHT_CLICK_AIR", "RIGHT_CLICK_BLOCK"]
      - type: any_of
        conditions:
          - type: world_has_storm
          - type: world_time_between
            min: 13000
            max: 23000
      - type: not
        condition:
          type: player_has_cooldown
          material: ENDER_PEARL
```

---

## Tips y Mejores Prácticas

1. **Usa macros en `scripts.yml`** para reutilizar lógica común
2. **`run_repeating` preserva `EVENT.custom`**, úsalo para pasar datos entre iteraciones
3. **`rand()` en fórmulas** crea variaciones aleatorias en partículas
4. **`gravity_pull` con `at_target`** permite feedback visual por entidad
5. **Variables con prefijo `EVENT.custom.`** son ideales para datos temporales
6. **`PLAYER.custom.`** para datos persistentes del jugador
7. **Usa `scale_by_distance: true`** para efectos más realistas de física

---

## Referencia Rápida

### Variables de Contexto
`EVENT`, `SUBJECT`, `TARGET`, `PLAYER`, `WORLD`, `ITEM`, `PROJECTILE`

### Acciones Más Usadas
`message`, `call`, `heal_player`, `apply_effect`, `spawn_particle_shape`, `start_particle_system`, `gravity_pull`, `run_repeating`

### Condiciones Más Usadas
`var_in`, `var_compare`, `random_chance`, `world_time_between`, `player_in_water`, `all_of`, `any_of`, `not`

### Math Operadores
`add`, `mul`, `min`, `max`, `rand`, `rand_range`, `sin`, `cos`, `clamp`, `lerp`

### Particle Shapes
`SPHERE`, `CIRCLE`, `LINE`, `CUBE`, `FORMULA`

---

**Versión**: 1.0.0  
**Última actualización**: 24 diciembre 2025
