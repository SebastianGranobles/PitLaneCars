package com.example.parcial_sebastiangranoblesardila.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val role: String = "ASESOR" // ASESOR, ADMIN, MECANICO
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

// --- VIEWMODEL ---
class UserViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var appointmentsListener: ListenerRegistration? = null
    private var mechanicsListener: ListenerRegistration? = null

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _finishedAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val finishedAppointments: StateFlow<List<Appointment>> = _finishedAppointments

    private val _mechanics = MutableStateFlow<List<Mechanic>>(emptyList())
    val mechanics: StateFlow<List<Mechanic>> = _mechanics

    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState: StateFlow<AuthState> = _authState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _passwordChangeHistory = MutableStateFlow<List<PasswordChangeLog>>(emptyList())
    val passwordChangeHistory: StateFlow<List<PasswordChangeLog>> = _passwordChangeHistory

    private val _isProfileEditingLocked = MutableStateFlow(false)
    val isProfileEditingLocked: StateFlow<Boolean> = _isProfileEditingLocked

    val nationalities = listOf("Colombiana", "Venezolana", "Ecuatoriana", "Peruana", "Otra")

    init {
        // Restaurar sesión si existe
        auth.currentUser?.let { firebaseUser ->
            fetchUserData(firebaseUser.uid)
            startAppointmentsRealtimeListener()
            startMechanicsRealtimeListener()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // --- FUNCIONES DE AUTENTICACIÓN ---

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }
        _authState.value = AuthState.LOADING
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    fetchUserData(uid)
                    startAppointmentsRealtimeListener()
                    startMechanicsRealtimeListener()
                } else {
                    _authState.value = AuthState.ERROR
                    _errorMessage.value = "Error: ${task.exception?.localizedMessage}"
                }
            }
    }

    private fun fetchUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    val userObj = User(
                        fullName = data?.get("fullname") as? String ?: "",
                        email = data?.get("email") as? String ?: "",
                        phone = data?.get("phone") as? String ?: "",
                        age = data?.get("age") as? String ?: "",
                        city = data?.get("city") as? String ?: "",
                        nationality = data?.get("nationality") as? String ?: "",
                        lastProfileUpdateTime = data?.get("lastProfileUpdateTime") as? Long ?: 0L,
                        uid = data?.get("uid") as? String ?: "",
                        role = data?.get("role") as? String ?: "ASESOR"
                    )
                    _user.value = userObj
                    _authState.value = AuthState.SUCCESS
                } else {
                    _authState.value = AuthState.SUCCESS
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.localizedMessage
                _authState.value = AuthState.ERROR
            }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Todos los campos son obligatorios"
            return
        }
        _authState.value = AuthState.LOADING

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""

                    val userData = hashMapOf(
                        "age" to "",
                        "city" to "",
                        "email" to email,
                        "fullname" to name,
                        "lastProfileUpdateTime" to 0L,
                        "nationality" to "",
                        "phone" to "",
                        "uid" to uid,
                        "role" to "ASESOR"
                    )

                    db.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            _user.value = User(fullName = name, email = email, uid = uid, role = "ASESOR")
                            _authState.value = AuthState.SUCCESS
                            startAppointmentsRealtimeListener()
                            startMechanicsRealtimeListener()
                        }
                        .addOnFailureListener { e ->
                            _errorMessage.value = "Error en base de datos: ${e.localizedMessage}"
                            _authState.value = AuthState.ERROR
                        }
                } else {
                    _authState.value = AuthState.ERROR
                    _errorMessage.value = "Error: ${task.exception?.localizedMessage}"
                }
            }
    }

    fun logout() {
        appointmentsListener?.remove()
        mechanicsListener?.remove()
        auth.signOut()
        _user.value = null
        _appointments.value = emptyList()
        _finishedAppointments.value = emptyList()
        _mechanics.value = emptyList()
        _authState.value = AuthState.IDLE
    }

    fun startAppointmentsRealtimeListener() {
        appointmentsListener?.remove()
        appointmentsListener = db.collection("appointments")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _errorMessage.value = "Error en tiempo real: ${e.localizedMessage}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects(Appointment::class.java)
                    _appointments.value = list.filter { it.status != "Listo" }
                    _finishedAppointments.value = list.filter { it.status == "Listo" }
                }
            }
    }

    fun startMechanicsRealtimeListener() {
        mechanicsListener?.remove()
        mechanicsListener = db.collection("mechanics")
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _mechanics.value = snapshot.toObjects(Mechanic::class.java)
                }
            }
    }

    fun addAppointment(app: Appointment): Boolean {
        val currentUid = auth.currentUser?.uid ?: return false
        val finalApp = app.copy(clientId = currentUid)

        db.collection("appointments").document(finalApp.id).set(finalApp)
            .addOnFailureListener { e ->
                _errorMessage.value = "Error al crear cita: ${e.localizedMessage}"
            }
        return true
    }

    fun updateAppointmentStatus(id: String, newStatus: String) {
        db.collection("appointments").document(id).update("status", newStatus)
    }

    fun deleteAppointment(id: String) {
        db.collection("appointments").document(id).delete()
            .addOnFailureListener { e ->
                _errorMessage.value = "Error al eliminar: ${e.localizedMessage}"
            }
    }

    fun addMechanic(mechanic: Mechanic) {
        db.collection("mechanics").document(mechanic.id).set(mechanic)
    }

    fun deleteMechanic(id: String) {
        db.collection("mechanics").document(id).delete()
    }

    fun resetAuthState() {
        _authState.value = AuthState.IDLE
        _errorMessage.value = null
    }

    fun changePassword(oldPass: String, newPass: String) {
        val firebaseUser = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, oldPass)

        _authState.value = AuthState.LOADING
        firebaseUser.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                firebaseUser.updatePassword(newPass).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        _authState.value = AuthState.SUCCESS
                    } else {
                        _errorMessage.value = updateTask.exception?.localizedMessage
                        _authState.value = AuthState.ERROR
                    }
                }
            } else {
                _errorMessage.value = "Contraseña actual incorrecta"
                _authState.value = AuthState.ERROR
            }
        }
    }

    fun updateUserInfo(phone: String, age: String, city: String, nationality: String, fullName: String) {
        val currentUid = auth.currentUser?.uid ?: return
        val updateTime = System.currentTimeMillis()

        val updatedData = mapOf(
            "phone" to phone,
            "age" to age,
            "city" to city,
            "nationality" to nationality,
            "fullname" to fullName,
            "lastProfileUpdateTime" to updateTime
        )

        _authState.value = AuthState.LOADING
        db.collection("users").document(currentUid).update(updatedData)
            .addOnSuccessListener {
                _user.value = _user.value?.copy(
                    phone = phone,
                    age = age,
                    city = city,
                    nationality = nationality,
                    fullName = fullName,
                    lastProfileUpdateTime = updateTime
                )
                _authState.value = AuthState.SUCCESS
                _errorMessage.value = "Perfil actualizado con éxito"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error: ${e.localizedMessage}"
                _authState.value = AuthState.ERROR
            }
    }

    fun deleteUserAccount() {
        val currentUid = auth.currentUser?.uid ?: return
        
        db.collection("users").document(currentUid).delete()
            .addOnSuccessListener {
                auth.currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            logout()
                        } else {
                            _errorMessage.value = "Error al borrar cuenta: ${task.exception?.localizedMessage}"
                        }
                    }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error al borrar datos: ${e.localizedMessage}"
            }
    }

    override fun onCleared() {
        super.onCleared()
        appointmentsListener?.remove()
        mechanicsListener?.remove()
    }
}
