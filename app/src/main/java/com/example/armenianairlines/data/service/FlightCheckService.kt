package com.example.armenianairlines.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.armenianairlines.MainActivity
import com.example.armenianairlines.R
import com.example.armenianairlines.data.api.FlightApi
import com.example.armenianairlines.data.api.RetrofitInstance
import com.example.armenianairlines.data.database.AppDatabase
import com.example.armenianairlines.data.database.FlightDao
import com.example.armenianairlines.data.model.Flight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

private const val TAG = "FlightCheckService"
private const val DATABASE_URL =
    "https://armenianairlines-b663e-default-rtdb.europe-west1.firebasedatabase.app"
private const val FETCH_INTERVAL = 1 * 60 * 1000L

class FlightCheckService : Service() {

    private val NOTIFICATION_ID = 1
    private var isDestroyed = false

    private lateinit var database: AppDatabase
    private lateinit var databaseFirebaseDatabase: DatabaseReference
    private lateinit var flightDao: FlightDao
    private lateinit var retrofitService: FlightApi


    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            stopSelf()
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(application)
        flightDao = database.flightDao()
        databaseFirebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL).reference
        retrofitService = RetrofitInstance.getRetrofitInstance(RetrofitInstance.globalURL)
            .create(FlightApi::class.java)
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroyed = true
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkUserAuthenticationAndStartTasks()
        return START_NOT_STICKY
    }

    private fun checkUserAuthenticationAndStartTasks() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            while (isActive) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    Log.d(TAG,"USER IN SERVICE STARTED")
                    startTasksForAuthenticatedUser(user.uid)
                    break
                } else {
                    Log.d(TAG,"USER OUT SERVICE STOPPED")
                    stopSelf()
                    break
                }
                delay(FETCH_INTERVAL)
            }
        }
    }

    private fun startTasksForAuthenticatedUser(uid: String) {
        val ref = databaseFirebaseDatabase.child("users").child(uid).child("flights")
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            while (!isDestroyed) {
                val user = FirebaseAuth.getInstance().currentUser
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val flightNumbers = mutableListOf<String>()
                        for (snapshot in dataSnapshot.children) {
                            snapshot.key?.let {
                                flightNumbers.add(it)
                                Log.d(TAG, "User flight fetched: $it")
                                Log.d(TAG,"USER -> ${uid}")
                            }
                        }
                        Log.d(TAG, "User flights fetched: $flightNumbers")
                        fetchFromApi(flightNumbers)
                    }


                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "Failed to read user flights", databaseError.toException())
                    }
                })
                delay(FETCH_INTERVAL)
            }
        }
    }

    private fun fetchFromApi(flightNumbers: List<String>) {
        Log.d(TAG, "Fetching data from API")
        val list = ArrayList<Flight>()
        CoroutineScope(Dispatchers.IO).launch {
            for (number in flightNumbers) {
                try {
                    val apiFlight = retrofitService.getFlightByNumber(number)
                    val localFlight = flightDao.getFlightByNumber(number)
                    Log.d(TAG, "Flights from API: $apiFlight")
                    Log.d(TAG, "Flights from local: $localFlight")
                    list.add(apiFlight)
                    if (!compareFlights(localFlight, apiFlight)) {
                        flightDao.updateFlight(apiFlight)
                        withContext(Dispatchers.Main) {
                            showNotification("Рейс $number изменен")
                        }
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTP error: ${e.code()} ${e.message()}")

                } catch (e: Exception) {
                    Log.e(TAG, "Unknown error: ${e.message}")

                }
            }
        }
    }

    private fun compareFlights(flight1: Flight, flight2: Flight): Boolean {
        return flight1.arrivalAirport == flight2.arrivalAirport &&
                flight1.arrivalCity == flight2.arrivalCity &&
                flight1.arrivalTime == flight2.arrivalTime &&
                flight1.departureAirport == flight2.departureAirport &&
                flight1.departureCity == flight2.departureCity &&
                flight1.departureTime == flight2.departureTime &&
                flight1.flightDate == flight2.flightDate
    }

    private fun showNotification(content: String) {
        val notification = NotificationCompat.Builder(this, MainActivity.channelID!!)
            .setContentTitle("Рейсы")
            .setContentText(content)
            .setSmallIcon(R.drawable.table_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setColor(ContextCompat.getColor(this,R.color.company_color))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


}