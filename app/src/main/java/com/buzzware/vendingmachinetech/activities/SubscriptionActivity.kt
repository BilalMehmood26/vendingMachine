package com.buzzware.vendingmachinetech.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivitySubscriptionBinding
import com.buzzware.vendingmachinetech.model.PaymentSheetModel
import com.buzzware.vendingmachinetech.model.Prices
import com.buzzware.vendingmachinetech.stripe.Controller
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscriptionActivity : AppCompatActivity() {

    private val binding: ActivitySubscriptionBinding by lazy {
        ActivitySubscriptionBinding.inflate(layoutInflater)
    }
    private val tag = "SubscriptionActivity_TAG"

    private lateinit var paymentSheet: PaymentSheet
    private var paymentClientSecret: String = ""
    private var customerConfig: PaymentSheet.CustomerConfiguration? = null

    private var monthlyFee = ""
    private var annualFee = ""
    private var subscription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        paymentSheet = PaymentSheet(this@SubscriptionActivity, this::onPaymentSheetResult)

        getPrices()
        setListener()

    }

    private fun setListener() {

        binding.backIV.setOnClickListener { onBackPressed() }

        binding.monthlyLayout.setOnClickListener {
            binding.monthlyRadio.setImageResource(R.drawable.ic_radio_check)
            binding.yearRadio.setImageResource(R.drawable.ic_radio_uncheck)
            subscription = monthlyFee
        }

        binding.yearLayout.setOnClickListener {
            binding.yearRadio.setImageResource(R.drawable.ic_radio_check)
            binding.monthlyRadio.setImageResource(R.drawable.ic_radio_uncheck)
            subscription = annualFee
        }

        binding.getStartBtn.setOnClickListener {
           /* startActivity(Intent(this, DashBoardActivity::class.java))
            overridePendingTransition(
                androidx.appcompat.R.anim.abc_fade_in,
                androidx.appcompat.R.anim.abc_fade_out
            )*/

            hitPaymentApi()
        }
    }

    private fun hitPaymentApi() {

        val response = mapOf(
            "customerid" to UserSession.user.stripeCustid,
            "currency" to "usd",
            "amount" to subscription
        )

        binding.progressBar.visibility = View.VISIBLE

        Controller.instance.paymentSheet(response).enqueue(object:Callback<PaymentSheetModel>{
            override fun onResponse(
                call: Call<PaymentSheetModel>,
                response: Response<PaymentSheetModel>
            ) {
                if (response.isSuccessful){
                    binding.progressBar.visibility = View.GONE
                    Log.d("LOGGER", "Sheet Response: ${response.body()}")
                    val body = response.body()
                    paymentClientSecret = body!!.paymentIntent
                    PaymentConfiguration.init(this@SubscriptionActivity, body.publishableKey)
                    customerConfig = PaymentSheet.CustomerConfiguration(body.customer, body.ephemeralKey)
                    presentPaymentSheet()
                }
            }

            override fun onFailure(call: Call<PaymentSheetModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SubscriptionActivity, "${t.message.toString()}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun presentPaymentSheet() {
        val configuration =  PaymentSheet.Configuration.Builder("Example, Inc.")
            .customer(customerConfig)
            .allowsDelayedPaymentMethods(true)
            .build()
        paymentSheet.presentWithPaymentIntent(paymentClientSecret, configuration)

    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Completed -> {
                orderSubscription()
            }
        }
    }

    private fun orderSubscription(){

        Toast.makeText(this@SubscriptionActivity, "orderSubscription", Toast.LENGTH_SHORT).show()
    }

    private fun getPrices() {
        Firebase.firestore.collection("prices").document("67nK6qyuQdmHrtCR6qhj").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val prices = it.result.toObject(Prices::class.java)
                monthlyFee = prices!!.monthly_fee.toString()
                annualFee = prices.yearly_fee.toString()
                Log.d(tag, "getPrices: $monthlyFee $annualFee")
                binding.apply {
                    monthlyPrice.text = "$monthlyFee €"
                    annualTv.text = "$annualFee €"
                }
            } else {
                Toast.makeText(this, it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_fade_out
        )
    }
}