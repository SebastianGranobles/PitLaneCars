package com.example.parcial_sebastiangranoblesardila.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

// --- MODELOS ---
enum class AuthState { IDLE, LOADING, SUCCESS, ERROR }

data class PasswordChangeLog(val dateString: String = "")

data class User(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val city: String = "",
    val nationality: String = "",
    val lastProfileUpdateTime: Long = 0L,
    val uid: String = "",
    val role: String = "ASESOR" // ASESOR, ADMIN, MECANICO, SUPER_ADMIN
)

data class Mechanic(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val specialty: String = "",
    val phone: String = "",
    val status: String = "Activo"
)

data class Appointment(
    val id: String = UUID.randomUUID().toString(),
    val plate: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val displacement: String = "",
    val mileage: String = "",
    val clientName: String = "",
    val phone1: String = "",
    val email: String = "",
    val selectedServices: List<String> = emptyList(),
    val problemDescription: String = "",
    val entryDate: String = "",
    val entryTime: String = "",
    val laborCost: Double = 0.0,
    val partsCost: Double = 0.0,
    val totalCost: Double = laborCost + partsCost,
    val paymentMethod: String = "Efectivo",
    val status: String = "Recibido", // Recibido (Yellow), Listo (Green), Cancelado
    val mechanic: String = "Sin asignar",
    val clientId: String = "",
    val phone2: String = "",
    val address: String = "",
    val vehicleType: String = "MOTO" // MOTO, CARRO
)

// --- VIEWMODEL LOCAL ---
class UserViewModel : ViewModel() {

    // ALMACENAMIENTO LOCAL EN MEMORIA (Simulando Firebase)
    private val localUsers = mutableListOf(
        User(
            fullName = "Administrador Pit-Lane",
            email = "admin@pitlane.com",
            phone = "3001234567",
            role = "SUPER_ADMIN", // Tiene todos los roles
            uid = "admin_uid"
        )
    )
    private val localPasswords = mutableMapOf("admin@pitlane.com" to "admin123")
    private val localAppointments = mutableListOf<Appointment>()
    private val localMechanics = mutableListOf(
        Mechanic(name = "Juan Mecánico", specialty = "Motores", phone = "3110000000"),
        Mechanic(name = "Pedro El Rápido", specialty = "Frenos", phone = "3120000000")
    )
    private val localPasswordHistory = mutableMapOf<String, MutableList<PasswordChangeLog>>()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _finishedAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val finishedAppointments: StateFlow<List<Appointment>> = _finishedAppointments

    private val _mechanics = MutableStateFlow<List<Mechanic>>(emptyList())
    val mechanics: StateFlow<List<Mechanic>> = _mechanics

    private val _passwordChangeHistory = MutableStateFlow<List<PasswordChangeLog>>(emptyList())
    val passwordChangeHistory: StateFlow<List<PasswordChangeLog>> = _passwordChangeHistory

    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState: StateFlow<AuthState> = _authState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isProfileEditingLocked = MutableStateFlow(false)
    val isProfileEditingLocked: StateFlow<Boolean> = _isProfileEditingLocked

    val nationalities = listOf("Colombiana", "Venezolana", "Ecuatoriana", "Peruana", "Otra")

    init {
        // Cargar datos iniciales
        refreshLocalData()
    }

    private fun refreshLocalData() {
        _appointments.value = localAppointments.filter { it.status != "Listo" }
        _finishedAppointments.value = localAppointments.filter { it.status == "Listo" }
        _mechanics.value = localMechanics.toList()
        _user.value?.let { currentUser ->
            _passwordChangeHistory.value = localPasswordHistory[currentUser.email] ?: emptyList()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return _user.value != null
    }

    // --- FUNCIONES DE AUTENTICACIÓN LOCALES ---

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }
        _authState.value = AuthState.LOADING
        
        val foundUser = localUsers.find { it.email == email }
        val correctPass = localPasswords[email]

        if (foundUser != null && correctPass == pass) {
            _user.value = foundUser
            _authState.value = AuthState.SUCCESS
            refreshLocalData()
        } else {
            _authState.value = AuthState.ERROR
            _errorMessage.value = "Email o contraseña incorrectos"
        }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Todos los campos son obligatorios"
            return
        }
        if (localUsers.any { it.email == email }) {
            _errorMessage.value = "El usuario ya existe"
            return
        }

        _authState.value = AuthState.LOADING
        val newUser = User(fullName = name, email = email, uid = UUID.randomUUID().toString(), role = "ASESOR")
        localUsers.add(newUser)
        localPasswords[email] = pass
        _user.value = newUser
        _authState.value = AuthState.SUCCESS
    }

    fun logout() {
        _user.value = null
        _appointments.value = emptyList()
        _finishedAppointments.value = emptyList()
        _mechanics.value = emptyList()
        _passwordChangeHistory.value = emptyList()
        _authState.value = AuthState.IDLE
    }

    fun addAppointment(app: Appointment): Boolean {
        val currentUid = _user.value?.uid ?: return false
        val finalApp = app.copy(clientId = currentUid)
        localAppointments.add(finalApp)
        refreshLocalData()
        return true
    }

    fun updateAppointmentStatus(id: String, newStatus: String) {
        val index = localAppointments.indexOfFirst { it.id == id }
        if (index != -1) {
            localAppointments[index] = localAppointments[index].copy(status = newStatus)
            refreshLocalData()
        }
    }

    fun deleteAppointment(id: String) {
        localAppointments.removeAll { it.id == id }
        refreshLocalData()
    }

    fun addMechanic(mechanic: Mechanic) {
        localMechanics.add(mechanic)
        refreshLocalData()
    }

    fun deleteMechanic(id: String) {
        localMechanics.removeAll { it.id == id }
        refreshLocalData()
    }

    fun resetAuthState() {
        _authState.value = AuthState.IDLE
        _errorMessage.value = null
    }

    fun changePassword(oldPass: String, newPass: String) {
        val currentUser = _user.value ?: return
        if (localPasswords[currentUser.email] == oldPass) {
            localPasswords[currentUser.email] = newPass
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateString = dateFormat.format(Date())
            val log = PasswordChangeLog(dateString)
            
            val history = localPasswordHistory.getOrPut(currentUser.email) { mutableListOf() }
            history.add(0, log) // Agregar al inicio (más reciente)
            
            _authState.value = AuthState.SUCCESS
            refreshLocalData()
        } else {
            _errorMessage.value = "Contraseña actual incorrecta"
            _authState.value = AuthState.ERROR
        }
    }

    fun updateUserInfo(phone: String, age: String, city: String, nationality: String, fullName: String) {
        val currentUser = _user.value ?: return
        val index = localUsers.indexOfFirst { it.uid == currentUser.uid }
        if (index != -1) {
            val updatedUser = localUsers[index].copy(
                phone = phone,
                age = age,
                city = city,
                nationality = nationality,
                fullName = fullName,
                lastProfileUpdateTime = System.currentTimeMillis()
            )
            localUsers[index] = updatedUser
            _user.value = updatedUser
            _authState.value = AuthState.SUCCESS
            _errorMessage.value = "Perfil actualizado localmente"
        }
    }

    fun deleteUserAccount() {
        val currentUser = _user.value ?: return
        localUsers.removeAll { it.uid == currentUser.uid }
        localPasswords.remove(currentUser.email)
        localPasswordHistory.remove(currentUser.email)
        logout()
    }
}
