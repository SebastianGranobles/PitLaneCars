package com.example.parcial_sebastiangranoblesardila.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val city: String = "",
    val nationality: String = "",
    val lastProfileUpdateTime: Long = 0L
)
