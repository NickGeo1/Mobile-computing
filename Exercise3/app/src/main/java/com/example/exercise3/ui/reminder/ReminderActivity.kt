package com.example.exercise3.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exercise3.ui.defButton
import com.example.exercise3.ui.defTopbarTextandIconbutton
import com.example.exercise3.ui.reminder.reminderList.ReminderListElement
import com.example.exercise3.ui.theme.bgyellow
import com.example.exercise3.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding

//Our main activity. Here we are going to show,add,edit and delete our reminders

@Composable
fun ReminderActivity(
    navController: NavHostController,
    username: String,
    userid: String,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {navController.navigate("modify_reminder/${username}/${userid}/${"0"}")},
                contentColor = bgyellow,
                modifier = Modifier.padding(all = 20.dp),
                backgroundColor = mainorange,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add_btn",
                )
            }
        }, backgroundColor = bgyellow
    ){
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxWidth()
        ) {
            ReminderAppBar(navController, username, userid)
            ReminderListElement(username, userid, nav = navController)
        }
    }
}

@Composable
private fun ReminderAppBar(
    nav: NavHostController,
    username: String,
    userId: String
)
{      //for top app bar
    TopAppBar(
        title = {
            defTopbarTextandIconbutton(
                onclick = {nav.navigate("login")},
                iconcntndesc = "logout_btn",
                text = "Logout"
            )
        },
        backgroundColor = mainorange,
        actions = {IconButton(onClick = {nav.navigate("profile/${username}/${userId}")}) {  //when we press we go at profile page
            Icon(imageVector = Icons.Default.Person, contentDescription = "profile_btn")
            }
        }
    )
}