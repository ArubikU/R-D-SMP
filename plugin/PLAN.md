# Plan de Desarrollo: RollAndDeath SMP Plugin

Este documento detalla la hoja de ruta para el desarrollo del plugin de Minecraft (Paper/Java) que gestionará las mecánicas del servidor RollAndDeath SMP.

## 1. Configuración del Proyecto
- [x] Inicializar proyecto Gradle (Kotlin DSL).
- [x] Configurar dependencias de Paper API (última versión estable, 1.20.x o 1.21.x).
- [x] Configurar `plugin.yml` y clase principal.
- [x] Establecer estructura de paquetes (`net.rollanddeath.smp`).

## 2. Sistemas Core (Base)

### 2.1. Sistema de Vidas y Muerte
- [x] **Contador de Vidas**: Persistencia de vidas (3 iniciales) en PDC (PersistentDataContainer).
- [x] **Lógica de Muerte**:
    - Al morir: Restar 1 vida.
    - Al llegar a 0: Gamemode SPECTATOR y kick/ban temporal (Limbo).
- [x] **Revivir**:
    - [x] Item "Orbe de Resurrección" (funcionalidad al usar).
    - [x] Item "Contrato de Alma" (ban propio por revivir a otro).
    - [x] Comando admin `/rd revive <player>` (Implementado como `/rd admin life set`).
- [x] **Día 31 (Permadeath)**: Configuración para activar ban permanente en fecha específica.

### 2.2. Sistema de Teams (Alianzas)
- [x] **Gestor de Equipos**: Estructura de datos `Team` y `TeamManager`.
- [x] **Comandos**: `/team create`, `/invite`, `/kick`, `/leave`, `/disband`.
- [x] **Restricciones**: Máximo 4 jugadores (Implementado en lógica, falta comando).
- [x] **Chat**: Canal privado `/tc <msg>`.
- [x] **Friendly Fire**: Toggleable, por defecto OFF (salvo en eventos de Caos).
- [x] **Guerra**: Sistema para declarar guerra entre teams (habilita griefing/pvp en protecciones).

### 2.3. Sistema de Protección (Claims)
- [x] **Protección de Bloques**:
    - Bloqueo automático de Cofres, Hornos, Puertas, Shulkers, Spawners al colocarlos.
    - Soporte para Cofres Dobles.
- [x] **Reglas de Acceso**:
    - Dueño tiene acceso total.
    - Miembros del Team tienen acceso total.
    - Admins con permiso `rd.admin.bypass` tienen acceso.
- [x] **Reglas de Griefing**:
    - **Bases Desconectadas**: Si el dueño Y todos los miembros del team están offline, la protección es indestructible (cancel break).
    - **La Purga**: Flag `isPurgeActive` implementado para desactivar protecciones.

## 3. Mecánicas de Ruleta (Modificadores)

### 3.1. Motor de Eventos
- [x] **ModifierManager**: Sistema abstracto para registrar, iniciar y detener efectos.
- [x] **Scheduler**: Ejecución automática a las 00:00 (hora servidor) o manual.
- [x] **Acumulación**: Lista persistente de efectos activos que crece cada día.

### 3.2. Implementación de Efectos
- **Maldiciones**:
    - [x] *Sol Tóxico*: Daño/Hambre si el cielo es visible y es de día.
    - [x] *Gravedad Pesada*: Cancelar saltos (Jump Boost 128), aumentar daño de caída (x2).
    - [x] *Escasez de Hierro*: Golems hostiles, Ores de hierro 50% chance de fallar.
    - [x] *Atmósfera Densa*: Elytras se rompen x2 rápido y consumen más cohetes.
    - [x] *Tierra Maldita*: `PlayerDeathEvent` -> Spawn Giant.
    - [x] *Arachnophobia*: Todas las arañas tienen Velocidad II e Invisibilidad.
    - [x] *Sin Regeneración*: `GameRule.NATURAL_REGENERATION` = false.
    - [x] *Suelo Frágil*: Stone/Cobble tiene chance de convertirse en Gravel al pisar.
    - [x] *Hambre Voraz*: El hambre baja 3 veces más rápido.
    - [x] *Creeper Nuclear*: Explosiones de Creeper son x3 más grandes.
    - [x] *Pesadillas*: Dormir tiene un 50% de chance de spawnear un Phantom instantáneo.
    - [x] *Agua Ácida*: Entrar al agua aplica Veneno.
    - [x] *Ceguera Profunda*: Bajo Y=0, tienes Ceguera permanente.
    - [x] *Inventario Pesado*: Si llevas el inventario lleno, tienes Lentitud I.
    - [x] *Tormenta Eterna*: Llueve y hay truenos constantemente.
    - [x] *Esqueletos Francotiradores*: Los esqueletos disparan un 50% más rápido.
    - [x] *Manos Resbaladizas*: Chance del 1% de soltar el ítem de tu mano al usarlo.
    - [x] *Madera Podrida*: Talar madera tiene chance de no dropear nada.
    - [x] *Silencio Mortal*: Los mobs hostiles no hacen sonidos.
    - [x] *Plaga de Ratas*: Silverfish spawnean al romper Stone.
