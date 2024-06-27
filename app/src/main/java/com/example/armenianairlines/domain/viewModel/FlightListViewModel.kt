package com.example.armenianairlines.domain.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.armenianairlines.data.api.FlightApi
import com.example.armenianairlines.data.api.RetrofitInstance
import com.example.armenianairlines.data.database.AppDatabase
import com.example.armenianairlines.data.model.Flight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG="FlightListViewModel"

class FlightListViewModel(application: Application) : AndroidViewModel(application) {

    val flightList = MutableLiveData<List<Flight>>()
    val dataError = MutableLiveData<Boolean>()

    private val database = AppDatabase.getInstance(application)
    private val flightDao = database.flightDao()

    private val retrofitService = RetrofitInstance.getRetrofitInstance(RetrofitInstance.globalURL)
        .create(FlightApi::class.java)

    init {
        getFlights()
    }

    private fun getFlightsToDb(flightsFromServer: List<Flight>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val flightsFromDb = flightDao.getFlights()
                val newFlights = flightsFromServer.filter { serverFlight ->
                    flightsFromDb.none { dbFlight -> dbFlight.id == serverFlight.id }
                }
                if (newFlights.isNotEmpty()) {
                    flightDao.addFlights(newFlights)
                    Log.d(TAG, "New data saved to DB")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving data to DB", e)
            }
        }
    }

    fun getFlights() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbFlights = flightDao.getFlights()
                if (dbFlights.isNotEmpty()) {
                    flightList.postValue(dbFlights)
                    Log.d(TAG, "Data loaded from DB")
                } else {
                    val response = retrofitService.getFlights()
                    flightList.postValue(response)
                    Log.d(TAG, "LiveDataGetData")
                    getFlightsToDb(response)
                }
            } catch (e: Exception) {
                dataError.postValue(true)
                Log.e(TAG, "Error fetching data", e)
            }
        }
    }
}