export const dailyEvents = [
    // --- MALDICIONES (CURSES) ---
    { name: "Sol Tóxico", type: "Maldición", desc: "La luz directa del sol causa lentitud y hambre.", icon: "☀️" },
    { name: "Gravedad Pesada", type: "Maldición", desc: "No se puede saltar bloques completos. Caída hace x2 daño.", icon: "⚓" },
    { name: "Escasez de Hierro", type: "Maldición", desc: "Golems hostiles. Ores de hierro reducidos.", icon: "🛡️" },
    { name: "Atmósfera Densa", type: "Maldición", desc: "Elytras se rompen x2 rápido y consumen más cohetes.", icon: "☁️" },
    { name: "Tierra Maldita", type: "Maldición", desc: "Al morir, spawnea un Zombie Gigante con tu loot.", icon: "🧟" },
    { name: "Arachnophobia", type: "Maldición", desc: "Todas las arañas tienen Velocidad II e Invisibilidad.", icon: "🕷️" },
    { name: "Inflación", type: "Maldición", desc: "Aldeanos cobran x3 esmeraldas por todo.", icon: "💎" },
    { name: "Suelo Frágil", type: "Maldición", desc: "Stone/Cobble tiene chance de convertirse en Gravel al pisar.", icon: "🏚️" },
    { name: "Hambre Voraz", type: "Maldición", desc: "El hambre baja 3 veces más rápido.", icon: "🍖" },
    { name: "Sin Regeneración", type: "Maldición", desc: "La vida no se regenera por comida (UHC Mode).", icon: "💔" },
    { name: "Creeper Nuclear", type: "Maldición", desc: "Explosiones de Creeper son x3 más grandes.", icon: "💥" },
    { name: "Pesadillas", type: "Maldición", desc: "Dormir tiene un 50% de chance de spawnear un Phantom instantáneo.", icon: "🛌" },
    { name: "Agua Ácida", type: "Maldición", desc: "Entrar al agua aplica Veneno.", icon: "🧪" },
    { name: "Ceguera Profunda", type: "Maldición", desc: "Bajo Y=0, tienes Ceguera permanente.", icon: "👁️" },
    { name: "Inventario Pesado", type: "Maldición", desc: "Si llevas el inventario lleno, tienes Lentitud I.", icon: "🎒" },
    { name: "Tormenta Eterna", type: "Maldición", desc: "Llueve y hay truenos constantemente.", icon: "⚡" },
    { name: "Esqueletos Francotiradores", type: "Maldición", desc: "Los esqueletos disparan un 50% más rápido.", icon: "🏹" },
    { name: "Manos Resbaladizas", type: "Maldición", desc: "Chance del 1% de soltar el ítem de tu mano al usarlo.", icon: "✋" },
    { name: "Madera Podrida", type: "Maldición", desc: "Talar madera tiene chance de no dropear nada.", icon: "🌲" },
    { name: "Silencio Mortal", type: "Maldición", desc: "Los mobs hostiles no hacen sonidos.", icon: "🔇" },
    { name: "Plaga de Ratas", type: "Maldición", desc: "Silverfish spawnean al romper Stone.", icon: "🐀" },
    { name: "Gravedad Lunar Inversa", type: "Maldición", desc: "La gravedad se reduce en un 20%.", icon: "🎈" },
    { name: "Nether Inestable", type: "Maldición", desc: "Los portales te llevan a coordenadas random en el Nether.", icon: "🔥" },
    { name: "Toque de Midas Maligno", type: "Maldición", desc: "La comida se convierte en oro (no comestible) al tocarla.", icon: "🥖" },
    { name: "Enderman Furiosos", type: "Maldición", desc: "Mirar a cualquier lado puede aggrear Endermans.", icon: "👀" },
    { name: "Maldición de Binding", type: "Maldición", desc: "Toda armadura equipada no se puede quitar.", icon: "🔒" },
    { name: "Sombra Persistente", type: "Maldición", desc: "Las antorchas se apagan solas tras 5 minutos.", icon: "🕯️" },
    { name: "Pies de Plomo", type: "Maldición", desc: "No puedes nadar hacia arriba, te hundes rápido.", icon: "⚓" },
    { name: "Vientos Huracanados", type: "Maldición", desc: "Empuje (Knockback) recibido es x3.", icon: "💨" },
    { name: "Sed de Sangre", type: "Maldición", desc: "Si no matas algo en 10 min, pierdes 1 corazón.", icon: "🩸" },
    { name: "Modo Hardcore UI", type: "Maldición", desc: "No ves tus corazones ni tu barra de comida (F1 parcial).", icon: "🫣" },
    { name: "Sordera", type: "Maldición", desc: "Todos los sonidos del juego desactivados.", icon: "🙉" },
    { name: "Paranoia", type: "Maldición", desc: "Sonidos falsos de Creeper y TNT expltando aleatoriamente.", icon: "😱" },
    { name: "Desorientación", type: "Maldición", desc: "La brújula apunta a direcciones aleatorias, no al spawn.", icon: "🧭" },
    { name: "Maldición de Ícaro", type: "Maldición", desc: "Si subes por encima de Y=150, empiezas a quemarte.", icon: "☀️" },
    { name: "Fuego Fatuo", type: "Maldición", desc: "El fuego azul (soul fire) mata instantáneamente.", icon: "🔥" },
    { name: "Cristal Frágil", type: "Maldición", desc: "Romper cristal causa explosión pequeña.", icon: "🪟" },
    { name: "Sin Leche", type: "Maldición", desc: "No puedes beber leche ni limpiar efectos.", icon: "🥛" },

    // --- BENDICIONES (BLESSINGS) ---
    { name: "Corazón de Titán", type: "Bendición", desc: "+2 Corazones de vida máxima permanentes.", icon: "❤️" },
    { name: "Fuerza Descomunal", type: "Bendición", desc: "+20% de Daño físico permanente.", icon: "💪" },
    { name: "Sangre de Vampiro", type: "Bendición", desc: "Matar mobs cura medio corazón.", icon: "🩸" },
    { name: "Manos de Seda", type: "Bendición", desc: "Toque de seda automático en todo.", icon: "🧤" },
    { name: "Adrenalina", type: "Bendición", desc: "Bajo 3 corazones = Velocidad III.", icon: "⚡" },
    { name: "Bendición de Demeter", type: "Bendición", desc: "Cultivos crecen instantáneamente con click derecho.", icon: "🌾" },
    { name: "Piel de Acero", type: "Bendición", desc: "Resistencia al fuego permanente.", icon: "🔥" },
    { name: "Minería Explosiva", type: "Bendición", desc: "Chance de encontrar TNT o Diamantes al picar.", icon: "⛏️" },
    { name: "Vuelo de Ícaro", type: "Bendición", desc: "Caída lenta permanente.", icon: "🪶" },
    { name: "Branquias", type: "Bendición", desc: "Respiración acuática infinita.", icon: "🐟" },
    { name: "Aura de Luz", type: "Bendición", desc: "Emites luz al caminar (Vision Nocturna).", icon: "💡" },
    { name: "Doble Loot", type: "Bendición", desc: "Mobs sueltan el doble de ítems.", icon: "💰" },
    { name: "Experiencia Líquida", type: "Bendición", desc: "Todo da x3 XP.", icon: "🧪" },
    { name: "Puño de Hierro", type: "Bendición", desc: "Golpear con la mano hace daño como espada de piedra.", icon: "👊" },
    { name: "Fotosíntesis", type: "Bendición", desc: "Recuperas hambre bajo el sol.", icon: "🌻" },
    { name: "Amistad Animal", type: "Bendición", desc: "Lobos y Gatos domados son inmortales.", icon: "🐺" },
    { name: "Toque de Fortuna", type: "Bendición", desc: "Ores siempre dan el máximo drop posible.", icon: "🍀" },
    { name: "Velocidad de la Luz", type: "Bendición", desc: "Caminar sobre caminos de tierra da Velocidad II.", icon: "👟" },
    { name: "Pescador Legendario", type: "Bendición", desc: "Pesca es instantánea.", icon: "🎣" },
    { name: "Herrero Divino", type: "Bendición", desc: "Yunques nunca se rompen.", icon: "🔨" },
    { name: "Inmunidad al Veneno", type: "Bendición", desc: "Inmune a veneno y wither.", icon: "🤢" },
    { name: "Salto de Fe", type: "Bendición", desc: "No recibes daño de caída en césped.", icon: "🌱" },
    { name: "Comercio Justo", type: "Bendición", desc: "Piglin Bartering siempre da cosas buenas.", icon: "🐷" },
    { name: "Escudo Reflejante", type: "Bendición", desc: "Bloquear devuelve el 50% del daño.", icon: "🛡️" },
    { name: "Leñador Maestro", type: "Bendición", desc: "Talar un bloque rompe todo el árbol.", icon: "🪓" },
    { name: "Imán de Items", type: "Bendición", desc: "Los items del suelo vuelan hacia ti (rango 5m).", icon: "🧲" },
    { name: "Regalo del Cielo", type: "Bendición", desc: "Cofre con suministros cae en Spawn cada hora.", icon: "🎁" },
    { name: "Arquero Mágico", type: "Bendición", desc: "Las flechas no se consumen.", icon: "🏹" },
    { name: "Poción Permanente", type: "Bendición", desc: "Efecto de Haste I para todos.", icon: "⛏️" },
    { name: "Sin Hambre", type: "Bendición", desc: "La barra de comida no baja si no corres.", icon: "🍔" },
    { name: "Ojos de Ender", type: "Bendición", desc: "Endermans dropean perlas 100% garantizado.", icon: "🟣" },
    { name: "Cama Regenerativa", type: "Bendición", desc: "Dormir cura toda la vida al instante.", icon: "🛌" },
    { name: "Invisibilidad Táctica", type: "Bendición", desc: "Si te agachas por 3 segundos, te vuelves invisible.", icon: "👻" },
    { name: "Supersalto", type: "Bendición", desc: "Salto Impulso II permanente.", icon: "🐇" },
    { name: "Vida Extra", type: "Bendición", desc: "+1 Corazón Dorado (absorción) que se regenera cada día.", icon: "💛" },

    // --- CAOS (CHAOS) ---
    { name: "Mundo Espejo", type: "Caos", desc: "Enderman pacíficos, Cerdos agresivos.", icon: "🪞" },
    { name: "Lluvia de Slimes", type: "Caos", desc: "Lluvia spawnea slimes del cielo.", icon: "🟢" },
    { name: "Intercambio Equivalente", type: "Caos", desc: "Matar un mob te teletransporta a su lugar.", icon: "🔄" },
    { name: "Noche Eterna", type: "Caos", desc: "El sol nunca sale.", icon: "🌑" },
    { name: "Botín Explosivo", type: "Caos", desc: "20% chance de que cofres exploten.", icon: "💣" },
    { name: "Gravedad Cero", type: "Caos", desc: "Todos tienen levitación leve constante.", icon: "🚀" },
    { name: "Suelo de Hielo", type: "Caos", desc: "Todo el mundo resbala como en hielo.", icon: "❄️" },
    { name: "Chat Maldito", type: "Caos", desc: "Hablar en chat te hace daño.", icon: "💬" },
    { name: "Juego de la Silla", type: "Caos", desc: "Cada 30 min, todos cambian de posición con otro jugador.", icon: "🪑" },
    { name: "Mundo Gigante", type: "Caos", desc: "Los Slimes y Magma Cubes son x4 tamaño.", icon: "🟩" },
    { name: "Sin Coordenadas", type: "Caos", desc: "F3 desactivado/oculto completamente.", icon: "🗺️" },
    { name: "Invasión Zombie", type: "Caos", desc: "Solo spawnean Zombies, pero muchísimos.", icon: "🧟" },
    { name: "Invasión Esqueleto", type: "Caos", desc: "Solo spawnean Esqueletos.", icon: "💀" },
    { name: "Día de Pesca", type: "Caos", desc: "Solo se puede comer pescado.", icon: "🐟" },
    { name: "La Purga", type: "Caos", desc: "Se pueden robar cofres protegidos.", icon: "🎭" },
    { name: "Terremoto", type: "Caos", desc: "La pantalla tiembla cada cierto tiempo.", icon: "📉" },
    { name: "Susurros", type: "Caos", desc: "Sonidos de cueva aleatorios para todos.", icon: "👻" },
    { name: "Gallinas Explosivas", type: "Caos", desc: "Las gallinas explotan si te acercas.", icon: "🐔" },
    { name: "Sin Armadura", type: "Caos", desc: "No se puede equipar pecheras.", icon: "👕" },
    { name: "Guerra de Nieve", type: "Caos", desc: "Las bolas de nieve hacen 2 corazones de daño.", icon: "⛄" },
    { name: "Pisos de Lava", type: "Caos", desc: "La lava fluye tan rápido como el agua.", icon: "🌋" },
    { name: "Atracción Fatal", type: "Caos", desc: "Todos los mobs son atraídos hacia el jugador más cercano.", icon: "🧲" },
    { name: "Sin Nombres", type: "Caos", desc: "Nametags de jugadores ocultos.", icon: "🕵️" },
    { name: "Ruleta Rusa", type: "Caos", desc: "Comer tiene 1% de chance de matar.", icon: "🔫" },
    { name: "Sin Chat", type: "Caos", desc: "El chat está deshabilitado. Nadie puede leerte.", icon: "🤐" },
    { name: "Inventario Ciego", type: "Caos", desc: "Los tooltips de los items están ocultos.", icon: "❓" },
    { name: "Miopes", type: "Caos", desc: "Distancia de renderizado forzada a 2 chunks.", icon: "👓" },
];

