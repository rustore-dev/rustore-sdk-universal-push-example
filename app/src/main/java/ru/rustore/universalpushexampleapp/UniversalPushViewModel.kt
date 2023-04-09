package ru.rustore.universalpushexampleapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UniversalPushViewModel : ViewModel() {
    val errorAvailability = mutableStateOf("")
    val hmsAvailability = mutableStateOf(false)
    val fcmAvailability = mutableStateOf(false)
    val rustoreAvailability = mutableStateOf(false)

    val errorToken = mutableStateOf("")
    val hmsToken = mutableStateOf("")
    val fcmToken = mutableStateOf("")
    val rustoreToken = mutableStateOf("")
}
