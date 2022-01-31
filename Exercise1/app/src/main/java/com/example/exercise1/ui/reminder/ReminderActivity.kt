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
import com.example.exercise1.rememberAppState
import com.example.exercise1.ui.reminder.reminderList.ReminderListElement
import com.example.exercise1.ui.theme.bgyellow
import com.example.exercise1.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun ReminderActivity(
    nav: NavHostController
) {
    Scaffold(
        modifier = Modifier.padding(bottom = 24.dp),
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
            ReminderAppBar(nav)
            ReminderListElement()
        }
    }
}

@Composable
private fun ReminderAppBar(
    nav: NavHostController
)
{      //for top app bar
    TopAppBar(
        title = {
            IconButton(onClick = {nav.navigate("login")}){  //when we press back we go at login page
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "logout_btn")
            }
            Text(
                text = "Logout",        //Logout logo
                color = bgyellow,
                modifier = Modifier
                    .padding(bottom = 2.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp)
        },
        backgroundColor = mainorange,
        actions = {IconButton(onClick = {nav.navigate("profile")}) {  //when we press we go at profile page
            Icon(imageVector = Icons.Default.Person, contentDescription = "profile_btn", Modifier)
        }
            }
    )
}




@Preview
@Composable
fun Preview(){
    val appState = rememberAppState()
    ReminderActivity(appState.navController)
}