package com.vanegasazucena.appministrator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vanegasazucena.appministrator.data.local.AppDatabase
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

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val currentUser = MutableStateFlow<UserEntity?>(null)

    val activeFilter = MutableStateFlow<FilterState?>(null)
    val activeSearchQuery = MutableStateFlow<String?>(null)
    val activeSearchCriteria = MutableStateFlow<SearchEncomiendaCriteria?>(null)

    val activeUserSearchQuery = MutableStateFlow<String?>(null)
    val activeUserSearchCriteria = MutableStateFlow<SearchUserCriteria?>(null)

    private val rawEncomiendas: StateFlow<List<EncomiendaEntity>>
    val filteredEncomiendas: StateFlow<List<EncomiendaEntity>>

    private val rawUsers: StateFlow<List<UserEntity>>
    val filteredUsers: StateFlow<List<UserEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.userDao(), database.encomiendaDao())

        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureInitialData()
        }

        rawEncomiendas = repository.getAllEncomiendas()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        rawUsers = repository.getAllUsers()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
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
        viewModelScope.launch {
            val creator = currentUser.value?.username ?: "PeterParker"
            val actualId = if (!id.isNullOrEmpty()) id else repository.generateNextEncomiendaCode()

            val entity = EncomiendaEntity(
                id = actualId,
                emisorNombre = emisorNombre,
                emisorDui = emisorDui,
                remitenteNombre = remitenteNombre,
                remitenteDui = remitenteDui,
                creadoPor = creator,
                codigo = actualId,
                estado = estado,
                tamano = tamano,
                precio = precio,
                fecha = fecha
            )
            repository.insertEncomienda(entity)
            onComplete()
        }
    }

    fun deleteEncomienda(encomienda: EncomiendaEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteEncomienda(encomienda)
            onComplete()
        }
    }

    fun saveUser(username: String, password: String, role: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = UserEntity(username = username, password = password, role = role)
            repository.insertUser(user)
            onComplete()
        }
    }

    fun deleteUser(username: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser(username)
            onComplete()
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
