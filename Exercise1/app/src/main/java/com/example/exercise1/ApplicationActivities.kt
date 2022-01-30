package com.example.exercise1

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exercise1.ui.login.Login
import com.example.exercise1.ui.profile.ProfileActivity
import com.example.exercise1.ui.reminder.ReminderActivity

@Composable
fun ApplicationActivities(appState: ApplicationState = rememberAppState())
{
    NavHost(
        navController = appState.navController, //navigation object
        startDestination = "login"  //route of first window destination
    ){
        composable(route = "login") //all the windows-destinations are here as composables
        {
            Login(navController = appState.navController)
        }
        composable(route = "main")
        {
           ReminderActivity(appState.navController)
        }
        composable(route="profile")
        {
            ProfileActivity(appState.navController) //take the method appState.navigateBack() as param
        }

    }
}