export const weeklyRoles = [
    { name: "El Pacifista", pros: "Regeneración I 5s (se renueva cada 4s).", cons: "No puede infligir daño directo (ataques cancelados).", icon: "🕊️" },
    { name: "El Vampiro", pros: "Fuerza I 2s + Visión Nocturna 12s de noche/lluvia/sin sol directo.", cons: "Con sol directo: se quema 3s por tick o casco pierde 3 de durabilidad/s.", icon: "🦇" },
    { name: "Glass Cannon", pros: "Todo el daño que inflige se duplica (x2).", cons: "Daño recibido x2 y vida máxima 3 corazones.", icon: "💥" },
    { name: "Rey Midas", pros: "Hierro/Cobre recogido se vuelve oro; 10% de piedra/cobble en pepitas.", cons: "Cada 10 min consume 1 pepita/lingote o recibe 2 corazones; sin regeneración natural.", icon: "👑" },
    { name: "El Nómada", pros: "Velocidad II 2s (refresco cada 1s) mientras no repita cama.", cons: "Dormir 2 veces en la misma cama quita la velocidad y hace explotar la cama.", icon: "⛺" },
    { name: "El Tanque", pros: "Resistencia I y 40 de vida (20 corazones), refresco cada 4s.", cons: "Lentitud II permanente y el hambre se consume x3 más rápido.", icon: "🛡️" },
    { name: "El Asesino", pros: "Invisibilidad al agacharse; backstab duplica daño si el objetivo mira igual dirección.", cons: "Solo puede usar armadura de cuero.", icon: "🗡️" },
    { name: "El Ingeniero", pros: "Prisa II y Suerte II 5s (se renuevan cada 4s).", cons: "Lentitud I permanente.", icon: "🔧" },
    { name: "El Pirotécnico", pros: "Resistencia al Fuego 5s (cada 4s); golpes prenden 5s.", cons: "Recibe daño al tocar agua o bajo la lluvia.", icon: "🧨" },
    { name: "El Maldito", pros: "50% de probabilidad de que mobs lo ignoren al fijar objetivo.", cons: "Mala Suerte V 5s (renovada cada 4s).", icon: "💀" },
    { name: "El Druida", pros: "En forest/jungle/taiga: Regeneración I + Velocidad I 5s (cada 3s); animales lo siguen (10x5x10).", cons: "No puede usar armadura de metal.", icon: "🌿" },
    { name: "El Berserker", pros: "+10% daño por cada 2 corazones faltantes (acumulativo).", cons: "No puede usar escudos (se retiran del offhand).", icon: "🪓" },
    { name: "El Francotirador", pros: "Flechas a >20 bloques hacen x1.5 daño (aviso al tirador).", cons: "Daño cuerpo a cuerpo reducido 50%.", icon: "🎯" },
    { name: "El Mercader", pros: "Héroe de la Aldea II 5s (cada 4s); 5% de esmeralda extra al matar mobs.", cons: "Los mobs hostiles lo targetean primero en 12 bloques.", icon: "💰" },
    { name: "El Fantasma", pros: "Puede abrir puertas (incluye hierro) al agacharse.", cons: "Vida máxima 10 (5 corazones).", icon: "👻" },
    { name: "El Acuático", pros: "Respiración acuática permanente; en agua: Gracia del Delfín I + Poder del Canal I 2s (cada 1s).", cons: "Recibe daño si está fuera de agua o lluvia.", icon: "🧜" },
    { name: "El Minero", pros: "Prisa II 5s (cada 4s); Visión Nocturna 12s bajo Y<0.", cons: "Ceguera en la superficie durante el día.", icon: "⛏️" },
    { name: "El Domador", pros: "Mascotas infligen x2 daño y reciben x0.5 daño.", cons: "Comparte ~50% del daño recibido con sus mascotas cercanas (12 bloques).", icon: "🐕" },
    { name: "El Alquimista", pros: "Pociones bebida/splash triplican su duración manteniendo nivel.", cons: "Efectos negativos también duran x3.", icon: "⚗️" },
    { name: "El Caballero", pros: "+30% daño con espada.", cons: "-50% daño con arco y no puede usar arcos/crossbows/tridentes.", icon: "⚔️" },
    { name: "El Ladrón", pros: "Velocidad II 5s (cada 4s); puede robar un slot al hacer sneak + clic derecho (CD 5 min).", cons: "Suelta el ítem de su mano al recibir daño.", icon: "🦝" },
    { name: "El Gigante", pros: "Vida máxima 40 (20 corazones); modelo x2 si está disponible.", cons: "Hitbox más grande (fácil de golpear).", icon: "🦍" },
    { name: "El Enano", pros: "Modelo x0.5 si está disponible.", cons: "Vida máxima 16 (8 corazones) y alcance reducido.", icon: "🐁" },
    { name: "El Ilusionista", pros: "25% de esquivar: cancela golpe e Invisibilidad 3s con partículas.", cons: "No puede comer carne (consumo cancelado).", icon: "🎭" },
    { name: "El Bárbaro", pros: "+50% daño cuerpo a cuerpo.", cons: "No puede equipar armadura de diamante/netherite ni encantar objetos.", icon: "🍖" },
    { name: "El Sabio", pros: "Experiencia ganada x2.", cons: "Debilidad I permanente (refrescada cada 4s).", icon: "📚" },
    { name: "El Caótico", pros: "30% de aplicar debuff random (5s nivel I) al golpear.", cons: "Recibe un efecto negativo aleatorio cada 10 min.", icon: "🎲" },
    { name: "El Guardián", pros: "Resistencia I 2s propia; aliados a 5 bloques reciben Resistencia I + Regeneración I 2s (cada 1s).", cons: "Redirige 30% del daño de aliados a 5 bloques hacia sí mismo.", icon: "🏰" },
    { name: "El Explorador", pros: "Velocidad I 2s (cada 1s) y no pierde hambre al esprintar.", cons: "Última fila del inventario bloqueada y vaciada si la llena.", icon: "🧭" },
    { name: "El Cocinero", pros: "Al comer: +5 saturación, Regeneración I 5s, Absorción I 60s.", cons: "Si come algo no comestible: Veneno IV 30s; carne cruda aplica Veneno II corto.", icon: "👨‍🍳" },
];

