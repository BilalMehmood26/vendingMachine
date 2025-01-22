package com.buzzware.vendingmachinetech.model

data class PaymentSheetModel(
    var paymentIntent : String = "",
    var ephemeralKey : String = "",
    var customer : String = "",
    var publishableKey : String = ""
)
