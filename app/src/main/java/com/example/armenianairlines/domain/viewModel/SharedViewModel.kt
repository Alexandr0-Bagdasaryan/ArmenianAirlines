package com.example.armenianairlines.domain.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.armenianairlines.data.api.FlightApi
import com.example.armenianairlines.data.api.RetrofitInstance
import com.example.armenianairlines.data.model.Flight
import com.example.armenianairlines.data.model.User
import com.example.armenianairlines.data.model.UserFlight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val DATABASE_URL =
    "https://armenianairlines-b663e-default-rtdb.europe-west1.firebasedatabase.app"
private const val TAG = "SharedViewModel"

class SharedViewModel : ViewModel() {

     val firebaseAuth: FirebaseAuth
     val dataBase: DatabaseReference
    private val retrofitService: FlightApi

    val sharedUser = MutableLiveData<User>()

    private var user = FirebaseAuth.getInstance().currentUser

    val flightList = MutableLiveData<List<Flight>>()


    init {
        firebaseAuth = FirebaseAuth.getInstance()
        dataBase = FirebaseDatabase.getInstance(DATABASE_URL).reference
        retrofitService = RetrofitInstance.getRetrofitInstance(RetrofitInstance.globalURL)
            .create(FlightApi::class.java)
    }


    fun setCurrentUser(){
        user=FirebaseAuth.getInstance().currentUser
    }

    fun setUser() {
        val user = firebaseAuth.currentUser?.uid!!
        val firebaseUser = dataBase.child("users").child(user)
        firebaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("name").getValue(String::class.java)
                val surName = dataSnapshot.child("surName").getValue(String::class.java)
                val email = dataSnapshot.child("email").getValue(String::class.java)
                val password = dataSnapshot.child("password").getValue(String::class.java)

                val user = User(name!!, surName!!, email!!, password!!)
                sharedUser.value = user
                Log.d(
                    "Firebase",
                    "Name: $name, SurName: $surName, Email: $email, Password: $password"
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "loadUser:onCancelled", databaseError.toException())
            }
        })
    }

    fun addFlight(flight: UserFlight) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            Log.d(TAG, "USER ID -> $userId")
            dataBase.child("users").child(userId).child("flights")
                .child(flight.flightNumber)
                .setValue(flight)
        }
    }


    init {
        if (user != null) {
            startTasksForAuthenticatedUser()
        } else {
            Log.e(TAG, "User is not authenticated")
        }
    }

    fun startTasksForAuthenticatedUser() {
        val ref = dataBase.child("users").child(user!!.uid).child("flights")
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