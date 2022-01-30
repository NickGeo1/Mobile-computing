package com.example.exercise1.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable

fun ProfileActivity(nav: NavHostController){
    Surface{
        Column(
        ){
            IconButton(onClick = { nav.navigate("main") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    }
}