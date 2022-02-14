package com.example.exercise2.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exercise2.rememberAppState
import com.example.exercise2.ui.defButton
import com.example.exercise2.ui.defTopbarTextandIconbutton
import com.example.exercise2.ui.theme.Shapes
import com.example.exercise2.ui.theme.bgyellow
import com.example.exercise2.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding
import com.example.exercise2.UserInitialisaton

//The user profile activity. Here the user can see and change his username and profile picture(TODO)

@Composable
fun ProfileActivity(nav: NavHostController, username: String, userid: String) {
    Surface(modifier = Modifier.fillMaxSize().systemBarsPadding(), color = bgyellow) {
        val txtusername = rememberSaveable { mutableStateOf(username) }

        Box {
            TopAppBar(
                backgroundColor = mainorange
            ) {
                defTopbarTextandIconbutton(
                    onclick = { nav.navigate("main/${username}/${userid}") },
                    iconcntndesc = "main_btn",
                    text = "Reminders"
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector =
                Icons.Default.Person,
                contentDescription = "account_img",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            defButton(onclick = { /*TODO*/ }, text = "Change")

            Spacer(modifier = Modifier.height(30.dp))

            TextField( //textbox for username change
                value = txtusername.value,
                onValueChange = { txt -> txtusername.value = txt },
                label = { Text("Type your username here") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            defButton(onclick = { UserInitialisaton.updateUser(userid, username, txtusername.value, nav)
                                }, text = "Change")
        }
    }
}
@Preview
@Composable
fun DefaultPreview2() {
    val appState = rememberAppState()
    ProfileActivity(nav = appState.navController, "Test", userid = "testid")

}