package com.example.exercise1.ui.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.exercise1.entities.User
import com.example.exercise1.rememberAppState
import com.example.exercise1.ui.defTopbarTextandIconbutton
import com.example.exercise1.ui.reminder.reminderList.ReminderListElement
import com.example.exercise1.ui.theme.bgyellow
import com.example.exercise1.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding

//Our main activity. Here we are going to show,add,edit and delete our reminders

@Composable
fun ReminderActivity(
    nav: NavHostController,
    username: String
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {/*TODO*/ },
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
            ReminderAppBar(nav, username)
            ReminderListElement()
        }
    }
}

@Composable
private fun ReminderAppBar(
    nav: NavHostController,
    username: String?
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
        actions = {IconButton(onClick = {nav.navigate("profile/${username}")}) {  //when we press we go at profile page
            Icon(imageVector = Icons.Default.Person, contentDescription = "profile_btn", Modifier)
            }
        }
    )
}

@Preview
@Composable
fun Preview(){
    val appState = rememberAppState()
    ReminderActivity(appState.navController, "TEST")
}