package com.example.exercise4.ui.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.exercise4.Graph
import com.example.exercise4.UserInitialisaton
import com.example.exercise4.entities.Reminder
import com.example.exercise4.ui.defButton
import com.example.exercise4.ui.defText
import com.example.exercise4.ui.defTopbarTextandIconbutton
import com.example.exercise4.ui.reminder.reminderList.ReminderListViewModel
import com.example.exercise4.ui.theme.Shapes
import com.example.exercise4.ui.theme.bgyellow
import com.example.exercise4.ui.theme.mainorange
import com.example.exercise4.util.viewModelProviderFactoryOf
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModifyReminder(nav: NavController,
                   username:String,
                   userid:String,
                   reminder_id: String,
                   viewModel: ReminderListViewModel = viewModel(
                       key = "user_reminder_$reminder_id",
                       factory = viewModelProviderFactoryOf { ReminderListViewModel(reminder_id,"0", nav) }
                   )
                )
{
    Graph.currentactivity = "Modify"
    //if the reminder object is null, that means we provided the 0 reminder_id(query couldn't find the reminder)
    //This is the case of creation of new reminder. In the case of editing, we provide the chosen reminder's
    //id to this context so the reminder object is not null and we can fill the textboxes
    val viewState by viewModel.state.collectAsState()
    val reminder = viewState.editreminder

    val reminder_message = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable { mutableStateOf(reminder.message) }
    }

    val location = nav //get the location data if any. We get location data from the google map
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<LatLng>("location_data")
        ?.value

    val reminder_latitude = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable{ mutableStateOf(reminder.latitude) }
    }

    val reminder_longitude = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable{ mutableStateOf(reminder.longitude) }
    }

    val reminder_date = when(reminder){
        null->rememberSaveable { mutableStateOf("") }
        else ->rememberSaveable { mutableStateOf(reminder.reminder_time) }
    }

    val reminder_notification = when(reminder){
        null->rememberSaveable { mutableStateOf(false) }
        else ->rememberSaveable { mutableStateOf(reminder.notification) }
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
                val correcthour = when{
                    hour < 10 -> "0$hour"
                    else -> hour
                }
                val correctminute = when{
                    minute < 10 -> "0$minute"
                    else -> minute
                }
                reminder_date.value = "$dayOfMonth/${month+1}/$year $correcthour:$correctminute" //we add 1 to month because months begin from 0
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

            Spacer(modifier = Modifier.height(20.dp))

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
                onValueChange ={},
                readOnly = true,
                label = { Text("Your reminder date") },
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            defButton(onclick = { datePicker.show() }, text = "Choose date") //show the date picker

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                TextField( //textbox for latitude
                    value = location?.latitude?.toString() ?: reminder_latitude.value,
                    onValueChange = {},
                    label = { Text(text = "Longitude")},
                    modifier = Modifier.width(110.dp),
                    readOnly = true,
                    shape = Shapes.medium
                )

                Spacer(modifier = Modifier.width(20.dp))

                TextField( //textbox for longitude
                    value = location?.longitude?.toString() ?: reminder_longitude.value,
                    onValueChange = {},
                    label = { Text("Latitude") },
                    modifier = Modifier.width(110.dp),
                    readOnly = true,
                    shape = Shapes.medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick =
                {
                    val mapnavigate = when (location)
                    {
                        null -> "remindermap/${reminder_latitude.value},${reminder_longitude.value}"
                        else -> "remindermap/${location.latitude},${location.longitude}"
                    }
                    Graph.markeradded = false
                    nav.navigate(mapnavigate)
                },
                enabled = true,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(55.dp),
                shape = Shapes.small,
                colors = ButtonDefaults.buttonColors(backgroundColor = mainorange)
            ){
                defText("Choose map location", Color.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                defText(text = "Receive notification: ", color = Color.Black)

                Spacer(modifier = Modifier.width(20.dp))

                Checkbox(checked = reminder_notification.value,
                        onCheckedChange = {reminder_notification.value = !reminder_notification.value},
                        enabled = reminder_date.value !="" || location != null || reminder_latitude.value != "" && reminder_longitude.value != "")
                        //we enable the notification choice only if there is a time or a location set for it
            }

            Spacer(modifier = Modifier.height(20.dp))

            defButton(onclick =
            {
                if(reminder_date.value!="" && isPastDate(reminder_date.value))
                {
                    nav.navigate("fail/You cant add a notification with a past date")
                }
                else if(reminder==null){ //if reminder object is null we insert a new reminder
                    UserInitialisaton.addReminder(username,  Reminder(
                        message=reminder_message.value,
                        latitude = location?.latitude?.toString() ?: reminder_latitude.value,
                        longitude = location?.longitude?.toString() ?: reminder_longitude.value,
                        creator_id = userid.toLong(),
                        creation_time = Date().time,
                        reminder_time = reminder_date.value,
                        reminder_seen = reminder_date.value == "",
                        //if time and location is empty this is true. We consider reminders without time and location set as seen
                        notification = reminder_notification.value),
                        navController = nav)
                }else{ //if reminder object is not null we updating the current values
                    UserInitialisaton.updateReminder(username, Reminder(id = reminder.id,
                        message = reminder_message.value,
                        longitude = location?.longitude?.toString() ?: reminder_longitude.value,
                        latitude = location?.latitude?.toString() ?: reminder_latitude.value,
                        creator_id = userid.toLong(),
                        creation_time = Date().time,
                        reminder_time = reminder_date.value,
                        reminder_seen = reminder_date.value == ""
                        ,
                        notification = reminder_notification.value), navController = nav)

                }
            } , text = "Done")
        }
    }
}

fun isPastDate(newremtime:String):Boolean{
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
    val newremindertime = formatter.parse(newremtime)
    val currenttime = formatter.parse(formatter.format(Date()))
    val comparer = newremindertime.compareTo(currenttime)
    return when{
        comparer < 0 -> true
        else -> false
    }
}