export const mobs = [
    // --- COMÚN ---
    { name: "Rata de Cueva", desc: "Silverfish un poco más grande. Envenena.", danger: "⭐", rarity: "comun" },
    { name: "Fantasma Menor", desc: "Vuela bajo y empuja. No hace mucho daño.", danger: "⭐", rarity: "comun" },
    { name: "Slime de Magma", desc: "Versión pequeña del Magma Cube. Spawnea en superficie.", danger: "⭐", rarity: "comun" },
    { name: "Araña Saltadora", desc: "Araña que salta 5 bloques de altura.", danger: "⭐", rarity: "comun" },
    { name: "Zombie Minero", desc: "Lleva casco y pico. Rompe puertas rápido.", danger: "⭐", rarity: "comun" },
    { name: "Esqueleto Vagabundo", desc: "Usa espada en lugar de arco. Corre rápido.", danger: "⭐", rarity: "comun" },
    { name: "Creeper Húmedo", desc: "No explota, pero deja un charco de agua persistente.", danger: "⭐", rarity: "comun" },

    // --- RARO ---
    { name: "Esqueleto Blindado", desc: "Esqueleto con armadura completa de hierro.", danger: "⭐⭐", rarity: "raro" },
    { name: "Zombie Veloz", desc: "Zombie con Velocidad II. Muy molesto.", danger: "⭐⭐", rarity: "raro" },
    { name: "La Colmena", desc: "Zombie que suelta 5 Silverfish y 2 Arañas al morir.", danger: "⭐⭐⭐", rarity: "raro" },
    { name: "Espíritu Vengativo", desc: "Vex que no desaparece. Spawnea al minar diamantes.", danger: "⭐⭐⭐", rarity: "raro" },
    { name: "Golem Corrupto", desc: "Iron Golem agresivo con textura oxidada. Lento pero letal.", danger: "⭐⭐⭐", rarity: "raro" },
    { name: "Creeper de Hielo", desc: "Explosión congela el área y aplica lentitud.", danger: "⭐⭐", rarity: "raro" },
    { name: "Fantasma Gigante", desc: "Phantom x3 tamaño. Puede agarrar jugadores.", danger: "⭐⭐⭐", rarity: "raro" },
    { name: "Bruja del Pantano", desc: "Lanza pociones de nivel II. Spawnea slimes.", danger: "⭐⭐", rarity: "raro" },

    // --- ÉPICO ---
    { name: "El Acechador", desc: "Creeper invisible. Hace sonido de estática y aplica Ceguera.", danger: "⭐⭐⭐⭐", rarity: "epico" },
    { name: "Torreta de Hueso", desc: "Esqueleto inmóvil con arco perforante de largo alcance.", danger: "⭐⭐⭐⭐", rarity: "epico" },
    { name: "Sombra", desc: "Enderman que no se teletransporta pero corre supersónico.", danger: "⭐⭐⭐⭐", rarity: "epico" },
    { name: "Blaze Azul", desc: "Dispara fuego azul (Soul Fire) con daño x2.", danger: "⭐⭐⭐⭐", rarity: "epico" },
    { name: "Shulker Mímico", desc: "Parece un bloque de piedra. Ataca al minarlo.", danger: "⭐⭐⭐⭐", rarity: "epico" },
    { name: "Jinete de Araña Elite", desc: "Esqueleto Wither montado en Araña de Cueva.", danger: "⭐⭐⭐⭐", rarity: "epico" },
    { name: "Evoker Loco", desc: "Spawnea Vexes infinitos hasta que muere.", danger: "⭐⭐⭐⭐", rarity: "epico" },

    // --- LEGENDARIO / PERMADEATH ---
    { name: "Caballero del Apocalipsis", desc: "Jinete con armadura netherite y espada letal.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
    { name: "Leviatán", desc: "Bestia acuática gigante; controla el agua a su alrededor.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
    { name: "Rey Rata", desc: "Manda oleadas de ratas; invoca refuerzos al ser golpeado.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
    { name: "El Warden Despierto", desc: "Versión agresiva del Warden, detección y daño aumentados.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
    { name: "Dragón Alpha", desc: "Mini jefe volador con aliento potenciado.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
    { name: "El Segador", desc: "Entidad oscura con guadaña; aplica Wither y ceguera.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
    { name: "Rey Slime", desc: "Slime gigante con mucha vida; deja minions al dividirse.", danger: "⭐⭐⭐⭐", rarity: "legendario" },
    { name: "Banshee", desc: "Vex potenciada que grita y golpea fuerte.", danger: "⭐⭐⭐⭐", rarity: "legendario" },
    { name: "Caminante del Vacío", desc: "Wither Skeleton con set netherite y daño brutal.", danger: "⭐⭐⭐⭐⭐", rarity: "legendario" },
];

export const wikiAdvancedEntries = [
    {
        id: "toolsmithing",
        title: "Mesa de Herrero Avanzada",
        icon: "🛠️",
        content: [
            {
                subtitle: "Descripción",
                text: "La Toolsmithing Table permite aplicar trims a herramientas y duplicar plantillas especiales. Su interfaz es casi idéntica a una mesa de herrero vanilla.",
                image: "https://imgur.com/DVL9fys.png",
                imageAlt: "Receta de Toolsmithing Table"
            },
            {
                subtitle: "Receta",
                text: "Coloca 2 lingotes de cobre en la fila superior y 4 tablones de madera debajo para craftear la mesa.",
                recipeImage: "https://imgur.com/DVL9fys.png",
                recipeAlt: "Receta de la mesa"
            },
            {
                subtitle: "Plantillas Disponibles",
                trims: [
                    { name: "Linear", image: "https://i.postimg.cc/28MdcMM7/xItrOqb.png", structures: "Ruinas y Minas", chance: "7.6% / 5.6%" },
                    { name: "Tracks", image: "https://i.postimg.cc/ncCY1pRL/RxjzI7J.png", structures: "Puesto Saqueador / Mansión", chance: "37.5% / 50%" },
                    { name: "Charge", image: "https://i.postimg.cc/T2kJmpcS/LpdAQ3g.png", structures: "Ancient City", chance: "5%" },
                    { name: "Frost", image: "https://i.postimg.cc/5yk5MR01/xNVu2Qn.png", structures: "Igloo", chance: "40%" }
                ]
            },
            {
                subtitle: "Duplicación",
                text: "Duplica cualquier plantilla rodeándola con 7 diamantes y el bloque base correspondiente dentro de la mesa.",
                gif: "https://imgur.com/fMgLznI.gif",
                gifAlt: "Duplicación de plantillas"
            }
        ],
        citation: "https://modrinth.com/datapack/tool-trims"
    },
    {
        id: "glamour",
        title: "Sistema Glamour",
        icon: "🎨",
        content: [
            {
                subtitle: "Mesa de Pintura",
                text: "La Painting Table se fabrica con tablones, un bowl y un brush. Sirve para crear y colorear lienzos.",
                recipeImage: "https://cdn.modrinth.com/data/cached_images/89c3a9530d370b0088ba455a172cde5751d852a8.png",
                recipeAlt: "Receta de Painting Table"
            },
            {
                subtitle: "Lienzos",
                text: "Craftea lienzos con palos y una alfombra. El color de la alfombra define el color base del canvas.",
                recipeImage: "https://cdn.modrinth.com/data/cached_images/35412102562cdf234735dd9b1499c20bef7443ad.png",
                recipeAlt: "Receta de canvas"
            },
            {
                subtitle: "Mesa Glamour",
                text: "Combina glowstone, un Creaking Heart, diamantes y cualquier alfombra para desbloquear la Glamour Table.",
                recipeImage: "https://cdn.modrinth.com/data/cached_images/7b3c5a40b959bb45870668efeee054db8e5c3818.png",
                recipeAlt: "Receta de Glamour Table"
            },
            {
                subtitle: "Preparar la Herramienta",
                text: "Antes de pintar, prepara tu herramienta con un ojo de ender y glowstone en una mesa de herrero para aplicar el contorno base.",
                recipeImage: "https://cdn.modrinth.com/data/cached_images/a93140f0f3a3acefb7b8f62e5952eb8c4e9c487f.png",
                recipeAlt: "Preparación previa"
            },
            {
                subtitle: "Pinceles",
                text: "Tiñe un brush con tintes en la mesa de crafteo para obtener pinceles de color personalizados.",
                recipeImage: "https://cdn.modrinth.com/data/cached_images/28a524a0ba89bb5d83ad615c5eb5df55e3c3297e.png",
                recipeAlt: "Receta de pinceles"
            },
            {
                subtitle: "Cómo Pintar",
                text: "Coloca el lienzo o herramienta en su mesa. Usa tintes o pinceles para pintar píxel a píxel. Con slime ball borras y con mano vacía retiras el ítem.",
                gif: "https://cdn.modrinth.com/data/cached_images/89db7cc73d64b88c419d6c275fe0643b693cb5c3.gif",
                gifAlt: "Proceso de pintura"
            },
            {
                subtitle: "Eliminar Glamour",
                text: "Para revertir la textura, coloca la herramienta en la mesa Glamour y haz clic con una esponja húmeda.",
                text2: "Herramientas compatibles: todas las herramientas principales, espadas y mazas."
            },
            {
                subtitle: "Herramienta Externa",
                text: "La web Texture Helper Tool ayuda a generar comandos o plantillas paint-by-number para replicar texturas exactas.",
                link: { label: "Texture Helper Tool", url: "https://dqwertyc.github.io/glamour-table/" }
            }
        ],
        citation: "https://modrinth.com/mod/glamour"
    }
];
export const items = [
    // --- COMÚN ---
    {
        name: "Piedra de Afilar",
        type: "Utilidad",
        desc: "Repara un ítem en yunque sin gastar XP.",
        rarity: "comun",
        acquisition: "Crafting",
        recipe: {
            type: "shapeless",
            ingredients: ["Stone", "Flint"],
            result: "Piedra de Afilar"
        }
    },
    {
        name: "Palo Afilado",
        type: "Arma",
        desc: "+1.5 de daño, starter.",
        rarity: "comun",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Flint", null, null,
                "Stick", null, null,
                null, null, null
            ],
            result: "Palo Afilado"
        }
    },
    {
        name: "Antorcha Eterna",
        type: "Utilidad",
        desc: "No se apaga jamás.",
        rarity: "comun",
        acquisition: "Crafting",
        recipe: {
            type: "shapeless",
            ingredients: ["Torch", "Glowstone Dust"],
            result: "Antorcha Eterna"
        }
    },
    {
        name: "Pan Mohoso",
        type: "Consumible",
        desc: "Comida dudosa, puede dar efectos negativos.",
        rarity: "comun",
        acquisition: "Crafting",
        recipe: {
            type: "shapeless",
            ingredients: ["Bread", "Brown Mushroom"],
            result: "Pan Mohoso"
        }
    },
    {
        name: "Venda Curativa",
        type: "Consumible",
        desc: "Cura un poco al aplicarla.",
        rarity: "comun",
        acquisition: "Crafting",
        recipe: {
            type: "shapeless",
            ingredients: ["Paper", "White Wool"],
            result: "Venda Curativa"
        }
    },

    {
        name: "Sopa Misteriosa",
        type: "Consumible",
        desc: "Efecto aleatorio (bueno o malo) por 10s.",
        rarity: "comun",
        acquisition: "Crafting",
        recipe: {
            type: "shapeless",
            ingredients: ["Bowl", "Nether Wart", "Spider Eye"],
            result: "Sopa Misteriosa"
        }
    },

    // --- RARO ---
    {
        name: "Botas de Cuero Reforzadas",
        type: "Armadura",
        desc: "Botas de cuero con planchas de hierro, más durables.",
        rarity: "raro",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Iron Ingot", null, "Iron Ingot",
                "Leather Boots", null, "Leather Boots",
                null, null, null
            ],
            result: "Botas de Cuero Reforzadas"
        }
    },
    {
        name: "Casco de Acero",
        type: "Armadura",
        desc: "Defensa nivel netherite (+1 armadura, +2 dureza). Desbloqueo Día 10.",
        rarity: "raro",
        acquisition: "Crafting / KillStore Día 10",
        recipe: {
            type: "shaped",
            grid: [
                "Block of Iron", "Blast Furnace", "Block of Iron",
                "Chain", null, "Chain",
                "Iron Ingot", null, "Iron Ingot"
            ],
            result: "Casco de Acero"
        }
    },
    {
        name: "Pechera de Acero",
        type: "Armadura",
        desc: "Defensa nivel netherite (+2 armadura, +3 dureza). Desbloqueo Día 10.",
        rarity: "raro",
        acquisition: "Crafting / KillStore Día 10",
        recipe: {
            type: "shaped",
            grid: [
                "Block of Iron", "Blast Furnace", "Block of Iron",
                "Block of Iron", "Chainmail Chestplate", "Block of Iron",
                "Block of Iron", "Iron Ingot", "Block of Iron"
            ],
            result: "Pechera de Acero"
        }
    },
    {
        name: "Grebas de Acero",
        type: "Armadura",
        desc: "Defensa nivel netherite (+2 armadura, +3 dureza). Desbloqueo Día 10.",
        rarity: "raro",
        acquisition: "Crafting / KillStore Día 10",
        recipe: {
            type: "shaped",
            grid: [
                "Block of Iron", "Blast Furnace", "Block of Iron",
                "Chain", null, "Chain",
                "Block of Iron", "Iron Ingot", "Block of Iron"
            ],
            result: "Grebas de Acero"
        }
    },
    {
        name: "Botas de Acero",
        type: "Armadura",
        desc: "Defensa nivel netherite (+1 armadura, +2 dureza). Desbloqueo Día 10.",
        rarity: "raro",
        acquisition: "Crafting / KillStore Día 10",
        recipe: {
            type: "shaped",
            grid: [
                "Chain", null, "Chain",
                "Block of Iron", "Blast Furnace", "Block of Iron",
                "Iron Ingot", null, "Iron Ingot"
            ],
            result: "Botas de Acero"
        }
    },
    {
        name: "Espada de Acero",
        type: "Arma",
        desc: "Daño nivel netherite (+3 daño, +0.1 velocidad). Desbloqueo Día 10.",
        rarity: "raro",
        acquisition: "Crafting / KillStore Día 10",
        recipe: {
            type: "shaped",
            grid: [
                null, "Block of Iron", null,
                null, "Blast Furnace", null,
                null, "Iron Ingot", null
            ],
            result: "Espada de Acero"
        }
    },
    { name: "Botas de Hermes", type: "Armadura", desc: "Doble salto. Se rompen muy rápido.", rarity: "raro", acquisition: "Drop: Zombie Veloz (6%)" },
    { name: "Poción de Olvido", type: "Consumible", desc: "Elimina el agro de los mobs cercanos.", rarity: "raro", acquisition: "Drop: Bruja del Pantano" },
    {
        name: "Escudo de Espinas",
        type: "Escudo",
        desc: "Devuelve 100% del daño. Baja durabilidad.",
        rarity: "raro",
        acquisition: "Crafting",
        recipe: {
            type: "shapeless",
            ingredients: ["Shield", "Cactus"],
            result: "Escudo de Espinas"
        }
    },
    {
        name: "Arco de Hueso",
        type: "Arma",
        desc: "Dispara flechas torcidas pero hace más daño.",
        rarity: "raro",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                null, "Bone", "String",
                "Bone", null, "String",
                null, "Bone", "String"
            ],
            result: "Arco de Hueso"
        }
    },
    {
        name: "Pico de Vidrio",
        type: "Herramienta",
        desc: "Mina instantáneo pero tiene 10 usos.",
        rarity: "raro",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Glass", "Glass", "Glass",
                null, "Stick", null,
                null, "Stick", null
            ],
            result: "Pico de Vidrio"
        }
    },
    { name: "Imán de XP", type: "Amuleto", desc: "Atrae experiencia desde 20 bloques.", rarity: "raro", acquisition: "Drop: Slime de Magma" },
    {
        name: "Mochila Pequeña",
        type: "Utilidad",
        desc: "Una mochila pequeña para llevar más objetos (9 slots).",
        rarity: "raro",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Leather", "Chest", "Leather",
                "Leather", null, "Leather",
                "Leather", "Leather", "Leather"
            ],
            result: "Mochila Pequeña"
        }
    },
    {
        name: "Gancho de Agarre",
        type: "Utilidad",
        desc: "Te permite escalar paredes verticales.",
        rarity: "raro",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                null, "Iron Ingot", "Iron Ingot",
                null, "String", "Iron Ingot",
                "String", null, null
            ],
            result: "Gancho de Agarre"
        }
    },

    // --- ÉPICO ---
    { name: "Pico de la Codicia", type: "Herramienta", desc: "Fortuna X, pero te quita vida al picar.", rarity: "epico", acquisition: "Drop: Zombie Minero (1.5%)" },
    { name: "Manzana de la Discordia", type: "Consumible", desc: "Fuerza IV (1 min), luego Veneno II (1 min).", rarity: "epico", acquisition: "Drop: Bruja (3%)" },
    { name: "Tridente de Poseidón", type: "Arma", desc: "Invoca rayos sin tormenta. Riptide fuera del agua.", rarity: "epico", acquisition: "Drop: Leviatán / Ahogado" },
    {
        name: "Alas Blindadas",
        type: "Armadura",
        desc: "Elytras que dan protección como pechera de hierro.",
        rarity: "epico",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Netherite Ingot", "Elytra", "Netherite Ingot",
                "Netherite Ingot", null, "Netherite Ingot",
                "Netherite Ingot", null, "Netherite Ingot"
            ],
            result: "Alas Blindadas"
        }
    },
    {
        name: "Capa de Invisibilidad",
        type: "Armadura",
        desc: "Te hace invisible a mobs si no te mueves.",
        rarity: "epico",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Black Wool", "Black Wool", "Black Wool",
                "Black Wool", "Glass", "Black Wool",
                "Black Wool", "Black Wool", "Black Wool"
            ],
            result: "Capa de Invisibilidad"
        }
    },
    {
        name: "Casco Obsidiana",
        type: "Armadura",
        desc: "Superior a netherite (+2 armadura, +3 dureza, +0.02 KB). Desbloqueo Día 20.",
        rarity: "epico",
        acquisition: "Crafting / KillStore Día 20",
        recipe: {
            type: "shaped",
            grid: [
                "Diamond Block", "Netherite Scrap", "Diamond Block",
                "Obsidian", "Smithing Table", "Obsidian",
                null, null, null
            ],
            result: "Casco Obsidiana"
        }
    },
    {
        name: "Pechera Obsidiana",
        type: "Armadura",
        desc: "Superior a netherite (+3 armadura, +4 dureza, +0.02 KB). Desbloqueo Día 20.",
        rarity: "epico",
        acquisition: "Crafting / KillStore Día 20",
        recipe: {
            type: "shaped",
            grid: [
                "Diamond Block", "Netherite Scrap", "Diamond Block",
                "Obsidian", "Smithing Table", "Obsidian",
                "Diamond Block", "Obsidian", "Diamond Block"
            ],
            result: "Pechera Obsidiana"
        }
    },
    {
        name: "Grebas Obsidiana",
        type: "Armadura",
        desc: "Superior a netherite (+3 armadura, +4 dureza, +0.02 KB). Desbloqueo Día 20.",
        rarity: "epico",
        acquisition: "Crafting / KillStore Día 20",
        recipe: {
            type: "shaped",
            grid: [
                "Diamond Block", "Netherite Scrap", "Diamond Block",
                "Obsidian", null, "Obsidian",
                "Diamond Block", "Obsidian", "Diamond Block"
            ],
            result: "Grebas Obsidiana"
        }
    },
    {
        name: "Botas Obsidiana",
        type: "Armadura",
        desc: "Superior a netherite (+2 armadura, +2.5 dureza, +0.02 KB). Desbloqueo Día 20.",
        rarity: "epico",
        acquisition: "Crafting / KillStore Día 20",
        recipe: {
            type: "shaped",
            grid: [
                "Obsidian", null, "Obsidian",
                "Diamond Block", "Netherite Scrap", "Diamond Block",
                "Obsidian", null, "Obsidian"
            ],
            result: "Botas Obsidiana"
        }
    },
    {
        name: "Espada Obsidiana",
        type: "Arma",
        desc: "Superior a netherite (+4 daño, +0.1 velocidad). Desbloqueo Día 20.",
        rarity: "epico",
        acquisition: "Crafting (upgrade) / KillStore Día 20",
        recipe: {
            type: "shaped",
            grid: [
                null, "Obsidian", null,
                null, "Diamond Sword", null,
                null, "Netherite Scrap", null
            ],
            result: "Espada Obsidiana"
        }
    },
    { name: "Martillo de Guerra", type: "Arma", desc: "Daño de área masivo, recarga muy lenta.", rarity: "epico", acquisition: "Drop: Golem Corrupto" },
    { name: "Tótem de Regeneración", type: "Reliquia", desc: "Cura a todos los aliados en 10 bloques.", rarity: "epico", acquisition: "Cofres de Dungeon / Roll" },
    {
        name: "Dado de Loki",
        type: "Utilidad",
        desc: "Lanza un roll diario instantáneo sin gastar tu cooldown. Lleva suerte +20% o -20% según la receta usada.",
        rarity: "epico",
        acquisition: "Crafting (2 recetas)",
        recipe: {
            type: "shapeless",
            ingredients: [
                "Block of Emerald", "Nether Star", "Totem of Undying", "Netherite Ingot", "Enchanted Golden Apple",
                "Dragon's Breath", "Echo Shard", "Ghast Tear", "End Crystal"
            ],
            result: "Dado de Loki (+20% suerte)",
            alt: {
                type: "shapeless",
                ingredients: [
                    "Block of Emerald", "Wither Rose", "Fermented Spider Eye", "Poisonous Potato", "Rotten Flesh",
                    "Spider Eye", "Soul Soil", "Soul Torch", "Magma Cream"
                ],
                result: "Dado de Loki (-20% suerte)"
            }
        }
    },

    // --- LEGENDARIO / MÍTICO ---
    { name: "Tótem del Azar", type: "Reliquia", desc: "Te salva de morir + efecto random.", rarity: "legendario", acquisition: "Roll Diario (Muy Raro)" },
    {
        name: "Orbe de Resurrección",
        type: "Consumible",
        desc: "Revive a un compañero. Costo: 1 Vida propia.",
        rarity: "legendario",
        acquisition: "Crafting (Costo: 1 Vida)",
        recipe: {
            type: "shaped",
            grid: [
                "Block of Diamond", "Nether Star", "Block of Diamond",
                "Nether Star", "Block of Gold", "Nether Star",
                "Block of Diamond", "Nether Star", "Block of Diamond"
            ],
            result: "Orbe de Resurrección",
            warning: "Al craftear este ítem, perderás 1 vida permanentemente."
        }
    },
    { name: "Espada Vampírica", type: "Arma", desc: "Roba vida, quema al sol.", rarity: "legendario", acquisition: "Drop: Wither Skeleton (0.2%)" },
    { name: "Pico Destructor de Mundos", type: "Herramienta", desc: "Mina 3x3 bloques (Tunnel bore).", rarity: "legendario", acquisition: "Evento Especial" },
    {
        name: "Casco de la Visión Verdadera",
        type: "Armadura",
        desc: "Ves mobs invisibles y ores a través de paredes.",
        rarity: "legendario",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Gold Ingot", "Ender Pearl", "Gold Ingot",
                "Gold Ingot", null, "Gold Ingot",
                null, null, null
            ],
            result: "Casco de la Visión Verdadera"
        }
    },
    {
        name: "Manzana de Vida",
        type: "Consumible",
        desc: "Otorga +1 vida en el sistema de vidas al comerla, además de los efectos de la manzana encantada.",
        rarity: "legendario",
        acquisition: "Crafting",
        recipe: {
            type: "shaped",
            grid: [
                "Player Head", "Totem of Undying", "Player Head",
                "Totem of Undying", "Enchanted Golden Apple", "Totem of Undying",
                "Player Head", "Totem of Undying", "Player Head"
            ],
            result: "Manzana de Vida"
        }
    },
    {
        name: "Casco del Vacío",
        type: "Armadura",
        desc: "Tope de línea (+3 armadura, +3 dureza, +0.05 KB). Desbloqueo Día 30.",
        rarity: "legendario",
        acquisition: "Crafting / KillStore Día 30",
        recipe: {
            type: "shaped",
            grid: [
                "Crying Obsidian", "Netherite Ingot", "Crying Obsidian",
                "Echo Shard", "End Crystal", "Echo Shard",
                null, null, null
            ],
            result: "Casco del Vacío"
        }
    },
    {
        name: "Pechera del Vacío",
        type: "Armadura",
        desc: "Tope de línea (+4 armadura, +4 dureza, +0.05 KB). Desbloqueo Día 30.",
        rarity: "legendario",
        acquisition: "Crafting / KillStore Día 30",
        recipe: {
            type: "shaped",
            grid: [
                "Crying Obsidian", "Netherite Ingot", "Crying Obsidian",
                "Echo Shard", "End Crystal", "Echo Shard",
                "Crying Obsidian", "Netherite Ingot", "Crying Obsidian"
            ],
            result: "Pechera del Vacío"
        }
    },
    {
        name: "Grebas del Vacío",
        type: "Armadura",
        desc: "Tope de línea (+3.5 armadura, +3 dureza, +0.05 KB). Desbloqueo Día 30.",
        rarity: "legendario",
        acquisition: "Crafting / KillStore Día 30",
        recipe: {
            type: "shaped",
            grid: [
                "Crying Obsidian", "Netherite Ingot", "Crying Obsidian",
                "Echo Shard", null, "Echo Shard",
                "Crying Obsidian", "Netherite Ingot", "Crying Obsidian"
            ],
            result: "Grebas del Vacío"
        }
    },
    {
        name: "Botas del Vacío",
        type: "Armadura",
        desc: "Tope de línea (+2 armadura, +2 dureza, +0.05 KB). Desbloqueo Día 30.",
        rarity: "legendario",
        acquisition: "Crafting / KillStore Día 30",
        recipe: {
            type: "shaped",
            grid: [
                "Echo Shard", null, "Echo Shard",
                "Crying Obsidian", "Netherite Ingot", "Crying Obsidian",
                "Netherite Ingot", null, "Netherite Ingot"
            ],
            result: "Botas del Vacío"
        }
    },
    {
        name: "Espada del Vacío",
        type: "Arma",
        desc: "Tope de línea (+5 daño, +0.05 velocidad, +0.1 KB res). Desbloqueo Día 30.",
        rarity: "legendario",
        acquisition: "Crafting (upgrade) / KillStore Día 30",
        recipe: {
            type: "shaped",
            grid: [
                null, "Crying Obsidian", null,
                "Netherite Sword", "Echo Shard", null,
                null, "End Crystal", null
            ],
            result: "Espada del Vacío"
        }
    },
    { name: "Corazón de Notch", type: "Legendario", desc: "+1 Vida Extra permanente. Drop único.", rarity: "mitico", acquisition: "Drop: Warden Despierto" },
    {
        name: "Contrato de Alma",
        type: "Consumible",
        desc: "Ban 24h a cambio de revivir a otro.",
        rarity: "mitico",
        acquisition: "Crafting (Costo: Ban 24h)",
        recipe: {
            type: "shaped",
            grid: [
                "Soul Sand", "Paper", "Soul Sand",
                "Bone", "Wither Skeleton Skull", "Bone",
                "Soul Sand", "Ink Sac", "Soul Sand"
            ],
            result: "Contrato de Alma",
            warning: "ADVERTENCIA: Al usar este ítem serás baneado por 24 horas."
        }
    },
    { name: "Llamada del Vacío", type: "Trampa", desc: "Crea agujero negro temporal.", rarity: "mitico", acquisition: "Drop: The Stalker" },
    { name: "Huevo de Dragón Real", type: "Mascota", desc: "Invoca un Dragón bebé que ataca enemigos.", rarity: "mitico", acquisition: "Drop: Dragón Alpha" },
    { name: "La Mano de Dios", type: "Admin Item", desc: "Elimina un evento permanente de la lista. Un solo uso.", rarity: "mitico", acquisition: "Admin Only" },

    // --- ÍTEMS DE ROL (SOLO SU ROL PUEDE CRAFTEAR/USAR) ---
    {
        name: "Bálsamo Pacifista",
        type: "Consumible de Rol",
        desc: "Regeneración II 10s + Absorción I 30s. Solo El Pacifista.",
        rarity: "raro",
        acquisition: "Crafting (rol Pacifista)",
        recipe: {
            type: "shapeless",
            ingredients: ["Honey Bottle", "Ghast Tear", "Milk Bucket"],
            result: "Bálsamo Pacifista"
        }
    },
    {
        name: "Vial de Sangre",
        type: "Consumible de Rol",
        desc: "Fuerza I 30s + Visión Nocturna 45s. Solo El Vampiro.",
        rarity: "raro",
        acquisition: "Crafting (rol Vampiro)",
        recipe: {
            type: "shapeless",
            ingredients: ["Glass Bottle", "Fermented Spider Eye", "Nether Wart", "Redstone"],
            result: "Vial de Sangre"
        }
    },
    {
        name: "Carga de Cristal",
        type: "Consumible de Rol",
        desc: "Fuerza II 15s + Lentitud corta. Solo Glass Cannon.",
        rarity: "raro",
        acquisition: "Crafting (rol Glass Cannon)",
        recipe: {
            type: "shapeless",
            ingredients: ["Amethyst Shard", "TNT", "Glowstone Dust"],
            result: "Carga de Cristal"
        }
    },
    {
        name: "Talismán Dorado",
        type: "Consumible de Rol",
        desc: "Suerte 40s + Absorción I 30s. Solo Rey Midas.",
        rarity: "raro",
        acquisition: "Crafting (rol Rey Midas)",
        recipe: {
            type: "shapeless",
            ingredients: ["Block of Gold", "Copper Ingot", "Emerald"],
            result: "Talismán Dorado"
        }
    },
    {
        name: "Brújula Errante",
        type: "Consumible de Rol",
        desc: "Velocidad II + Salto I por 30s. Solo El Nómada.",
        rarity: "raro",
        acquisition: "Crafting (rol Nómada)",
        recipe: {
            type: "shapeless",
            ingredients: ["Compass", "Phantom Membrane", "Rabbit Foot"],
            result: "Brújula Errante"
        }
    },
    {
        name: "Placa Reforzada",
        type: "Consumible de Rol",
        desc: "Resistencia II 20s + Lentitud. Solo El Tanque.",
        rarity: "raro",
        acquisition: "Crafting (rol Tanque)",
        recipe: {
            type: "shapeless",
            ingredients: ["Block of Iron", "Shield", "Turtle Helmet"],
            result: "Placa Reforzada"
        }
    },
    {
        name: "Kit de Veneno",
        type: "Consumible de Rol",
        desc: "Invisibilidad 20s + Fuerza I 10s. Solo El Asesino.",
        rarity: "raro",
        acquisition: "Crafting (rol Asesino)",
        recipe: {
            type: "shapeless",
            ingredients: ["Fermented Spider Eye", "Arrow", "Black Dye"],
            result: "Kit de Veneno"
        }
    },
    {
        name: "Kit de Reparación",
        type: "Consumible de Rol",
        desc: "Prisa II 30s + Resistencia I 10s. Solo El Ingeniero.",
        rarity: "raro",
        acquisition: "Crafting (rol Ingeniero)",
        recipe: {
            type: "shapeless",
            ingredients: ["Redstone", "Iron Ingot", "Anvil"],
            result: "Kit de Reparación"
        }
    },
    {
        name: "Mechero Potenciado",
        type: "Consumible de Rol",
        desc: "Resistencia al Fuego 45s + Fuerza I 15s. Solo El Pirotécnico.",
        rarity: "raro",
        acquisition: "Crafting (rol Pirotécnico)",
        recipe: {
            type: "shapeless",
            ingredients: ["Flint and Steel", "Blaze Powder", "Magma Cream"],
            result: "Mechero Potenciado"
        }
    },
    {
        name: "Ídolo Torcido",
        type: "Consumible de Rol",
        desc: "50/50: Suerte + Regeneración o Debilidad + Veneno. Solo El Maldito.",
        rarity: "raro",
        acquisition: "Crafting (rol Maldito)",
        recipe: {
            type: "shapeless",
            ingredients: ["Wither Rose", "Ender Pearl", "Rotten Flesh"],
            result: "Ídolo Torcido"
        }
    },
    {
        name: "Semilla Ancestral",
        type: "Consumible de Rol",
        desc: "Regeneración I 15s + Saturación corta. Solo El Druida.",
        rarity: "raro",
        acquisition: "Crafting (rol Druida)",
        recipe: {
            type: "shapeless",
            ingredients: ["Moss Block", "Wheat Seeds", "Bone Meal"],
            result: "Semilla Ancestral"
        }
    },
    {
        name: "Hidromiel Rabiosa",
        type: "Consumible de Rol",
        desc: "Fuerza II 25s + Hambre. Solo El Berserker.",
        rarity: "raro",
        acquisition: "Crafting (rol Berserker)",
        recipe: {
            type: "shapeless",
            ingredients: ["Honey Bottle", "Blaze Powder", "Cooked Beef"],
            result: "Hidromiel Rabiosa"
        }
    },
    {
        name: "Mira Improvisada",
        type: "Consumible de Rol",
        desc: "Daño de disparo mejorado + ligera Lentitud. Solo El Francotirador.",
        rarity: "raro",
        acquisition: "Crafting (rol Francotirador)",
        recipe: {
            type: "shapeless",
            ingredients: ["Spyglass", "Feather", "String"],
            result: "Mira Improvisada"
        }
    },
    {
        name: "Libro de Tratos",
        type: "Consumible de Rol",
        desc: "Héroe de la Aldea 120s. Solo El Mercader.",
        rarity: "raro",
        acquisition: "Crafting (rol Mercader)",
        recipe: {
            type: "shapeless",
            ingredients: ["Book", "Emerald", "Gold Ingot"],
            result: "Libro de Tratos"
        }
    },
    {
        name: "Velo Etéreo",
        type: "Consumible de Rol",
        desc: "Invisibilidad 25s + Debilidad. Solo El Fantasma.",
        rarity: "raro",
        acquisition: "Crafting (rol Fantasma)",
        recipe: {
            type: "shapeless",
            ingredients: ["White Stained Glass Pane", "Phantom Membrane", "Snowball"],
            result: "Velo Etéreo"
        }
    },
    {
        name: "Branquias de Coral",
        type: "Consumible de Rol",
        desc: "Respiración acuática 60s + Gracia del Delfín. Solo El Acuático.",
        rarity: "raro",
        acquisition: "Crafting (rol Acuático)",
        recipe: {
            type: "shapeless",
            ingredients: ["Prismarine Shard", "Kelp", "Tropical Fish"],
            result: "Branquias de Coral"
        }
    },
    {
        name: "Carga de Túnel",
        type: "Consumible de Rol",
        desc: "Prisa II 40s + Visión Nocturna. Solo El Minero.",
        rarity: "raro",
        acquisition: "Crafting (rol Minero)",
        recipe: {
            type: "shapeless",
            ingredients: ["TNT", "Iron Pickaxe", "Torch"],
            result: "Carga de Túnel"
        }
    },
    {
        name: "Silbato Alfa",
        type: "Consumible de Rol",
        desc: "Resistencia I + Fuerza I 20s. Solo El Domador.",
        rarity: "raro",
        acquisition: "Crafting (rol Domador)",
        recipe: {
            type: "shapeless",
            ingredients: ["Bone", "Lead", "Cooked Chicken"],
            result: "Silbato Alfa"
        }
    },
    {
        name: "Catalizador Alquímico",
        type: "Consumible de Rol",
        desc: "Suerte 45s + Regeneración corta. Solo El Alquimista.",
        rarity: "raro",
        acquisition: "Crafting (rol Alquimista)",
        recipe: {
            type: "shapeless",
            ingredients: ["Nether Wart", "Redstone", "Glowstone Dust"],
            result: "Catalizador Alquímico"
        }
    },
    {
        name: "Estandarte de Orden",
        type: "Consumible de Rol",
        desc: "Absorción II 40s + Resistencia I. Solo El Caballero.",
        rarity: "raro",
        acquisition: "Crafting (rol Caballero)",
        recipe: {
            type: "shapeless",
            ingredients: ["White Banner", "Iron Sword", "Shield"],
            result: "Estandarte de Orden"
        }
    },
    {
        name: "Juego de Ganzúas",
        type: "Consumible de Rol",
        desc: "Velocidad II 20s + Invisibilidad 15s. Solo El Ladrón.",
        rarity: "raro",
        acquisition: "Crafting (rol Ladrón)",
        recipe: {
            type: "shapeless",
            ingredients: ["Tripwire Hook", "Iron Nugget", "Ender Pearl"],
            result: "Juego de Ganzúas"
        }
    },
    {
        name: "Bracera Colosal",
        type: "Consumible de Rol",
        desc: "Health Boost II 45s + Lentitud. Solo El Gigante.",
        rarity: "raro",
        acquisition: "Crafting (rol Gigante)",
        recipe: {
            type: "shapeless",
            ingredients: ["Block of Iron", "Leather", "Beetroot Soup"],
            result: "Bracera Colosal"
        }
    },
    {
        name: "Martillo de Forja",
        type: "Consumible de Rol",
        desc: "Prisa III 30s + Resistencia al Fuego. Solo El Enano.",
        rarity: "raro",
        acquisition: "Crafting (rol Enano)",
        recipe: {
            type: "shapeless",
            ingredients: ["Iron Pickaxe", "Coal", "Anvil"],
            result: "Martillo de Forja"
        }
    },
    {
        name: "Espejo Fatuo",
        type: "Consumible de Rol",
        desc: "Invisibilidad 20s + Velocidad. Solo El Ilusionista.",
        rarity: "raro",
        acquisition: "Crafting (rol Ilusionista)",
        recipe: {
            type: "shapeless",
            ingredients: ["Glass Pane", "Lapis Lazuli", "Ender Pearl"],
            result: "Espejo Fatuo"
        }
    },
    {
        name: "Hidromiel Bárbara",
        type: "Consumible de Rol",
        desc: "Fuerza II 25s + Fatiga de Minería. Solo El Bárbaro.",
        rarity: "raro",
        acquisition: "Crafting (rol Bárbaro)",
        recipe: {
            type: "shapeless",
            ingredients: ["Cooked Porkchop", "Honey Bottle", "Blaze Powder"],
            result: "Hidromiel Bárbara"
        }
    },
    {
        name: "Pergamino del Sabio",
        type: "Consumible de Rol",
        desc: "Suerte II 60s + Absorción. Solo El Sabio.",
        rarity: "raro",
        acquisition: "Crafting (rol Sabio)",
        recipe: {
            type: "shapeless",
            ingredients: ["Paper", "Lapis Lazuli", "Amethyst Shard"],
            result: "Pergamino del Sabio"
        }
    },
    {
        name: "Sello del Caos",
        type: "Consumible de Rol",
        desc: "Efecto aleatorio: fuerza/velocidad o debilidad/lentitud. Solo El Caótico.",
        rarity: "raro",
        acquisition: "Crafting (rol Caótico)",
        recipe: {
            type: "shapeless",
            ingredients: ["Echo Shard", "Redstone", "Gunpowder"],
            result: "Sello del Caos"
        }
    },
    {
        name: "Sígilo de Guardia",
        type: "Consumible de Rol",
        desc: "Resistencia II 15s + Absorción 30s. Solo El Guardián.",
        rarity: "raro",
        acquisition: "Crafting (rol Guardián)",
        recipe: {
            type: "shapeless",
            ingredients: ["Shield", "Iron Ingot", "Heart of the Sea"],
            result: "Sígilo de Guardia"
        }
    },
    {
        name: "Brújula de Ruta",
        type: "Consumible de Rol",
        desc: "Velocidad II 40s + Visión Nocturna. Solo El Explorador.",
        rarity: "raro",
        acquisition: "Crafting (rol Explorador)",
        recipe: {
            type: "shapeless",
            ingredients: ["Map", "Feather", "Sweet Berries"],
            result: "Brújula de Ruta"
        }
    },
    {
        name: "Mezcla de Especias",
        type: "Consumible de Rol",
        desc: "Saturación + Regeneración corta. Solo El Cocinero.",
        rarity: "raro",
        acquisition: "Crafting (rol Cocinero)",
        recipe: {
            type: "shapeless",
            ingredients: ["Bowl", "Carrot", "Potato", "Beetroot"],
            result: "Mezcla de Especias"
        }
    },

    // --- VANILLA (ROLL DIARIO) ---
    { name: "Pan (x5)", type: "Material", desc: "Comida básica.", rarity: "comun", acquisition: "Roll Diario" },
    { name: "Lingote de Hierro (x3)", type: "Material", desc: "Material de crafteo.", rarity: "comun", acquisition: "Roll Diario" },
    { name: "Diamante", type: "Material", desc: "Joya preciosa.", rarity: "raro", acquisition: "Roll Diario / Minería" },
    { name: "Manzana Dorada", type: "Consumible", desc: "Absorción y Regeneración.", rarity: "raro", acquisition: "Roll Diario / Crafting" },
    { name: "Fragmento de Netherite", type: "Material", desc: "Material antiguo.", rarity: "epico", acquisition: "Roll Diario / Minería" },
    { name: "Totem de la Inmortalidad", type: "Consumible", desc: "Evita la muerte una vez.", rarity: "epico", acquisition: "Roll Diario / Evokers" },
    { name: "Lingote de Netherite", type: "Material", desc: "El metal más fuerte.", rarity: "legendario", acquisition: "Roll Diario / Crafting" },
    { name: "Manzana de Notch", type: "Consumible", desc: "Efectos poderosos de regeneración y resistencia.", rarity: "legendario", acquisition: "Roll Diario" }
];

