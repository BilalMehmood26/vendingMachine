package com.buzzware.vendingmachinetech.stripe

import com.buzzware.vendingmachinetech.model.CustomerResponse
import com.buzzware.vendingmachinetech.model.PaymentSheetModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("widgetsforusa/payment-sheet2")
    @JvmSuppressWildcards
    fun paymentSheet(@Body body: Map<String, Any>): Call<PaymentSheetModel>

    @POST("/widgetsforusa/checkcustidexistornot")
    @JvmSuppressWildcards
    suspend fun createCustomer(@Body body: Map<String, Any>): Response<CustomerResponse>
}