# Tiendas con cartel

Guía rápida para crear y usar las tiendas vinculadas a cofres, barriles o shulkers.

## Crear una tienda
- Coloca un cartel con `[tienda]` pegado directamente al contenedor que guardará el stock.
- Sigue el asistente en chat: escribe `ok` con el item a vender en mano, elige la cantidad por venta, luego `ok` con el item de pago (puedes agregar un número para la cantidad a cobrar) y confirma.
- El cartel se bloquea y queda asociado al contenedor; solo el dueño puede gestionarlo.

## Comprar
- Haz clic en el cartel y elige cuántos paquetes quieres (1, 10, 32, 64 o cualquier número).
- Debes tener el pago exacto. Se consumen únicamente los ítems que coincidan con el descriptor de pago.
- Si hay stock suficiente, recibes exactamente los ítems que estaban en el contenedor (se respetan encantamientos y daño acumulado).

## Cobrar y borrar
- El dueño al clicar ve opciones: `retirar` paga todo lo acumulado; `borrar` elimina la tienda (antes cobra los pagos pendientes).
- Los pagos se guardan como los ítems exactos entregados por compradores.

## Coincidencia de ítems y reabastecimiento
- Coincidencia estricta por material + encantamientos + `custom_item_id` (si existe). Otros ítems en el contenedor se ignoran.
- Puedes reabastecer con tolvas u otros sistemas automatizados mientras los ítems que entren cumplan el descriptor.
- Metadatos fuera de esos campos (p. ej. desgaste) no afectan la coincidencia, pero el ítem entregado mantiene su estado real.
