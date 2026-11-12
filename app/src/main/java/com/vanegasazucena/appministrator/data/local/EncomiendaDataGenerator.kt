package com.vanegasazucena.appministrator.data.local

/** Datos ficticios únicamente para instalaciones nuevas y demostraciones. */
object EncomiendaDataGenerator {
    fun generate300Encomiendas(): List<EncomiendaEntity> = (1..300).map { numero ->
        val id = "ENC-${numero.toString().padStart(5, '0')}"
        EncomiendaEntity(
            id = id,
            emisorNombre = "Emisor de ejemplo $numero",
            emisorDui = "",
            remitenteNombre = "Destinatario de ejemplo $numero",
            remitenteDui = "",
            creadoPor = "admin",
            codigo = "", // AppRepository asigna el código seguro y único.
            estado = "PENDIENTE",
            tamano = "LIVIANO",
            precio = 3.0,
            fecha = "Ejemplo"
        )
    }
}
