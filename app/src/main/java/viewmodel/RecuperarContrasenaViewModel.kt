package com.example.parcial_sebastiangranoblesardila.Presentation.viewmodel

import androidx.lifecycle.ViewModel

class RecuperarContrasenaViewModel : ViewModel() {

    private val userCredentials = mapOf(
        "sebas@gmail.com" to "1144109752"

    )

    fun recoverPassword(email: String): String? {
        return userCredentials[email.trim()]
    }
}
