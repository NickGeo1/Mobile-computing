package com.example.exercise1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.exercise1.entities.User
import com.example.exercise1.repository.UserRepository
import kotlinx.coroutines.launch

//This class is for the database management of the users. We initialise some users at the beginning

class UserInitialisaton(private val userRepository: UserRepository = Graph.userRepository): ViewModel() {

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

    //this function checks if the user's credentials match these at database during login

    fun validateUser(givenusername: String, givenpassword: String, navController: NavController)
    {
        viewModelScope.launch {
            val resultuser: User? = userRepository.selectUser(givenusername)

            if(resultuser != null && givenpassword.equals(resultuser.password)){
                navController.navigate("main/${resultuser.username}")
            }
            else{
                navController.navigate("fail/Login failed, please check your credentials/login")
            }
        }
    }

    //This function checks if the user's new username already exists. If it exists we don't let him use it
    //If it's not, we update the database row with the new username

    fun updateUser(oldusername: String, newusername: String, navController: NavController){
        viewModelScope.launch {

            val resultuser: User? = userRepository.selectUser(newusername)

            if(resultuser==null){
                val olduser = userRepository.selectUser(oldusername)
                val newuser = User(id= olduser.id, username = newusername, password = olduser.password, img = olduser.img)
                userRepository.updateUser(newuser)
                navController.navigate("profile/$newusername")
            }else{
                navController.navigate("fail/The username $newusername already exists/${oldusername.toString()}")
            }
        }
    }
}

