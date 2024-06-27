package com.example.armenianairlines.domain.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.armenianairlines.data.api.FlightApi
import com.example.armenianairlines.data.api.RetrofitInstance
import com.example.armenianairlines.data.model.Flight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val DATABASE_URL =
    "https://armenianairlines-b663e-default-rtdb.europe-west1.firebasedatabase.app"


private const val TAG="ProfileViewModel"

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val flightList = MutableLiveData<List<Flight>>()

    private val databaseFirebaseDatabase =
        FirebaseDatabase.getInstance(DATABASE_URL).reference
    private val user = FirebaseAuth.getInstance().currentUser
    private val retrofitService = RetrofitInstance.getRetrofitInstance(RetrofitInstance.globalURL)
        .create(FlightApi::class.java)

    init {
        if (user != null) {
            startTasksForAuthenticatedUser()
        } else {
            Log.e(TAG, "User is not authenticated")
        }
    }

     fun startTasksForAuthenticatedUser() {
        val ref = databaseFirebaseDatabase.child("users").child(user!!.uid).child("flights")
        val flightNumbers = mutableListOf<String>()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let {
                        flightNumbers.add(it)
                    }
                }
                fetchFlights(flightNumbers)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read user flights", error.toException())
            }
        })
    }

    private fun fetchFlights(flightNumbers: List<String>) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val list = mutableListOf<Flight>()
            for (number in flightNumbers) {
                try {
                    val apiFlight = retrofitService.getFlightByNumber(number)
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "LOOP NUMBER -> $number")
                        Log.d(TAG, "LOOP DATA -> $apiFlight")
                    }
                    list.add(apiFlight)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching flight $number", e)
                }
            }
            withContext(Dispatchers.Main) {
                Log.d(TAG, "PROFILE VM DATA -> $list")
                flightList.value = list
            }
        }
    }
}