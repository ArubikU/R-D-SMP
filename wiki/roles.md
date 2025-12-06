# Roles y efectos (código real)

Valores calculados directamente del código actual (20 ticks = 1 segundo). Amplificador 0 = nivel I, 1 = nivel II, etc.

## Pacifista
- Pros: Regeneración I por 5s, se renueva cada 4s.
- Contras: No puede dañar a otros jugadores; los golpes se cancelan con aviso.

## Vampiro
- Pros: Fuerza I por 2s y Visión Nocturna por 12s cuando es de noche, llueve o no hay luz solar directa.
- Contras: Si es de día, no llueve y la luz del cielo >= 12: se prende fuego 3s por tick o daña el casco en 3 de durabilidad por segundo si lleva uno.

## Glass Cannon
- Pros: Todo el daño que inflige se duplica.
- Contras: Todo el daño que recibe también se duplica y la vida máxima es 3 corazones.

## Rey Midas
- Pros: Convierte hierro/cobre recogido en lingotes de oro; 10% de cobble/piedra se convierte en pepitas.
- Contras: Cada 10 min consume 1 pepita o lingote de oro; si no tiene, recibe 2 corazones de daño y no regenera vida de forma natural.

## Nómada
- Pros: Velocidad II por 2s (renueva cada 1s) mientras no tenga el debuff.
- Contras: Dormir dos veces en la misma cama elimina la velocidad y provoca una explosión en la cama.

## Tanque
- Pros: Resistencia I y Vida máxima 40 (20 corazones), se refresca cada 4s.
- Contras: Lentitud II permanente (renovada cada 4s).

## Asesino
- Pros: Invisibilidad mientras se agacha; puñalada por la espalda duplica daño si el objetivo mira en dirección similar.
- Contras: Solo puede usar armadura de cuero.

## Ingeniero
- Pros: Prisa II y Suerte II por 5s, se renuevan cada 4s.
- Contras: Lentitud I permanente.

## Pirotécnico
- Pros: Resistencia al Fuego por 5s (se renueva cada 4s) y sus golpes prenden fuego 5s.
- Contras: Recibe daño al tocar agua o bajo la lluvia.

## Maldito
- Pros: 50% de probabilidad de que mobs lo ignoren al fijar objetivo.
- Contras: Mala Suerte V por 5s renovada cada 4s.

## Druida
- Pros: En biomas forest/jungle/taiga recibe Regeneración I y Velocidad I por 5s (cada 3s). Atrae animales en radio 10x5x10.
- Contras: No puede usar armadura de metal.

## Berserker
- Pros: +10% de daño por cada 2 corazones faltantes (acumula según vida perdida).
- Contras: No puede usar escudos (se le retiran del offhand).

## Francotirador
- Pros: Flechas que golpean a >20 bloques hacen x1.5 daño y notifican al tirador.
- Contras: Daño cuerpo a cuerpo reducido 50%.

## Mercader
- Pros: Héroe de la Aldea II por 5s (cada 4s). 5% de probabilidad de soltar una esmeralda extra al matar mobs.
- Contras: Los mobs hostiles lo priorizan como objetivo cercano.

## Fantasma
- Pros: Puede abrir puertas (incluye hierro) al agacharse.
- Contras: Vida máxima 10 (5 corazones).

## Acuático
- Pros: Respiración acuática permanente. En agua: Gracia del Delfín I y Poder del Canal I por 2s (cada 1s).
- Contras: Recibe daño si está fuera de agua o lluvia.

## Minero
- Pros: Prisa II por 5s (cada 4s). Visión Nocturna 12s cuando está por debajo de Y 0.
- Contras: Ceguera mientras esté en la superficie durante el día.

## Domador
- Pros: Mascotas infligen x2 daño y reciben x0.5 daño.
- Contras: Comparte parte del daño recibido con sus mascotas cercanas.

## Alquimista
- Pros: Pociones bebidas o splash duplican su duración manteniendo nivel.
- Contras: Todos los efectos, incluidos los negativos, duran x3.

## Caballero
- Pros: +30% daño con espadas.
- Contras: -50% daño con arco y no puede usar arcos ni tridentes.

## Ladrón
- Pros: Velocidad II por 5s (cada 4s). Puede robar un slot aleatorio al hacer sneak + click derecho a un jugador.
- Contras: Cooldown de robo 5 min; aviso a víctima y ladrón; suelta el ítem de la mano al recibir daño.

## Gigante
- Pros: Vida máxima 40 (20 corazones). Escala del modelo x2 si el atributo está disponible.
- Contras: El hambre se consume al doble de velocidad.

## Enano
- Pros: Escala del modelo x0.5 si el atributo está disponible.
- Contras: Vida máxima 16 (8 corazones).

## Ilusionista
- Pros: 25% de probabilidad de esquivar daño; cancela el golpe, aplica Invisibilidad 3s y partículas.
- Contras: No puede comer carne (consume se cancela).

## Bárbaro
- Pros: +50% daño cuerpo a cuerpo.
- Contras: No puede equipar armadura de diamante o netherite y no puede encantar objetos.

## Sabio
- Pros: Experiencia ganada x2.
- Contras: Debilidad I permanente.

## Caótico
- Pros: 30% de probabilidad de aplicar un debuff aleatorio (Veneno, Lentitud, Debilidad, Ceguera, Náusea, Wither, Levitación) por 5s nivel I.
- Contras: Recibe un efecto negativo aleatorio cada 10 minutos.

## Explorador
- Pros: Velocidad I por 2s (cada 1s). No pierde hambre al esprintar.
- Contras: Inventario reducido (bloquea la última fila de slots).

## Cocinero
- Pros: Al comer comida: +5 de saturación, Regeneración I 5s y Absorción I 60s.
- Contras: Si consume algo no comestible: Veneno IV por 30s; la carne cruda aplica Veneno II breve.

## Guardián
- Pros: Resistencia I 2s para el guardián; aliados a 5 bloques reciben Resistencia I + Regeneración I 2s, renovado cada 1s.
- Contras: Redirige el 30% del daño que reciben jugadores en un radio de 5 bloques hacia el guardián; ese daño lo sufre el guardián tras reducir el golpe del aliado.
