package com.example.exercise3.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.exercise3.UserInitialisaton
import com.example.exercise3.ui.defButton
import com.example.exercise3.ui.defText
import com.example.exercise3.ui.theme.Shapes
import com.example.exercise3.ui.theme.bgyellow
import com.example.exercise3.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding
import exercise2.R

@Composable
fun Login(
    navController: NavController
){
    Surface(modifier = Modifier.fillMaxSize(), color = bgyellow)
    { //the surface of the screen controls
        val password = rememberSaveable { mutableStateOf("") }
        val username = rememberSaveable { mutableStateOf("") }

        val image: Painter = painterResource(id = R.drawable.cal)
        Image(painter = image,contentDescription = "",modifier = Modifier.fillMaxSize())

        Column( //A column inside the surface on which we are going to put our controls
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally, //each row control of the column at the center
            verticalArrangement = Arrangement.Center //all elements of column at the center
        )
        {
            Text(text = "Welcome to Remindme!",
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif)

            Spacer(modifier = Modifier.height(30.dp))

            Icon( //an Icon inside the column
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                tint = mainorange)

            Spacer(modifier = Modifier.height(10.dp)) //a spacer inside column(spaced bellow icon)

            defText("Login into your Remindme account:", color = Color.Black)

            Spacer(modifier = Modifier.height(10.dp))

            TextField( //textbox for username inside the column
                value = username.value,
                onValueChange = {data -> username.value = data}, //data is the current text and
                label = { Text("Username") },                //in every change, it changes the username.value
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField( //textbox for password inside the column
                value = password.value,
                onValueChange = {data -> password.value = data},
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(), //for hidden text
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(70.dp))

            defButton(onclick = {
                UserInitialisaton.validateUser(username.value, password.value, navController)},   //check if the user details are correct
                text = "Login")
        }
    }

}

//@Preview
//@Composable
//fun DefaultPreview() {
//    val appState = rememberAppState()
//    Login(navController = appState.navController)
//
//}