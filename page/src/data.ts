export const dailyEvents = [
    // --- MALDICIONES (CURSES) ---
    { name: "Sol Tóxico", type: "Maldición", desc: "La luz directa del sol causa lentitud y hambre.", icon: "☀️" },
    { name: "Sol Abrasador", type: "Maldición", desc: "El sol quema a los jugadores expuestos.", icon: "🌞" },
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
    { name: "El Pacifista", pros: "Regeneración II 5s (se renueva cada 4s).", cons: "No puede infligir daño directo (ataques cancelados).", icon: "🕊️" },
    { name: "El Vampiro", pros: "En oscuridad/noche: Fuerza I 2s + Visión Nocturna 12s + Velocidad I; golpes en oscuridad roban 15% del daño como curación.", cons: "Con sol directo: se quema 3s por tick o casco pierde 3 de durabilidad/s.", icon: "🦇" },
    { name: "Glass Cannon", pros: "Todo el daño que inflige se duplica (x2).", cons: "Daño recibido x2 y vida máxima 5 corazones.", icon: "💥" },
    { name: "Rey Midas", pros: "Hierro/Cobre recogido se vuelve oro; 10% de piedra/cobble en pepitas; pepitas casi no regeneran, lingotes regeneran más y los bloques dan Regeneración II + hasta +4 corazones temporales (requieren seguir consumiendo bloques).", cons: "Cada 10s debe consumir oro (bloques, lingotes o pepitas) o pierde 1 corazón; sin regeneración natural.", icon: "👑" },
    { name: "El Nómada", pros: "Velocidad II 2s (refresco cada 1s) mientras no repita cama.", cons: "Dormir 2 veces en la misma cama quita la velocidad y hace explotar la cama.", icon: "⛺" },
    { name: "El Tanque", pros: "Resistencia I y 40 de vida (20 corazones), refresco cada 4s.", cons: "Lentitud II permanente y el hambre se consume x3 más rápido.", icon: "🛡️" },
    { name: "El Asesino", pros: "Invisibilidad al agacharse; backstab duplica daño si el objetivo mira igual dirección.", cons: "Puede usar armadura de hierro o superior.", icon: "🗡️" },
    { name: "El Ingeniero", pros: "Prisa II y Suerte II 5s (se renuevan cada 4s).", cons: "Lentitud I permanente.", icon: "🔧" },
    { name: "El Pirotécnico", pros: "Resistencia al Fuego 5s (cada 4s); golpes prenden 5s.", cons: "Recibe daño al tocar agua o bajo la lluvia.", icon: "🧨" },
    { name: "El Maldito", pros: "50% de probabilidad de que mobs lo ignoren al fijar objetivo.", cons: "Mala Suerte V 5s (renovada cada 4s).", icon: "💀" },
    { name: "El Druida", pros: "En forest/jungle/taiga: Regeneración I + Velocidad I 5s (cada 3s); animales lo siguen (10x5x10).", cons: "No puede usar armadura de metal.", icon: "🌿" },
    { name: "El Berserker", pros: "+10% daño por cada 2 corazones faltantes (acumulativo).", cons: "No puede usar escudos (se retiran del offhand).", icon: "🪓" },
    { name: "El Francotirador", pros: "Flechas a >20 bloques hacen x1.5 daño (aviso al tirador).", cons: "Daño cuerpo a cuerpo reducido 50%.", icon: "🎯" },
    { name: "El Mercader", pros: "Héroe de la Aldea III + Suerte (refresco 4s); 12% de chance de 1-2 esmeraldas extra al matar mobs; si está cerca, las ofertas de aldeanos son 25% más baratas.", cons: "Los mobs hostiles lo targetean primero en 12 bloques.", icon: "💰" },
    { name: "El Fantasma", pros: "Puede abrir puertas (incluye hierro) al agacharse; invisibilidad permanente; los mobs solo te detectan 30% de las veces.", cons: "Vida máxima 10 (5 corazones).", icon: "👻" },
    { name: "El Acuático", pros: "Respiración acuática permanente; en agua: Gracia del Delfín I + Poder del Canal I 2s (cada 1s).", cons: "Recibe daño si está fuera de agua o lluvia.", icon: "🧜" },
    { name: "El Minero", pros: "Prisa II 5s (cada 4s); Visión Nocturna 12s bajo Y<0.", cons: "Ceguera en la superficie durante el día.", icon: "⛏️" },
    { name: "El Domador", pros: "Mascotas infligen x2 daño y solo reciben 50% del daño; la otra mitad va al dueño. Cada mascota cercana reduce el daño al dueño con rendimiento decreciente hasta 50% total.", cons: "Recibes la mitad del daño que sufran tus mascotas.", icon: "🐕" },
    { name: "El Alquimista", pros: "Pociones bebida/splash triplican su duración manteniendo nivel.", cons: "Efectos negativos también duran x3.", icon: "⚗️" },
    { name: "El Caballero", pros: "+30% daño con espada y bloquear reduce 15% extra el daño.", cons: "-50% daño con arco y no puede usar arcos/crossbows/tridentes.", icon: "⚔️" },
    { name: "El Ladrón", pros: "Velocidad II 5s (cada 4s); puede robar un slot al hacer sneak + clic derecho (CD 5 min).", cons: "Suelta el ítem de su mano al recibir daño.", icon: "🦝" },
    { name: "El Gigante", pros: "Vida máxima 24 (12 corazones); modelo x1.25 si está disponible; 35% resistencia al empuje y reduce 25% el daño de caída causando daño de impacto a cercanos.", cons: "Hitbox más grande (fácil de golpear) y el hambre baja más rápido.", icon: "🦍" },
    { name: "El Enano", pros: "Modelo x0.75 si está disponible; Velocidad I, Fuerza I, Prisa I y 10% resistencia al empuje.", cons: "Vida máxima 16 (8 corazones), alcance reducido y colocar bloques es más difícil por la altura.", icon: "🐁" },
    { name: "El Ilusionista", pros: "25% de esquivar: cancela golpe e Invisibilidad 3s con partículas.", cons: "No puede comer carne (consumo cancelado).", icon: "🎭" },
    { name: "El Bárbaro", pros: "+50% daño cuerpo a cuerpo.", cons: "No puede usar netherite ni encantar objetos.", icon: "🍖" },
    { name: "El Sabio", pros: "Experiencia ganada x2 y al ganar XP obtiene Regeneración I 3s.", cons: "Debilidad I permanente (refrescada cada 4s).", icon: "📚" },
    { name: "El Caótico", pros: "Cada 5 min recibe un buff aleatorio; al golpear tiene 20% de buff propio y 30% de aplicar debuff aleatorio al objetivo (5s nivel I).", cons: "Su caos es impredecible.", icon: "🎲" },
    { name: "El Guardián", pros: "Resistencia I 2s propia; aliados a 5 bloques reciben Resistencia I + Regeneración I 2s (cada 1s).", cons: "Redirige 30% del daño de aliados a 5 bloques hacia sí mismo.", icon: "🏰" },
    { name: "El Explorador", pros: "Velocidad II + Resistencia I 2s (cada 1s); 30% menos daño de caída; puede leer coords exactas de mapas del tesoro al clic derecho; no pierde hambre al esprintar.", cons: "Última fila del inventario bloqueada y vaciada si la llena.", icon: "🧭" },
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
// Los ítems ahora se sirven desde el backend (/recipes). Se deja vacío para evitar drift.
export const items: any[] = [];

export const serverRules = [
    { title: "Casco de Rayos X", desc: "El Casco de Visión Verdadera (xray) solo marca bloques a 6 bloques de distancia. Más lejos no muestra nada." },
    { title: "Progresión de Armadura", desc: "Día 1 solo hierro. Día 2 puedes usar 1 pieza de diamante, Día 3 dos, Día 4 tres, Día 5 ya las cuatro. Desde el Día 5 se habilita netherita de forma progresiva: 1 pieza en Día 5, +1 por día hasta Día 8." },
    { title: "Apertura de Dimensiones", desc: "El Nether se desbloquea el Día 7. El End se desbloquea el Día 14." },
    { title: "Dormir Rápido", desc: "Solo se necesita el 30% de los jugadores conectados durmiendo para saltar la noche." },
    { title: "Buff de Equipos Grandes", desc: "Equipos de 4-5 miembros obtienen 5% menos daño por cada miembro vivo y conectado (hasta 25%). La XP que gana cada uno reparte 5% a cada compañero online." },
    { title: "Cofre de Muerte", desc: "Al morir, tu loot aparece en un cofre en el lugar de la muerte (salvo que esté activo 'Mundo Gigante')." },
    { title: "Vida y Muerte", desc: "Empiezas con 3 vidas. Al llegar a 0, eres espectador hasta que alguien te reviva. El día 31 es PERMADEATH." },
    { title: "Acumulación", desc: "Los efectos de la ruleta NO se reinician. Se acumulan. Adáptate o muere." },
    { title: "Rotación de Mobs", desc: "Cada día se desbloquea un nuevo Mob Custom que se añade a la lista de spawns. Estos mobs reemplazan a sus versiones vanilla con cierta probabilidad (100% comunes, 25% raros, 2% bosses)." },
    { title: "Griefing Táctico", desc: "El griefing está permitido SOLO si hay guerra declarada o evento de 'Purga'. Bases desconectadas son seguras." },
    { title: "Fair Play", desc: "Cero hacks (X-Ray, KillAura, etc). Uso de bugs está prohibido salvo que la ruleta lo permita." },
    { title: "Respeto", desc: "El toxicidad extrema o ataques personales resultan en ban directo sin gastar vidas." },
    { title: "Alianzas", desc: "Equipos de máximo 5 personas. Las traiciones están permitidas, pero tienen consecuencias sociales." },
    { title: "Stream Sniping", desc: "Prohibido. Usar información de streams para matar es ban." },
    { title: "Eventos", desc: "La asistencia a la 'Hora de la Ruleta' (00:00 server time) es obligatoria si estás online." },
    { title: "Batalla del End", desc: "El End es zona de alto peligro. Endermans pueden ser reemplazados por Caminantes del Vacío. Al matar al Dragón, sus guardianes aparecerán para proteger el huevo." },
    { title: "Cristales del End", desc: "Los End Crystals tienen 3 vidas (requieren 3 golpes para romperse)." },
];

// Reglas fijas por día (1-31)
export const dayRules = [
    { day: 1, name: "Calma Inicial", desc: "Inicio sin cambios adicionales." },
    { day: 2, name: "Golpes Más Fuertes I", desc: "+2% daño de mobs hostiles." },
    { day: 3, name: "Golpes Más Fuertes II", desc: "+2% daño de mobs hostiles." },
    { day: 4, name: "Golpes Más Fuertes III", desc: "+2% daño de mobs hostiles." },
    { day: 5, name: "Golpes Más Fuertes IV", desc: "+2% daño de mobs hostiles." },
    { day: 6, name: "Patrullas Triples", desc: "Se suma otro mob extra por spawn hostil." },
    { day: 7, name: "Golpes Más Fuertes V", desc: "+2% daño de mobs hostiles." },
    { day: 8, name: "Golpes Más Fuertes VI", desc: "+2% daño de mobs hostiles." },
    { day: 9, name: "Golpes Más Fuertes VII", desc: "+2% daño de mobs hostiles." },
    { day: 10, name: "Noche en Serio", desc: "100% para dormir y arañas con 1-2 efectos." },
    { day: 11, name: "Golpes Más Fuertes VIII", desc: "+2% daño de mobs hostiles." },
    { day: 12, name: "Patrullas Dobles", desc: "Aparece un mob extra por spawn hostil." },
    { day: 13, name: "Golpes Más Fuertes IX", desc: "+2% daño de mobs hostiles." },
    { day: 14, name: "Toque de Queda Suave", desc: "60% de jugadores para saltar la noche." },
    { day: 15, name: "No se Duerme", desc: "Dormir deja de saltar la noche." },
    { day: 16, name: "Golpes Más Fuertes X", desc: "+2% daño de mobs hostiles." },
    { day: 17, name: "Arañas Potenciadas II", desc: "Arañas con 2-3 efectos." },
    { day: 18, name: "Loot Racionado II", desc: "Sin loot de Wither Skeleton, Guardian, Magma Cube y Drowned." },
    { day: 19, name: "Ravagers Codiciosos I", desc: "3% de probabilidad de tótem en Ravager." },
    { day: 20, name: "Loot Racionado I", desc: "Sin loot de Blaze, Bruja, Enderman y Ghast." },
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
        id: "hunters",
        title: "Cazadores y Recompensas",
        icon: "🏹",
        content: [
            { subtitle: "Comando Principal", text: "Usa /cazadores para abrir el panel de recompensas activas." },
            { subtitle: "Publicar Recompensa", text: "Agrega un premio (ej. 10 diamantes) por la cabeza de un jugador. Debes tener el premio en tu inventario; queda retenido hasta que alguien cobre." },
            { subtitle: "Cobro Automático", text: "Quien mate al objetivo y entregue la cabeza en /cazadores cobra toda la recompensa acumulada al instante." },
            { subtitle: "Notas", text: "Las recompensas se acumulan; cualquiera puede aportar. Puedes cancelar para recuperar tu premio si nadie ha cobrado. Respeta las reglas de guerra/combate vigentes." }
        ]
    },
    {
        id: "shops",
        title: "Tiendas de Cartel",
        icon: "🏪",
        content: [
            { subtitle: "Crear tienda", text: "Coloca un cartel [tienda] pegado a un cofre/barril/shulker. Sigue el chat: ok con el item a vender en mano, elige cantidad, ok con el item de pago (puedes añadir un número), confirma." },
            { subtitle: "Comprar", text: "Haz clic en el cartel y escribe cuántos paquetes quieres (1/10/32/64 o número). Necesitas el pago exacto; solo se consumen ítems que coincidan." },
            { subtitle: "Cobrar o borrar", text: "El dueño al clicar ve opciones: retirar paga todo lo acumulado; borrar elimina la tienda y devuelve los pagos pendientes." },
            { subtitle: "Coincidencia y stock", text: "Solo coinciden material + encantamientos + custom_item_id. Otros ítems en el contenedor se ignoran; puedes reabastecer con tolvas mientras coincidan." }
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
    },
    {
        id: "starter_kit",
        title: "Kit Inicial",
        icon: "📦",
        content: [
            { subtitle: "Armadura", text: "Set completo de Hierro con Protección II." },
            { subtitle: "Armas", text: "Espada de Hierro con Afilado II." },
            { subtitle: "Suministros", text: "64 Panes para empezar tu aventura." },
            { subtitle: "Bonus", text: "2 Recompensas del Roll Diario gratis al entrar por primera vez." }
        ]
    },
    {
        id: "monetization",
        title: "Tienda y Soporte",
        icon: "💎",
        content: [
            { subtitle: "Link de la tienda", text: "Apoya el host y consigue permisos de conveniencia en la Tebex oficial: https://roll-and-death-smp.tebex.io/" },
            { subtitle: "¿Qué se vende?", text: "Permisos de comodidad (backpack, ender ampliado, mesas portátiles) y cosméticos (particle trails). Nada pay-to-win." },
            { subtitle: "Por qué", text: "Cada compra ayuda a cubrir el costo del host (~23 USD/mes). ¡Gracias por apoyar el servidor!" }
        ]
    }
];
