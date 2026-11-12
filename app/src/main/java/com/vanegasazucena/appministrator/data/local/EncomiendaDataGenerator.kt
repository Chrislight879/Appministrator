package com.vanegasazucena.appministrator.data.local

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

object EncomiendaDataGenerator {

    private val nombres = listOf(
        "Carlos Mendoza", "Maria Lopez", "Jose Garcia", "Ana Martinez",
        "Pedro Hernandez", "Laura Sanchez", "Miguel Torres", "Sofia Ramirez",
        "Diego Flores", "Camila Rivera", "Andres Morales", "Valentina Castro",
        "Fernando Ortiz", "Isabella Nunez", "Roberto Vargas", "Daniela Rojas",
        "Luis Jimenez", "Paula Reyes", "Jorge Aguilar", "Mariana Ruiz",
        "Eduardo Diaz", "Gabriela Pena", "Ricardo Cruz", "Natalia Medina",
        "Alejandro Gomez", "Evelyn Castaneda", "Oscar Herrera", "Lucia Gutierrez",
        "Enrique Paredes", "Monica Delgado"
    )

    private val estados = listOf("PAGADO", "PENDIENTE", "ENTREGADO")
    private val tamanos = listOf("LIVIANO", "MEDIANO", "GRANDE")
    private val creadores = listOf("admin", "PeterParker", "JuanPerez")

    fun generate300Encomiendas(): List<EncomiendaEntity> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val list = mutableListOf<EncomiendaEntity>()

        for (i in 1..300) {
            val code = "ENC-${String.format("%05d", i)}"
            val emisor = nombres[Random.nextInt(nombres.size)]
            val remitente = nombres[Random.nextInt(nombres.size)]
            val emisorDui = "${Random.nextInt(10000000, 99999999)}-${Random.nextInt(0, 9)}"
            val remitenteDui = "${Random.nextInt(10000000, 99999999)}-${Random.nextInt(0, 9)}"
            val estado = estados[Random.nextInt(estados.size)]
            val tamano = tamanos[Random.nextInt(tamanos.size)]
            val precio = when (tamano) {
                "LIVIANO" -> Random.nextDouble(3.0, 10.0)
                "MEDIANO" -> Random.nextDouble(10.0, 25.0)
                "GRANDE" -> Random.nextDouble(25.0, 60.0)
                else -> 5.0
            }
            val creator = creadores[Random.nextInt(creadores.size)]

            calendar.add(Calendar.HOUR, -Random.nextInt(1, 48))
            val fecha = dateFormat.format(calendar.time)

            list.add(
                EncomiendaEntity(
                    id = code,
                    emisorNombre = emisor,
                    emisorDui = emisorDui,
                    remitenteNombre = remitente,
                    remitenteDui = remitenteDui,
                    creadoPor = creator,
                    codigo = code,
                    estado = estado,
                    tamano = tamano,
                    precio = String.format("%.2f", precio).toDouble(),
                    fecha = fecha,
                    timestampMillis = calendar.timeInMillis
                )
            )
        }

        return list
    }
}
