package com.example.exercise4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.example.exercise4.entities.Reminder
import com.example.exercise4.entities.User
import com.example.exercise4.repository.ReminderRepository
import com.example.exercise4.repository.UserRepository
import com.example.exercise4.ui.reminder.reminderList.ReminderListViewModel
import com.example.exercise4.util.LocationReminderNotificationWorker
import kotlinx.coroutines.launch

//This class is for the database management of the users. We initialise some users at the beginning

object UserInitialisaton : ViewModel() {

    private val userRepository: UserRepository = Graph.userRepository
    private val reminderRepository: ReminderRepository = Graph.reminderRepository

    fun Initialisaton() //Initialisation of users(we haven't added any images yet)
    {
        val userlist = mutableListOf<User>(
            User(username = "nikos", password = "nikos", img = ""),
            User(username = "nikos2",password ="nikos2", img =""),
            User(username = "nikos3",password ="nikos3", img =""))

        viewModelScope.launch{
            userlist.forEach {
                userRepository.insertUser(it)
            }

        }
    }

    //this function updates a reminder
    fun updateReminder(username: String, newreminder: Reminder, oldreminder: Reminder, navController: NavController){
        viewModelScope.launch {
            if(reminderRepository.selectReminder(newreminder)==null){
                reminderRepository.updateReminder(newreminder)
                //Cancel the work for this updating reminder's old version in case the reminder location modified and the reminder requires only location
                //During the new reminder work rerun, it is going to check for the new location
                if(oldreminder.reminder_time == "" && oldreminder.longitude != "" && oldreminder.latitude !="" && !oldreminder.reminder_seen)
                    Graph.listWorkmanager.cancelAllWorkByTag(oldreminder.id.toString())
//                if(oldreminder.reminder_time == "" && oldreminder.longitude != "" && oldreminder.latitude !="" && !oldreminder.reminder_seen)
//                {
//                    Graph.listWorkmanager.cancelAllWorkByTag(oldreminder.id.toString())
//                    val locationWorker = OneTimeWorkRequestBuilder<LocationReminderNotificationWorker>()
//                        .setInputData(workDataOf("reminder_latitude" to newreminder.latitude, "reminder_longitude" to newreminder.longitude))
//                        .addTag(newreminder.id.toString()) //set tag to location worker in order to cancel it later
//                    Graph.listWorkmanager.enqueue(locationWorker.build())
//                }

                navController.navigate("main/${username}/${newreminder.creator_id}")
            }else{
                navController.navigate("fail/There is already a reminder like that")
            }
        }
    }

    //this function adds a reminder
    fun addReminder(username: String, reminder: Reminder, navController: NavController){
        viewModelScope.launch {
            if(reminderRepository.selectReminder(reminder)==null){
                 reminderRepository.insertReminder(reminder)
                 navController.navigate("main/${username}/${reminder.creator_id}")
            }else{
                navController.navigate("fail/There is already a reminder like that")
            }
        }
    }
    //this function deletes a reminder
    fun deleteReminder(reminder: Reminder){
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
        }
    }

    //this function checks if the user's credentials match these at database during login

    fun validateUser(givenusername: String, givenpassword: String, navController: NavController)
    {
        viewModelScope.launch {
            val resultuser: User? = userRepository.selectUser(givenusername)

            if(resultuser != null && givenpassword.equals(resultuser.password)){
                navController.navigate("main/${resultuser.username}/${resultuser.id}")
            }
            else{
                navController.navigate("fail/Login failed, please check your credentials")
            }
        }
    }

    //This function checks if the user's new username already exists. If it exists we don't let him use it
    //If it's not, we update the database row with the new username

    fun updateUser(userid:String, oldusername: String, newusername: String, navController: NavController){
        viewModelScope.launch {

            val resultuser: User? = userRepository.selectUser(newusername)

            if(resultuser==null){
                val olduser = userRepository.selectUser(oldusername)
                val newuser = User(id= olduser.id, username = newusername, password = olduser.password, img = olduser.img)
                userRepository.updateUser(newuser)
                navController.navigate("profile/$newusername/$userid") //navigate again to the same activity in order to update the username parameter
            }else{
                navController.navigate("fail/The username $newusername already exists")
            }
        }
    }
}

