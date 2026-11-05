package com.vanegasazucena.appministrator.model

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val isDeleted: Int = 0
)
