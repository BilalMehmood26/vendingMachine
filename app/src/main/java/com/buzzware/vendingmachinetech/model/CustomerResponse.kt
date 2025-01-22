package com.buzzware.vendingmachinetech.model

data class CustomerResponse(
    val success : Int = 0,
    val cust_id : String = "",
    val account_id : String = ""
)
