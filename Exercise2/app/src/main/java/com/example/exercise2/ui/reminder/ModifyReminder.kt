package com.example.exercise2.ui.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.exercise2.UserInitialisaton
import com.example.exercise2.entities.Reminder
import com.example.exercise2.ui.defButton
import com.example.exercise2.ui.defText
import com.example.exercise2.ui.defTopbarTextandIconbutton
import com.example.exercise2.ui.reminder.reminderList.ReminderListViewModel
import com.example.exercise2.ui.theme.Shapes
import com.example.exercise2.ui.theme.bgyellow
import com.example.exercise2.ui.theme.mainorange
import com.example.exercise2.util.viewModelProviderFactoryOf
import com.google.accompanist.insets.systemBarsPadding
import exercise2.R
import java.util.*

@Composable
fun ModifyReminder(nav: NavController,
                   username:String,
                   userid:String,
                   reminder_id: String,
                   viewModel: ReminderListViewModel = viewModel(
                       key = "user_reminder_$reminder_id",
                       factory = viewModelProviderFactoryOf { ReminderListViewModel(reminder_id,"0") }
                   )
                )
{
    //if the reminder object is null, that means we provided the 0 reminder_id(query couldn't find the reminder)
    //This is the case of creation of new reminder. In the case of editing, we provide the chosen reminder's
    //id to this context so the reminder object is not null and we can fill the textboxes
    val viewState by viewModel.state.collectAsState()
    val reminder = viewState.reminder

    val reminder_message = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable { mutableStateOf(reminder.message) }
    }

    val reminder_locationx = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable { mutableStateOf(reminder.location_x.toString()) }
    }

    val reminder_locationy = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable { mutableStateOf(reminder.location_y.toString()) }
    }

    val reminder_id = when(reminder){
        null->rememberSaveable { mutableStateOf(0) }
        else ->rememberSaveable { mutableStateOf(reminder.id) }
    }

    val reminder_date = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable { mutableStateOf(reminder.reminder_time) }
    }

    val context = LocalContext.current //get the current context for the date picker

    //instantiate calendar object
    val calendar = Calendar.getInstance()
    val firstyear = calendar.get(Calendar.YEAR)
    val firstmonth = calendar.get(Calendar.MONTH)
    val firstday = calendar.get(Calendar.DAY_OF_MONTH)
    val firsthour = calendar.get(Calendar.HOUR_OF_DAY)
    val firstminute = calendar.get(Calendar.MINUTE)
    calendar.time = Date()

    //set to the reminder_date variable the value of the chosen date-time at calendar
    val datePicker = DatePickerDialog(
        context,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            TimePickerDialog(context, { _, hour: Int, minute: Int ->
                reminder_date.value = "$dayOfMonth/$month/$year $hour:$minute"
            }, firsthour, firstminute,true).show()
        }, firstyear, firstmonth, firstday
    )

    Surface(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding(), color = bgyellow)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TopAppBar(
                backgroundColor = mainorange
            ) {
                defTopbarTextandIconbutton(
                    onclick = { nav.navigate("main/${username}/${userid}") },
                    iconcntndesc = "main_btn",
                    text = "Reminders"
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            defText(text = "Set your reminder details ", color = Color.Black)
        }

        Column(
            modifier = Modifier
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            TextField( //textbox for reminder message
                value = reminder_message.value,
                onValueChange = { msg -> reminder_message.value = msg },
                label = { Text("Your reminder message") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField( //textbox for reminder date
                value = reminder_date.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Your reminder date") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            defButton(onclick = { datePicker.show() }, text = "Choose date") //show the date picker

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                TextField( //textbox for location x
                    value = reminder_locationx.value,
                    onValueChange = { x -> reminder_locationx.value = x },
                    label = { Text(text = "Location X")},
                    modifier = Modifier.width(110.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = Shapes.medium
                )

                Spacer(modifier = Modifier.width(20.dp))

                TextField( //textbox for location y
                    value = reminder_locationy.value,
                    onValueChange = { y -> reminder_locationy.value = y },
                    label = { Text("Location Y") },
                    modifier = Modifier.width(110.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = Shapes.medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            defButton(onclick =
            {
                if(reminder==null){ //if reminder object is null we insert a new reminder
                    UserInitialisaton.addReminder(username,  Reminder(
                        message=reminder_message.value,
                        location_x = reminder_locationx.value.toInt(),
                        location_y = reminder_locationx.value.toInt(),
                        creator_id = userid.toLong(),
                        creation_time = Date().time,
                        reminder_time = reminder_date.value,
                        reminder_seen = false), navController = nav)
                }else{ //if reminder object is not null we updating the current values
                    UserInitialisaton.updateReminder(username, Reminder(id = reminder.id,
                        message = reminder_message.value,
                        location_y = reminder_locationy.value.toInt(),
                        location_x = reminder_locationx.value.toInt(),
                        creator_id = userid.toLong(),
                        creation_time = Date().time,
                        reminder_time = reminder_date.value,
                        reminder_seen = false), nav)

                }
            } , text = "Done")
        }
    }
}