export const serverRules = [
    { title: "Casco de Rayos X", desc: "El Casco de Visión Verdadera (xray) solo marca bloques a 6 bloques de distancia. Más lejos no muestra nada." },
    { title: "Progresión de Armadura", desc: "Día 1 solo hierro. Día 2 puedes usar 1 pieza de diamante, Día 3 dos, Día 4 tres, Día 5 ya las cuatro. Desde el Día 5 se habilita netherita de forma progresiva: 1 pieza en Día 5, +1 por día hasta Día 8." },
    { title: "Dormir Rápido", desc: "Solo se necesita el 30% de los jugadores conectados durmiendo para saltar la noche." },
    { title: "Buff de Equipos Grandes", desc: "Equipos de 4-5 miembros obtienen 5% menos daño por cada miembro vivo y conectado (hasta 25%). La XP que gana cada uno reparte 5% a cada compañero online." },
    { title: "Cofre de Muerte", desc: "Al morir, tu loot aparece en un cofre en el lugar de la muerte (salvo que esté activo 'Mundo Gigante')." },
    { title: "Vida y Muerte", desc: "Empiezas con 3 vidas. Al llegar a 0, eres espectador hasta que alguien te reviva. El día 31 es PERMADEATH." },
    { title: "Acumulación", desc: "Los efectos de la ruleta NO se reinician. Se acumulan. Adáptate o muere." },
    { title: "Griefing Táctico", desc: "El griefing está permitido SOLO si hay guerra declarada o evento de 'Purga'. Bases desconectadas son seguras." },
    { title: "Fair Play", desc: "Cero hacks (X-Ray, KillAura, etc). Uso de bugs está prohibido salvo que la ruleta lo permita." },
    { title: "Respeto", desc: "El toxicidad extrema o ataques personales resultan en ban directo sin gastar vidas." },
    { title: "Alianzas", desc: "Equipos de máximo 5 personas. Las traiciones están permitidas, pero tienen consecuencias sociales." },
    { title: "Stream Sniping", desc: "Prohibido. Usar información de streams para matar es ban." },
    { title: "Eventos", desc: "La asistencia a la 'Hora de la Ruleta' (00:00 server time) es obligatoria si estás online." },
];

