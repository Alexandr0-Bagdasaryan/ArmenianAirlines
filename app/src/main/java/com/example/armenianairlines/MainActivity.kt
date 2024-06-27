package com.example.armenianairlines

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.armenianairlines.data.service.FlightCheckService
import com.example.armenianairlines.presentation.FlightDetailFragment
import com.example.armenianairlines.presentation.FlightListFragment
import com.example.armenianairlines.presentation.LoginSignFragment
import com.example.armenianairlines.presentation.ProfileFragment
import com.example.armenianairlines.presentation.SplashFragment
import com.google.firebase.auth.FirebaseAuth


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), SplashFragment.Callbacks,
    LoginSignFragment.CallBacks, ProfileFragment.Callbacks, FlightListFragment.Callbacks,
    FlightDetailFragment.Callbacks {

    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private var foregroundServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        channelID = createNotificationChannel(this)


        foregroundServiceIntent = Intent(this, FlightCheckService::class.java)
        auth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG,"STARTED SERVICE")
                startService(foregroundServiceIntent)
            } else {
                Log.d(TAG,"STOPED SERVICE")
                stopService(foregroundServiceIntent)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG,"STARTED USER LISTNER")
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"STOPPED USER LISTNER")
        auth.removeAuthStateListener(authStateListener)
    }

    companion object {
        var channelID: String? = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(foregroundServiceIntent)
    }


    private fun createNotificationChannel(context: Context): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Channel_id"
            val channelName: CharSequence = "ArmenianAirlines"
            val channelDescription = "ArmenianAirlines Alert"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
            notificationChannel.description = channelDescription
            notificationChannel.enableVibration(true)
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            channelId
        } else {
            null
        }
    }

    override fun toMenu() {
        findNavController(R.id.fragment_container).navigate(
            R.id.action_splashFragment_to_menuFragment
        )
    }

    override fun toAccount() {
        findNavController(R.id.fragment_container).navigate(
            R.id.action_splashFragment_to_loginSignFragment
        )
    }

    override fun loginToMenu() {
        findNavController(R.id.fragment_container).navigate(
            R.id.action_loginSignFragment_to_menuFragment
        )
    }

    override fun logOut() {
        findNavController(R.id.fragment_container).navigate(
            R.id.action_menuFragment_to_loginSignFragment
        )
    }

    override fun toDetail(id: Int) {
        val bundle = Bundle().apply {
            putInt("flight", id)
        }
        findNavController(R.id.fragment_container).navigate(
            R.id.action_menuFragment_to_flightDetailFragment,
            bundle
        )
    }

    override fun toList() {
        findNavController(R.id.fragment_container).popBackStack()
    }
}