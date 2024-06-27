package com.example.armenianairlines.domain.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.armenianairlines.data.database.AppDatabase
import com.example.armenianairlines.data.model.Flight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private var TAG="FlightDetailViewModel"

class FlightDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val flightDao = database.flightDao()

    var flight = MutableLiveData<Flight>()

    fun getFlight(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            flight.postValue(flightDao.getFlight(id))
        }
    }

}