// Reglas fijas por día (1-31)
export const dayRules = [
    { day: 1, name: "Calma Inicial", desc: "Inicio sin cambios adicionales." },
    { day: 2, name: "Golpes Más Fuertes I", desc: "+2% daño de mobs hostiles." },
    { day: 3, name: "Golpes Más Fuertes II", desc: "+2% daño de mobs hostiles." },
    { day: 4, name: "Golpes Más Fuertes III", desc: "+2% daño de mobs hostiles." },
    { day: 5, name: "Toque de Queda Suave", desc: "60% de jugadores para saltar la noche." },
    { day: 6, name: "Golpes Más Fuertes IV", desc: "+2% daño de mobs hostiles." },
    { day: 7, name: "Golpes Más Fuertes V", desc: "+2% daño de mobs hostiles." },
    { day: 8, name: "Golpes Más Fuertes VI", desc: "+2% daño de mobs hostiles." },
    { day: 9, name: "Golpes Más Fuertes VII", desc: "+2% daño de mobs hostiles." },
    { day: 10, name: "Noche en Serio", desc: "100% para dormir y arañas con 1-2 efectos." },
    { day: 11, name: "Golpes Más Fuertes VIII", desc: "+2% daño de mobs hostiles." },
    { day: 12, name: "Patrullas Dobles", desc: "Aparece un mob extra por spawn hostil." },
    { day: 13, name: "Golpes Más Fuertes IX", desc: "+2% daño de mobs hostiles." },
    { day: 14, name: "Loot Racionado I", desc: "Sin loot de Blaze, Bruja, Enderman y Ghast." },
    { day: 15, name: "No se Duerme", desc: "Dormir deja de saltar la noche." },
    { day: 16, name: "Golpes Más Fuertes X", desc: "+2% daño de mobs hostiles." },
    { day: 17, name: "Arañas Potenciadas II", desc: "Arañas con 2-3 efectos." },
    { day: 18, name: "Loot Racionado II", desc: "Sin loot de Wither Skeleton, Guardian, Magma Cube y Drowned." },
    { day: 19, name: "Ravagers Codiciosos I", desc: "3% de probabilidad de tótem en Ravager." },
    { day: 20, name: "Patrullas Triples", desc: "Se suma otro mob extra por spawn hostil." },
    { day: 21, name: "Golpes Más Fuertes XI", desc: "+2% daño de mobs hostiles." },
    { day: 22, name: "Golpes Más Fuertes XII", desc: "+2% daño de mobs hostiles." },
    { day: 23, name: "Ravagers Codiciosos II", desc: "6% de probabilidad de tótem en Ravager." },
    { day: 24, name: "Arañas Potenciadas III", desc: "Arañas con 3 efectos garantizados." },
    { day: 25, name: "Golpes Más Fuertes XIII", desc: "+2% daño de mobs hostiles." },
    { day: 26, name: "Loot Racionado III", desc: "Sin loot de Piglin Zombie, Slime ni Golem de Hierro." },
    { day: 27, name: "Golpes Más Fuertes XIV", desc: "+2% daño de mobs hostiles." },
    { day: 28, name: "Ravagers Codiciosos III", desc: "8% de probabilidad de tótem en Ravager." },
    { day: 29, name: "Golpes Más Fuertes XV", desc: "+2% daño de mobs hostiles." },
    { day: 30, name: "Insomnio Total", desc: "Dormir bloqueado por completo." },
    { day: 31, name: "Golpes Más Fuertes XVI", desc: "+2% daño de mobs hostiles." },
];

