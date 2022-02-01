package com.example.exercise1

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exercise1.ui.login.Fail
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
        composable(route = "fail/{text}/{back}") //route for fail to login/fail to change username destination
        {                                       //Arguments: the text to display and either login route or profile route with old name as parameter
            backstackentry ->
            val navigateto = when(backstackentry.arguments?.getString("back") ?:""){
                "login"-> "login"
                else -> "profile/"+backstackentry.arguments?.getString("back")
            }
             Fail(navController = appState.navController,
            backstackentry.arguments?.getString("text")?:"")
            {appState.navController.navigate(navigateto) }
        }
        composable(route = "main/{username}") //route for main destination. Argument: username
        {
            backstackentry -> ReminderActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"")
        }
        composable(route="profile/{username}") //route for profile view destination. Argument: username
        {
            backstackentry -> ProfileActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"")
        }

    }
}

