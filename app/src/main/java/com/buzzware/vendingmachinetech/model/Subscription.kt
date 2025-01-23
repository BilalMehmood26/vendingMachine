package com.buzzware.vendingmachinetech.model

data class Subscription(
    val createdDate :Long = 0,
    val startDate :Long = 0,
    val endDate :Long = 0,
    val fee :String = "",
    val plan :String = "",
    val userId :String = "",
    val status :Boolean = true
)