// --- CONFIGURACIÓN MANUAL ---
// Agrega aquí los nombres EXACTOS de los eventos que quieres mostrar en la pestaña "Activos"
export const activeModifiersConfig: string[] = [
    // Ejemplo: "Sol Tóxico", "Corazón de Titán"
];

export const tutorials = [
    {
        id: "teams",
        title: "Sistema de Equipos",
        icon: "🛡️",
        content: [
            { subtitle: "Creación y Gestión", text: "Usa /team create <nombre> para fundar tu equipo. Invita jugadores con /team invite <jugador>. El límite es de 5 jugadores por equipo." },
            { subtitle: "Fuego Amigo", text: "Por defecto, no puedes dañar a tus compañeros de equipo. Esto evita accidentes con espadas o arcos durante peleas." },
            { subtitle: "Chat de Equipo", text: "Usa /team chat para alternar entre el chat global y el chat privado de tu equipo." },
            { subtitle: "Abandonar", text: "Si deseas salir, usa /team leave. Si eres el líder, el equipo se disolverá si no pasas el liderazgo antes." }
        ]
    },
    {
        id: "war",
        title: "Guerra y PvP",
        icon: "⚔️",
        content: [
            { subtitle: "Declarar Guerra", text: "Los líderes de equipo pueden declarar la guerra a otros equipos usando /team war <equipo>. Esto habilita el PvP sin restricciones entre esos equipos." },
            { subtitle: "Consecuencias", text: "Durante una guerra, tu ubicación será revelada (efecto Glowing) si te acercas a tus enemigos. ¡No podrás esconderte!" },
            { subtitle: "Rendición", text: "Una guerra termina cuando un equipo se rinde o es eliminado completamente." }
        ]
    },
    {
        id: "protection",
        title: "Protección de Bloques",
        icon: "🏰",
        content: [
            { subtitle: "Bloque de Protección", text: "Coloca un Bloque y sera protegido automaticamente por tu o tu team." },
            { subtitle: "Permisos", text: "Tus compañeros de equipo tienen acceso automático a tus protecciones." },
            { subtitle: "Vulnerabilidad", text: "Las protecciones NO funcionan durante eventos de 'Purga', pero siguen activas durante guerras." }
        ]
    },
    {
        id: "combat_log",
        title: "Sistema Combat Log",
        icon: "⏱️",
        content: [
            { subtitle: "Marcado en Combate", text: "Golpear o recibir daño te marca durante el tiempo configurado. El temporizador se reinicia con cada interacción de combate." },
            { subtitle: "Penalización", text: "Si te desconectas mientras sigues marcado, el sistema te mata automáticamente al volver o inmediatamente si el servidor detecta la salida." },
            { subtitle: "Avisos", text: "Recibirás un mensaje al entrar y salir del estado de combate para que puedas actuar con claridad." },
            { subtitle: "Configuración", text: "Ajusta la sección 'combat-log' del config.yml para definir duración, avisos y si deseas castigos adicionales." }
        ]
    },
    {
        id: "reanimation",
        title: "Reanimación Cooperativa",
        icon: "💉",
        content: [
            { subtitle: "Estado Downed", text: "Al llegar a 0 corazones sin permadeath activo entras en estado de incapacitado con un temporizador de desangrado." },
            { subtitle: "Canal de Reanimación", text: "Un compañero cercano puede iniciar la reanimación (agachado si se requiere). Otros jugadores pueden unirse en cualquier momento." },
            { subtitle: "Velocidad Compartida", text: "Cada rescatador extra reduce en 20% el tiempo restante del canal, acelerando la recuperación." },
            { subtitle: "Interrupciones y Arrastre", text: "Si nadie mantiene el canal, sangras hasta morir. Opcionalmente se puede arrastrar al aliado antes de curarlo si la configuración lo permite." }
        ]
    },
    {
        id: "daily_roll",
        title: "Roll Diario",
        icon: "🎲",
        content: [
            { subtitle: "Comando", text: "Usa /daily una vez cada 24 horas para obtener una recompensa aleatoria." },
            { subtitle: "Probabilidades", text: "65% Común, 20% Raro, 12% Épico, 3% Legendario." },
            { subtitle: "Premios", text: "Puedes obtener desde materiales básicos (Pan, Hierro) hasta objetos Legendarios únicos o Netherite. ¡Prueba tu suerte!" }
        ]
    },
    {
        id: "killpoints",
        title: "Killpoints y KillStore",
        icon: "💀",
        content: [
            { subtitle: "Cómo se consiguen", text: "Cada baja de jugador otorga 1 killpoint si el sistema está activo (Semana 2)." },
            { subtitle: "Consulta y uso", text: "Abre la tienda con /killstore para ver tu saldo y comprar. (Semana 3)." },
            { subtitle: "Catálogo destacado", text: "Totem 8kp, Manzana Encantada 5kp, Manzana de Vida 12kp, Orbe de Resurrección 18kp, Corazón de Notch 22kp. También packs vanilla (perlas, cohetes, flechas, lingote de netherita)." },
            { subtitle: "Progresión de gear sin enchants", text: "Día 10: Set + Espada de Acero (atributos base). Día 20: Set + Espada Obsidiana (más dureza). Día 30: Set + Espada del Vacío (armadura extra y resistencia al knockback). Recetas bloqueadas hasta su día y también disponibles en la KillStore desde esa fecha." },
            { subtitle: "Notas", text: "El saldo es por jugador y persiste entre reinicios. Los admins pueden cortar el sistema temporalmente sin perder tus puntos." }
        ]
    }
];
