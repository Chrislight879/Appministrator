package com.vanegasazucena.appministrator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vanegasazucena.appministrator.data.local.AppDatabase
import com.vanegasazucena.appministrator.data.local.AuditLogEntity
import com.vanegasazucena.appministrator.data.local.EncomiendaEntity
import com.vanegasazucena.appministrator.data.local.UserEntity
import com.vanegasazucena.appministrator.data.repository.AppRepository
import com.vanegasazucena.appministrator.ui.components.FilterState
import com.vanegasazucena.appministrator.ui.components.SearchEncomiendaCriteria
import com.vanegasazucena.appministrator.ui.components.SearchUserCriteria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val currentUser = MutableStateFlow<UserEntity?>(null)

    val activeFilter = MutableStateFlow<FilterState?>(null)
    val activeSearchQuery = MutableStateFlow<String?>(null)
    val activeSearchCriteria = MutableStateFlow<SearchEncomiendaCriteria?>(null)

    val activeUserSearchQuery = MutableStateFlow<String?>(null)
    val activeUserSearchCriteria = MutableStateFlow<SearchUserCriteria?>(null)

    val activeLogSearchQuery = MutableStateFlow("")

    private val rawEncomiendas: StateFlow<List<EncomiendaEntity>>
    val filteredEncomiendas: StateFlow<List<EncomiendaEntity>>

    private val rawUsers: StateFlow<List<UserEntity>>
    val filteredUsers: StateFlow<List<UserEntity>>

    val auditLogs: StateFlow<List<AuditLogEntity>>
    val filteredAuditLogs: StateFlow<List<AuditLogEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.userDao(), database.encomiendaDao(), database.auditLogDao())

        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureInitialData()
        }

        rawEncomiendas = repository.getAllEncomiendas()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        rawUsers = repository.getAllUsers()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        auditLogs = repository.getAllLogs()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        filteredAuditLogs = combine(
            auditLogs,
            activeLogSearchQuery
        ) { logs, query ->
            if (query.isBlank()) {
                logs
            } else {
                val q = query.trim().lowercase()
                logs.filter { log ->
                    log.fechaHora.lowercase().contains(q) ||
                            log.encomiendaId.lowercase().contains(q) ||
                            log.usuario.lowercase().contains(q) ||
                            log.accion.lowercase().contains(q)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        filteredEncomiendas = combine(
            rawEncomiendas,
            activeFilter,
            activeSearchQuery,
            activeSearchCriteria
        ) { list, filter, query, criteria ->
            var result = list

            if (filter != null) {
                if (filter.pendientes || filter.entregadas || filter.pagadas) {
                    result = result.filter { enc ->
                        (filter.pendientes && enc.estado.equals("PENDIENTE", ignoreCase = true)) ||
                                (filter.entregadas && enc.estado.equals("ENTREGADO", ignoreCase = true)) ||
                                (filter.pagadas && enc.estado.equals("PAGADO", ignoreCase = true))
                    }
                }
                if (filter.creadasPorUser && currentUser.value != null) {
                    result = result.filter { it.creadoPor.equals(currentUser.value?.username, ignoreCase = true) }
                }
            }

            if (!query.isNullOrEmpty() && criteria != null) {
                val q = query.trim().lowercase()
                result = result.filter { enc ->
                    when (criteria) {
                        SearchEncomiendaCriteria.REMITENTE -> enc.remitenteNombre.lowercase().contains(q)
                        SearchEncomiendaCriteria.DESTINATARIO -> enc.remitenteNombre.lowercase().contains(q) || enc.emisorNombre.lowercase().contains(q)
                        SearchEncomiendaCriteria.CODIGO -> enc.codigo.lowercase().contains(q)
                        SearchEncomiendaCriteria.CREADOR -> enc.creadoPor.lowercase().contains(q)
                    }
                }
            }

            result
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        filteredUsers = combine(
            rawUsers,
            activeUserSearchQuery,
            activeUserSearchCriteria
        ) { list, query, criteria ->
            var result = list
            if (!query.isNullOrEmpty()) {
                val q = query.trim().lowercase()
                result = result.filter { it.username.lowercase().contains(q) }
            }
            if (criteria != null) {
                result = result.filter { user ->
                    when (criteria) {
                        SearchUserCriteria.ADMINISTRADORES -> user.role.equals("ADMIN", ignoreCase = true)
                        SearchUserCriteria.COMUNES -> user.role.equals("COMMON", ignoreCase = true)
                    }
                }
            }
            result
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun setLogSearchQuery(query: String) {
        activeLogSearchQuery.value = query
    }

    fun login(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            if (user != null) {
                currentUser.value = user
                onResult(true, null)
            } else {
                onResult(false, "Usuario o contraseña incorrectos")
            }
        }
    }

    fun logout() {
        currentUser.value = null
        activeFilter.value = null
        activeSearchQuery.value = null
        activeSearchCriteria.value = null
        activeLogSearchQuery.value = ""
    }

    fun saveEncomienda(
        id: String?,
        emisorNombre: String,
        emisorDui: String,
        remitenteNombre: String,
        remitenteDui: String,
        estado: String,
        tamano: String,
        precio: Double,
        fecha: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val creator = currentUser.value?.username ?: "PeterParker"
            val actualId = if (!id.isNullOrEmpty()) id else repository.generateNextEncomiendaCode()
            val existing = repository.getEncomiendaById(actualId)

            val entity = EncomiendaEntity(
                id = actualId,
                emisorNombre = emisorNombre,
                emisorDui = emisorDui,
                remitenteNombre = remitenteNombre,
                remitenteDui = remitenteDui,
                creadoPor = if (existing != null) existing.creadoPor else creator,
                codigo = existing?.codigo ?: "", // Se genera al guardar y se conserva al editar.
                estado = estado,
                tamano = tamano,
                precio = precio,
                fecha = fecha
            )
            repository.insertEncomienda(entity)

            // Direct Insert to Audit Log DB
            if (existing == null) {
                repository.insertLog(
                    encomiendaId = actualId,
                    accion = "CREACIÓN",
                    usuario = creator,
                    detalles = "Nueva encomienda creada para $remitenteNombre. Estado: $estado, Tamaño: $tamano, Total: $$precio"
                )
            } else {
                val changes = mutableListOf<String>()
                if (!existing.estado.equals(estado, ignoreCase = true)) changes.add("Estado (${existing.estado} -> $estado)")
                if (!existing.tamano.equals(tamano, ignoreCase = true)) changes.add("Tamaño (${existing.tamano} -> $tamano)")
                if (existing.precio != precio) changes.add("Precio ($${existing.precio} -> $$precio)")
                if (existing.remitenteNombre != remitenteNombre) changes.add("Destinatario (${existing.remitenteNombre} -> $remitenteNombre)")
                if (existing.emisorNombre != emisorNombre) changes.add("Emisor (${existing.emisorNombre} -> $emisorNombre)")

                val detailsText = if (changes.isNotEmpty()) changes.joinToString(", ") else "Se actualizaron los datos de la encomienda"
                repository.insertLog(
                    encomiendaId = actualId,
                    accion = "ACTUALIZACIÓN",
                    usuario = creator,
                    detalles = detailsText
                )
            }

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteEncomienda(encomienda: EncomiendaEntity, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val creator = currentUser.value?.username ?: "admin"
            repository.deleteEncomienda(encomienda)
            repository.insertLog(
                encomiendaId = encomienda.id,
                accion = "ELIMINACIÓN",
                usuario = creator,
                detalles = "Encomienda ${encomienda.id} eliminada por $creator"
            )
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun saveUser(username: String, password: String, role: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val creator = currentUser.value?.username ?: "admin"
            val user = UserEntity(username = username, password = password, role = role)
            repository.insertUser(user)
            repository.insertLog(
                encomiendaId = "USUARIO: $username",
                accion = "CREACIÓN USUARIO",
                usuario = creator,
                detalles = "Usuario $username registrado con rol $role"
            )
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteUser(username: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val creator = currentUser.value?.username ?: "admin"
            repository.deleteUser(username)
            repository.insertLog(
                encomiendaId = "USUARIO: $username",
                accion = "ELIMINACIÓN USUARIO",
                usuario = creator,
                detalles = "Usuario $username eliminado del sistema"
            )
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun setFilter(filter: FilterState) {
        activeFilter.value = filter
    }

    fun setSearchEncomiendas(query: String, criteria: SearchEncomiendaCriteria) {
        activeSearchQuery.value = query
        activeSearchCriteria.value = criteria
    }

    fun setSearchUsers(query: String, criteria: SearchUserCriteria?) {
        activeUserSearchQuery.value = query
        activeUserSearchCriteria.value = criteria
    }

    fun clearFilters() {
        activeFilter.value = null
        activeSearchQuery.value = null
        activeSearchCriteria.value = null
        activeUserSearchQuery.value = null
        activeUserSearchCriteria.value = null
    }
}
