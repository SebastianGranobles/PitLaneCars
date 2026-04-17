package com.example.parcial_sebastiangranoblesardila.model

data class PasswordChangeLog(
    val oldPass: String = "******",
    val timestamp: Long = 0L,
    val dateString: String = ""
)
