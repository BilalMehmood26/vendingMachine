package com.buzzware.vendingmachinetech.model

data class User(
    var id: String = "",
    var email: String = "",
    var userName: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var image: String = "",
    var token: String = "",
    var userLat: Double? = 0.0,
    var userLng: Double? = 0.0,
    var isActive: Boolean = true,
    var isPro: Boolean = false,
    var subscriptionType: String = "trail",
    var userType: String = "user",
    var createdAt: Long? = 0
)



