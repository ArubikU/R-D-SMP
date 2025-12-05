# Sistemas de Combat Log y Reanimación

## Combat Log
- **Activación:** El gestor de combat log marca a un jugador cuando realiza o recibe daño.
- **Duración del estado:** Durante el tiempo configurado, abandonar el servidor provoca la muerte automática del jugador.
- **Excepciones:** La muerte solo ocurre si el jugador sigue marcado. El tiempo se reinicia cada vez que vuelve a entrar en combate.
- **Mensajes:** Los jugadores reciben avisos cuando quedan marcados y cuando finaliza el estado de combate.
- **Configuración sugerida:** Ajusta la sección `combat-log` del `config.yml` del plugin para definir duración, avisos y penalizaciones.

## Reanimación
- **Estado de incapacitado:** Cuando un jugador llega a 0 de vida sin permadeath activo, entra en estado de "downed". Tiene un temporizador de desangrado configurable.
- **Reanimaciones múltiples:** Cualquier jugador puede iniciar la reanimación si cumple los requisitos (estar cerca y agachado si se solicita). Jugadores adicionales pueden unirse al proceso.
- **Velocidad escalable:** Cada rescatador extra reduce en un 20 % el tiempo restante del canal de reanimación, hasta completar la curación.
- **Interrupciones:** Moverse demasiado lejos, dejar de agacharse o desconectarse cancela la reanimación. Si nadie continúa, el jugador se desangra.
- **Arrastre:** Si la opción está habilitada, un rescatador puede cargar con el incapacitado para moverlo antes de reanimarlo.
- **Configuración sugerida:** Usa la sección `reanimation` del `config.yml` para ajustar tiempos de desangrado, duración del canal, daño adicional recibido y requisitos (agacharse, cargar, etc.).

## Recomendaciones Generales
- Revisa los mensajes que se envían a los jugadores para garantizar claridad y consistencia con la ambientación del servidor.
- Coordina los valores de `combat-log` y `reanimation` para equilibrar la dificultad del combate y la colaboración del equipo.
- Considera documentar casos especiales (eventos, roles o modificadores) que alteren estas mecánicas.
