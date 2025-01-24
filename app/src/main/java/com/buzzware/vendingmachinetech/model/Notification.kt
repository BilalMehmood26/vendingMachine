package com.buzzware.vendingmachinetech.model

data class Notification(
    val description :String? ="",
    val isread :Boolean? =false,
    val title :String? ="",
    val time :Long? = 0,
    val type :String? ="",
    val videoId :String? ="",
    val userId :String? =""
)
