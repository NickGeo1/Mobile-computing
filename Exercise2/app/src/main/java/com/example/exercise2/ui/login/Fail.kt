package com.example.exercise2.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.exercise2.ui.defText
import com.example.exercise2.ui.defTopbarTextandIconbutton
import com.example.exercise2.ui.theme.bgyellow
import com.example.exercise2.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding

//Activity for Error messages

@Composable
fun Fail(navController: NavController, text: String, onbackpress: () -> Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding(), color = bgyellow){
        Box{
            TopAppBar(backgroundColor = mainorange) {
                defTopbarTextandIconbutton(
                    onclick = onbackpress,
                    iconcntndesc = null,
                    text = "Go back"
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            defText(text = text , color = Color.Red)
        }

    }
}