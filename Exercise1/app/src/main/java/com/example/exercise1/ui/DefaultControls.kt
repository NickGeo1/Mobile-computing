package com.example.exercise1.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exercise1.ui.theme.Shapes
import com.example.exercise1.ui.theme.mainorange
import org.w3c.dom.Text

@Composable             //default app text
fun defText(text: String){
    Text(text = text,
        color = Color.Black,
        fontSize = 17.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold)
}

@Composable
fun defButton(onclick: ()->Unit, text: String){
    Button( //button inside column
        onClick = onclick, //navigate to home/payment
        enabled = true,
        modifier = Modifier
            .width(220.dp)
            .height(55.dp),
        shape = Shapes.small, //set roundness
        colors = ButtonDefaults.buttonColors(backgroundColor = mainorange)
    ){
        defText(text)
    }
}