package com.example.exercise4.ui.reminder.reminderList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.exercise4.Graph
import com.example.exercise4.UserInitialisaton
import com.example.exercise4.entities.Reminder
import com.example.exercise4.ui.defButton
import com.example.exercise4.util.viewModelProviderFactoryOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderListElement(username:String, userid:String, nav:NavController)
{
    val show_btn_text = rememberSaveable { mutableStateOf("Show all")}
    val show_all = rememberSaveable { mutableStateOf(false)}
    val showlist: MutableState<List<Reminder>> = rememberSaveable {mutableStateOf(listOf())}

    val viewModel: ReminderListViewModel = viewModel(
        key = "user_list_$userid",
        factory = viewModelProviderFactoryOf { ReminderListViewModel("0",userid,changeview = {newview -> showlist.value = newview; show_btn_text.value = "Show all"}) }
    )
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ReminderList(
            list = if(showlist.value.isEmpty()){
                //if we assign this to showlist, the first time is null.
                //If we change the viewState.showreminders at every button press, sometimes doesnt work
                viewState.showreminders
            }else{
                 showlist.value
                 },
            nav = nav,
            username = username,
            //we pass these two variables all the way down to ReminderListItem() in order to change
            //showlist.value as soon as we press the delete button
            onelementdelete = {showlist.value = it},
            showall = show_all.value
        )
        Spacer(Modifier.height(30.dp))
        Row {
            defButton(onclick = {
                if(show_btn_text.value.equals("Show all"))
                {
                    show_btn_text.value = "Show seen"
                    show_all.value = true
                }else
                {
                    show_btn_text.value = "Show all"
                    show_all.value = false
                }
                coroutineScope.launch {
                    showlist.value = viewModel.changeShow(show_all.value) //we return the new results at showlist that changes the lazycolumn
                }
            }, text = show_btn_text.value)

            Spacer(Modifier.width(30.dp))

            defButton(onclick = {Graph.markeradded = false ; nav.navigate("remindermap/,/${true}")}, text = "Set virtual location")
        }

    }

}

@Composable
private fun ReminderList(
    list: List<Reminder>,
    nav: NavController,
    username:String,
    onelementdelete: (List<Reminder>) -> Unit,
    showall: Boolean
) {
    val dialog = rememberSaveable { mutableStateOf(false) }
    val message = rememberSaveable { mutableStateOf("") }
    val longitude = rememberSaveable { mutableStateOf("") }
    val latitude = rememberSaveable { mutableStateOf("") }
    val rem_time = rememberSaveable { mutableStateOf("") }
    val creation_time = rememberSaveable { mutableStateOf("") }
    val creator_id: MutableState<Long> = rememberSaveable { mutableStateOf(0) }
    val reminder_seen = rememberSaveable { mutableStateOf(false) }
    val reminder_notification = rememberSaveable { mutableStateOf(false)}

    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list) { item ->
            ReminderListItem(
                reminder = item,
                onClick = {
                    dialog.value = true
                    message.value = item.message
                    latitude.value = when(item.latitude)
                    {
                        "" -> "Not set"
                        else -> item.latitude
                    }

                    longitude.value = when(item.longitude)
                    {
                        "" -> "Not set"
                        else -> item.longitude
                    }
                    rem_time.value = when(item.reminder_time)
                    {
                        "" -> "Not set"
                        else -> item.reminder_time
                    }
                    creation_time.value = item.creation_time.toDateString()
                    creator_id.value = item.creator_id
                    reminder_seen.value = item.reminder_seen
                    reminder_notification.value = item.notification
                    },
                modifier = Modifier.fillParentMaxWidth(),
                nav = nav,
                userid = item.creator_id.toString(),
                username = username,
                onelementdelete,
                showall
            )
        }
    }

    if(dialog.value){ //show all reminder details
        AlertDialog(
            onDismissRequest = { dialog.value = false },
            confirmButton = {
                TextButton(onClick = {dialog.value = false})
                { Text(text = "OK") }
            },
            title = { Text(text = "Your reminder details") },
            text = { Text(text = "Message: ${message.value}\n\n" +
                                 "Latitude: ${latitude.value}\n\n" +
                                 "Longitude: ${longitude.value}\n\n" +
                                 "Reminder/notification time: ${rem_time.value}\n\n" +
                                 "Last modified at: ${creation_time.value}\n\n" +
                                 "Notification enabled: ${if(reminder_notification.value){"true"} else {"false"}}")
            }
        )
    }
}

@Composable
private fun ReminderListItem(
    reminder: Reminder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    nav: NavController,
    userid: String,
    username: String,
    onelementdelete: (List<Reminder>) -> Unit,
    showall: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(modifier = modifier.clickable { onClick() }) {
        val (divider, id, title, row, date) = createRefs()
        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        // title
        Text(
            text = reminder.message,
            maxLines = 2,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(title) {
                linkTo(
                    start = id.end,
                    end = row.start,
                    startMargin = 5.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )

        // id
        Text(
            text = reminder.id.toString(),
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(id) {
                linkTo(
                    start = parent.start,
                    end = row.start,
                    startMargin = 12.dp,
                    endMargin = 8.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 6.dp)
                bottom.linkTo(date.top, 10.dp)
                width = Dimension.preferredWrapContent
                centerVerticallyTo(title)
            }
        )

        // date
        Text(
            text = when(reminder.reminder_time){
                    "" -> "Time not set"
                    else -> "Notification set at: ${reminder.reminder_time}"
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.constrainAs(date) {
                    linkTo(
                        start = parent.start,
                        end = row.start,
                        startMargin = 8.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(id.bottom, 6.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
                    )

                    Row(
                        modifier = Modifier.constrainAs(row) {
                            top.linkTo(parent.top, 10.dp)
                            bottom.linkTo(parent.bottom, 10.dp)
                            end.linkTo(parent.end)
                        }
                    )
                    {

                        // icon for edit
                        IconButton(
                            onClick = { nav.navigate(route="modify_reminder/$username/$userid/${reminder.id}") },
                            modifier = Modifier
                                .size(50.dp)
                                .padding(6.dp)
                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "edit_btn"
                            )
                        }

                        IconButton( // icon for delete
                            onClick = { UserInitialisaton.deleteReminder(reminder)
                                coroutineScope.launch {
                                    onelementdelete(Graph.reminderRepository.selectuserReminders(userid.toLong(), showall)) //set showlist.value equal to the new list after deletion
                                }

                                //if this reminder has only location requirement and is unseen, we have to stop the worker associated with it as soon as we delete it
                                //each location worker has a tag equal to the id of the reminder
                                if(reminder.latitude != "" && reminder.longitude != "" && reminder.reminder_time == "" && !reminder.reminder_seen)
                                    Graph.listWorkmanager.cancelAllWorkByTag(reminder.id.toString())
                            },
                            //nav.navigate("main/$username/$userid")}, //we renavigate after the delete so we can see the results
                            modifier = Modifier
                                .size(50.dp)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "delete_btn"
                            )
                        }
                    }
            }
    }

private fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))

}