- **Bendiciones**:
    - [x] *Corazón de Titán*: `Attribute.MAX_HEALTH`.
    - [x] *Vuelo de Ícaro*: Efecto `SLOW_FALLING` permanente.
    - [x] *Minería Explosiva*: `BlockBreakEvent` -> Drop extra/TNT.
    - [x] *Regalo del Cielo*: Scheduler que spawnea cofre en coords random cerca del spawn.
- **Caos**:
    - [x] *Mundo Espejo*: Cancelar target de Endermans, aggro de Cerdos.
    - [x] *La Purga*: Hook con el sistema de protección.
    - [x] *PvP Forzado*: Activar PvP globalmente.
    - [x] *Juego de la Silla*: Scheduler que intercambia posiciones de jugadores online.

## 4. Sistema de Roles (RPG Semanal)

### 4.1. Gestor de Roles
- [ ] Asignación aleatoria semanal (reset de roles).
- [x] Persistencia del rol del jugador.

### 4.2. Habilidades de Roles
- [x] **Vampiro**: Listener de daño por sol, buff de fuerza nocturna.
- [x] **Ingeniero**: Recetas de crafteo personalizadas (solo él puede usarlas).
- [x] **Fantasma**: Toggle para atravesar puertas (modo espectador limitado o teleport).
- [x] **Minero**: Haste/Night Vision bajo Y=0.
- [x] **Domador**: Buff a lobos/gatos propios.

## 5. Custom Items & Mobs

### 5.1. Item Factory
- [x] Sistema para generar ItemStack con NBT/PDC tags únicos.
- [x] **Items Funcionales**:
    - *Venda Curativa*: `PlayerInteractEvent` -> Cooldown + Heal.
    - *Mochila Pequeña*: Abre inventario virtual guardado en NBT/Base de datos.
    - *Gancho de Agarre*: Lógica de caña de pescar que impulsa al jugador.
    - *Pico Destructor*: `BlockBreakEvent` -> Rompe área 3x3.

### 5.2. Mob Factory
- [x] `CreatureSpawnEvent`: Reemplazar mobs vanilla por versiones custom según probabilidad.
- [x] **Mobs Custom**:
    - *Creeper Nuclear*: Aumentar radio de explosión.
    - *Zombie Veloz*: Aplicar Speed II.
    - *Rey Rata*: Silverfish con más vida que spawnea otros al recibir daño.

## 6. UX & Integración
- [x] **Scoreboard/Tablist**: Mostrar Vidas, Rol actual y Eventos activos.
- [x] **Chat Formatting**: `[Team] [Rol] Nick: Mensaje`.
- [x] **Web Integration**: Exportar `status.json` en la carpeta del plugin con los modificadores activos para que la web lo lea (si están en el mismo host) o API REST simple.

## 7. Comandos Administrativos
- [x] `/rd admin roulette spin`: Forzar giro de ruleta.
- [x] `/rd admin setday <day>`: Establecer día, guardar y girar ruleta con animación.
- [x] `/rd admin life set <player> <amount>`: Modificar vidas.
- [x] `/rd admin role set <player> <role>`: Forzar rol.
- [x] `/rd admin event add <event_name>`: Activar evento manualmente.
