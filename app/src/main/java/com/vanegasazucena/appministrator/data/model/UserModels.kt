package com.vanegasazucena.appministrator.data.model

enum class UserRole(val label: String) {
    ADMIN("Administrador"),
    COMMON("Común")
}

data class User(
    val username: String,
    val role: UserRole
)
