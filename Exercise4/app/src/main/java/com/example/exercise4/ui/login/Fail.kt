package com.example.exercise4.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.exercise4.Graph
import com.example.exercise4.ui.defText
import com.example.exercise4.ui.defTopbarTextandIconbutton
import com.example.exercise4.ui.theme.bgyellow
import com.example.exercise4.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding

//Activity for Error messages

@Composable
fun Fail(navController: NavController, text: String, onbackpress: () -> Unit){
    Graph.currentactivity = "Fail"
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