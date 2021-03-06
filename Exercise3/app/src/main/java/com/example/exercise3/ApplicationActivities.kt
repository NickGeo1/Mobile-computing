package com.example.exercise3

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exercise3.ui.login.Fail
import com.example.exercise3.ui.login.Login
import com.example.exercise3.ui.profile.ProfileActivity
import com.example.exercise3.ui.reminder.ModifyReminder
import com.example.exercise3.ui.reminder.ReminderActivity

@Composable
fun ApplicationActivities(appState: ApplicationState = rememberAppState())
{
    NavHost(
        navController = appState.navController, //navigation object
        startDestination = "login"  //route of first window destination
    ){
        composable(route = "login") //all the windows-destinations are here as composables
        {
            BackHandler(true) { //do nothing when we press back at login page

            }
            Login(navController = appState.navController)
        }
        composable(route = "fail/{text}") //route for fail messages activity
        {
            //Argument: the text to display
            backstackentry ->
             Fail(navController = appState.navController,
            backstackentry.arguments?.getString("text")?:"", appState::navigateBack)
        }
        composable(route = "main/{username}/{userid}") //route for main destination.
        {
            backstackentry -> ReminderActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"")

            //We navigate multiple times at main activity during list update so we do this
            //in order to go back with only one back press
            BackHandler(true) {
                appState.navController.navigate("login")
            }
        }
        composable(route="profile/{username}/{userid}") //route for profile view destination.
        {
            backstackentry -> ProfileActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"")
        }
        composable(route = "modify_reminder/{username}/{userid}/{reminder_id}") //route for reminder modification
        {
                backstackentry -> ModifyReminder(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"",
            backstackentry.arguments?.getString("reminder_id")?:"")
        }

    }
}

