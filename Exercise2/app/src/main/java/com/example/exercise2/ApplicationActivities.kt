package com.example.exercise2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exercise2.ui.login.Fail
import com.example.exercise2.ui.login.Login
import com.example.exercise2.ui.profile.ProfileActivity
import com.example.exercise2.ui.reminder.ModifyReminder
import com.example.exercise2.ui.reminder.ReminderActivity

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
        {
            //Arguments: the text to display and either login route or profile route with old name as parameter
            backstackentry ->
            val navigateto = rememberSaveable{ mutableStateOf("")}
            if(backstackentry.arguments?.getString("back").equals("login")){
                navigateto.value = "login"
            }else{
                navigateto.value = backstackentry.arguments?.getString("back")?:""
                navigateto.value = navigateto.value.replace(",","/")
            }
            println(navigateto)
             Fail(navController = appState.navController,
            backstackentry.arguments?.getString("text")?:"")
            {appState.navController.navigate(navigateto.value) }
        }
        composable(route = "main/{username}/{userid}") //route for main destination. Argument: username
        {
            backstackentry -> ReminderActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"",

            backstackentry.arguments?.getString("userid")?:"")
        }
        composable(route="profile/{username}/{userid}") //route for profile view destination. Argument: username
        {
            backstackentry -> ProfileActivity(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"")
        }
        composable(route = "modify_reminder/{username}/{userid}/{reminder_id}")
        {
                backstackentry -> ModifyReminder(appState.navController,
            backstackentry.arguments?.getString("username")?:"",
            backstackentry.arguments?.getString("userid")?:"",
            backstackentry.arguments?.getString("reminder_id")?:"")
        }

    }
}

