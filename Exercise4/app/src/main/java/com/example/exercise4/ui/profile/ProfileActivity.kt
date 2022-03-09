package com.example.exercise4.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exercise4.Graph
import com.example.exercise4.rememberAppState
import com.example.exercise4.ui.defButton
import com.example.exercise4.ui.defTopbarTextandIconbutton
import com.example.exercise4.ui.theme.Shapes
import com.example.exercise4.ui.theme.bgyellow
import com.example.exercise4.ui.theme.mainorange
import com.google.accompanist.insets.systemBarsPadding
import com.example.exercise4.UserInitialisaton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exercise4.entities.User
import com.example.exercise4.util.viewModelProviderFactoryOf
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream





//The user profile activity. Here the user can see and change his username and profile picture(TODO)

@Composable
fun ProfileActivity(nav: NavHostController, username: String, userid: String) {

    Graph.currentactivity = "Profile"

    val viewModel: ProfileViewModel = viewModel(
        key = "profile_$userid",
        factory = viewModelProviderFactoryOf { ProfileViewModel(username = username) }
    )
    val viewState by viewModel.state.collectAsState()

    Surface(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding(), color = bgyellow) {
        val txtusername = rememberSaveable { mutableStateOf(username)}
        val bitmap: MutableState<Bitmap?> = rememberSaveable { mutableStateOf(null) }
        val coroutineScope = rememberCoroutineScope()
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview())
        {
            bitmap.value = it
        }

        Box {
            TopAppBar(
                backgroundColor = mainorange
            ) {
                defTopbarTextandIconbutton(
                    onclick = { nav.navigate("main/${username}/${userid}") },
                    iconcntndesc = "main_btn",
                    text = "Reminders"
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            bitmap.let {
                val data = it.value
                if (data != null)
                {
                    val bitmap_array = data.toByteArray()
                    coroutineScope.launch {
                        val currentUser = Graph.userRepository.selectUser(username)
                        Graph.userRepository.updateUser(
                            User(
                                userid.toLong(),
                                username,
                                currentUser.password,
                                data.toByteArray()
                            )
                        )
                    }

                    Image(
                        bitmap = data.asImageBitmap(),
                        contentDescription = "account_img",
                        modifier = Modifier.size(200.dp)
                    )
                }
                else if(viewState.userimage != null)
                {
                    Image(
                        bitmap = viewState.userimage!!.toBitmap().asImageBitmap(),
                        contentDescription = "account_img",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            defButton(onclick = { launcher.launch() }, text = "Change")

            Spacer(modifier = Modifier.height(30.dp))

            TextField( //textbox for username change
                value = txtusername.value,
                onValueChange = { txt -> txtusername.value = txt },
                label = { Text("Type your username here") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = Shapes.medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            defButton(onclick = { UserInitialisaton.updateUser(userid, username, txtusername.value, nav)
                                }, text = "Change")
        }
    }
}
fun Bitmap.toByteArray():ByteArray{
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.JPEG,10,this)
        return toByteArray()
    }
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this,0,size)
}

@Preview
@Composable
fun DefaultPreview2() {
    val appState = rememberAppState()
    ProfileActivity(nav = appState.navController, "Test", userid = "testid")

}