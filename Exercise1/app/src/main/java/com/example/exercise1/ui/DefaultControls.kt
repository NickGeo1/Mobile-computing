package com.example.exercise1.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exercise1.ui.theme.Shapes
import com.example.exercise1.ui.theme.bgyellow
import com.example.exercise1.ui.theme.mainorange
import org.w3c.dom.Text

@Composable             //default app text
fun defText(text: String, color: Color){
    Text(text = text,
        color = color,
        fontSize = 17.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold)
}

@Composable         //default button style
fun defButton(onclick: ()->Unit, text: String){
    Button(
        onClick = onclick,
        enabled = true,
        modifier = Modifier
            .width(220.dp)
            .height(55.dp),
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(backgroundColor = mainorange)
    ){
        defText(text, Color.Black)
    }
}

@Composable         //default text for topbar and default icon button
fun defTopbarTextandIconbutton(onclick: ()-> Unit, iconcntndesc: String?, text: String){
    IconButton(onClick = onclick){  //when we press back we go at login page
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = iconcntndesc)
    }
    Text(
        text = text,
        color = bgyellow,
        modifier = Modifier
            .padding(bottom = 2.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp)
}