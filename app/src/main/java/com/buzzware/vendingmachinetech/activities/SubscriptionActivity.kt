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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

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
    private var plan = ""

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
            plan = "monthly"
            Toast.makeText(this@SubscriptionActivity, "$subscription", Toast.LENGTH_SHORT).show()
        }

        binding.yearLayout.setOnClickListener {
            binding.yearRadio.setImageResource(R.drawable.ic_radio_check)
            binding.monthlyRadio.setImageResource(R.drawable.ic_radio_uncheck)
            subscription = annualFee
            plan = "annual"
            Toast.makeText(this@SubscriptionActivity, "$subscription", Toast.LENGTH_SHORT).show()

        }

        binding.getStartBtn.setOnClickListener {
            hitPaymentApi()
        }
    }

    private fun hitPaymentApi() {
        val totalAmount = (subscription.toLong() * 100)
        val response = mapOf(
            "customerid" to UserSession.user.stripeCustid,
            "currency" to "eur",
            "amount" to totalAmount.toString()
        )

        binding.progressBar.visibility = View.VISIBLE

        Controller.instance.paymentSheet(response).enqueue(object : Callback<PaymentSheetModel> {
            override fun onResponse(
                call: Call<PaymentSheetModel>,
                response: Response<PaymentSheetModel>
            ) {
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Log.d("LOGGER", "Sheet Response: ${response.body()}")
                    val body = response.body()
                    paymentClientSecret = body!!.paymentIntent
                    PaymentConfiguration.init(this@SubscriptionActivity, body.publishableKey)
                    customerConfig =
                        PaymentSheet.CustomerConfiguration(body.customer, body.ephemeralKey)
                    presentPaymentSheet()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@SubscriptionActivity,
                        "${response.errorBody()!!.string()} ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PaymentSheetModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@SubscriptionActivity,
                    "${t.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun presentPaymentSheet() {
        val configuration = PaymentSheet.Configuration.Builder("Example, Inc.")
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

    private fun orderSubscription() {

        binding.progressBar.visibility = View.VISIBLE
        val startDate = System.currentTimeMillis()
        var endDate: Long = 0

        endDate = if (plan == "monthly") {
            millisecondsFromNowToOneMonth()
        } else {
            millisecondsFromNowToOneYear()
        }

        val subscription = hashMapOf(
            "createdDate" to System.currentTimeMillis(),
            "endDate" to endDate,
            "fee" to subscription,
            "plan" to plan,
            "startDate" to startDate,
            "status" to true,
            "userId" to Firebase.auth.currentUser!!.uid
        )

        Firebase.firestore.collection("Subscriptions").document(Firebase.auth.currentUser!!.uid)
            .set(subscription).addOnCompleteListener {
                if (it.isSuccessful) {
                    val userDetails = hashMapOf(
                        "isPro" to true,
                        "subscriptionType" to plan
                    )
                    Firebase.firestore.collection("Users").document(UserSession.user.id)
                        .update(userDetails as Map<String, Any>).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            binding.progressBar.visibility = View.GONE
                            startActivity(Intent(this, DashBoardActivity::class.java))
                            overridePendingTransition(
                                androidx.appcompat.R.anim.abc_fade_in,
                                androidx.appcompat.R.anim.abc_fade_out
                            )
                            finish()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "${it.exception!!.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun millisecondsFromNowToOneYear(): Long {
        val now = System.currentTimeMillis()
        val oneYearInMillis = TimeUnit.DAYS.toMillis(365)
        return now + oneYearInMillis
    }

    private fun millisecondsFromNowToOneMonth(): Long {
        val now = System.currentTimeMillis()
        val oneMonthInMillis = TimeUnit.DAYS.toMillis(30)
        return now + oneMonthInMillis
    }

    private fun getPrices() {
        Firebase.firestore.collection("prices").document("67nK6qyuQdmHrtCR6qhj").get()
            .addOnCompleteListener {
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
                    Toast.makeText(this, it.exception!!.message.toString(), Toast.LENGTH_SHORT)
                        .show()
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