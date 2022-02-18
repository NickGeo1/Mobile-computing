package com.example.exercise3.ui.reminder.reminderList

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.exercise3.UserInitialisaton
import com.example.exercise3.entities.Reminder
import com.example.exercise3.util.viewModelProviderFactoryOf
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderListElement(username:String, userid: String, nav: NavController)
{
    val viewModel: ReminderListViewModel = viewModel(
        key = "user_list_$userid",
        factory = viewModelProviderFactoryOf { ReminderListViewModel("0",userid) }
    )
    val viewState by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        ReminderList(
            list = viewState.reminders,
            nav = nav,
            userid = userid,
            username = username
        )
    }
}

@Composable
private fun ReminderList(
    userid: String,
    list: List<Reminder>,
    nav: NavController,
    username:String
) {
    val dialog = rememberSaveable { mutableStateOf(false) }
    val message = rememberSaveable { mutableStateOf("") }
    val loc_x = rememberSaveable { mutableStateOf(0) }
    val loc_y = rememberSaveable { mutableStateOf(0) }
    val rem_time = rememberSaveable { mutableStateOf("") }
    val creation_time: MutableState<String> = rememberSaveable { mutableStateOf("") }
    val creator_id: MutableState<Long> = rememberSaveable { mutableStateOf(0) }
    val reminder_seen = rememberSaveable { mutableStateOf(false) }

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
                    loc_x.value = item.location_x
                    loc_y.value = item.location_y
                    rem_time.value = item.reminder_time
                    creation_time.value = item.creation_time.toDateString()
                    creator_id.value = item.creator_id
                    reminder_seen.value = item.reminder_seen

                },
                modifier = Modifier.fillParentMaxWidth(),
                nav = nav,
                userid = userid,
                username = username,
                //random_picture = piclist.random() //random picture for this list element
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
                                 "Location: (${loc_x.value},${loc_y.value})\n\n" +
                                 "Reminder/notification time: ${rem_time.value}\n\n" +
                                 "Created at: ${creation_time.value}")
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
    //picture: Painter
) {
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
            text = "Notification set at: " + reminder.reminder_time,
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

            //Image(painter = image, contentDescription = "",
                //modifier = Modifier.size(50.dp).padding(6.dp))

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
                onClick = { UserInitialisaton.deleteReminder(reminder) },
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