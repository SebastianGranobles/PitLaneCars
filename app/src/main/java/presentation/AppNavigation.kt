package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash_route"
    ) {
        composable("splash_route") {
            SplashScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("appointment_route") {
            AppointmentScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("login_route") {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("register_route") {
            RegisterScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("main_route") {
            MainScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("profile_route") {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("user_route") {
            UserScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("recover_password_route") {
            RecuperarContraseñaScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("password_history_route") {
            PasswordHistoryScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("history_route") {
            HistoryScreen(navController = navController, userViewModel = userViewModel)
        }
        // RUTAS DE ADMINISTRACIÓN
        composable("admin_dashboard_route") {
            AdminDashboardScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("mechanics_management_route") {
            MechanicsManagementScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("admin_appointments_route") {
            AdminAppointmentsScreen(navController = navController, userViewModel = userViewModel)
        }
    }
}
