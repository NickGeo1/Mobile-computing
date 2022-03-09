package com.example.exercise4

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exercise4.ui.login.Fail
import com.example.exercise4.ui.login.Login
import com.example.exercise4.ui.maps.ReminderLocationMap
import com.example.exercise4.ui.profile.ProfileActivity
import com.example.exercise4.ui.reminder.ModifyReminder
import com.example.exercise4.ui.reminder.ReminderActivity

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

            //We want to stop workers because we logout
            BackHandler(true) {
                Graph.listWorkmanager.cancelAllWork()
            }
        }
        composable(route="profile/{username}/{userid}") //route for profile view destination.
        {
            backstackentry -> ProfileActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"")
            //we navigate multiple times to profile activity during name change so we want to navigate
            //back to main activity with one backpress
            BackHandler(true) {
                appState.navController.navigate("main/${backstackentry.arguments?.getString("username")?:""}/${backstackentry.arguments?.getString("userid")?:""}")
            }
        }
        composable(route = "modify_reminder/{username}/{userid}/{reminder_id}") //route for reminder modification
        {
                backstackentry -> ModifyReminder(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"",
            backstackentry.arguments?.getString("reminder_id")?:"")
        }
        composable(route = "remindermap/{position}/{setvirtual}") //route for google map we pass the marker's location if there is already one set
        {
            backstackentry->
            ReminderLocationMap(navController = appState.navController,
                backstackentry.arguments?.getString("position")?:"",
                backstackentry.arguments?.getString("setvirtual")?:"")
        }

    }
}

