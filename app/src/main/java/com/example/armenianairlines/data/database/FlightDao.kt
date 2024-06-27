package com.example.armenianairlines.data.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.armenianairlines.data.model.Flight

@Dao
interface FlightDao {

    @Query("SELECT * FROM flight")
    fun getFlights():List<Flight>

    @Query("SELECT * FROM flight where arrivalCity=(:city)")
    fun getFlightsDestination(city:String):List<Flight>

    @Query("SELECT * FROM flight WHERE id=(:id)")
    fun getFlight(id: Int):Flight

    @Query("SELECT * FROM flight WHERE flightNumber=(:flightNumber)")
    fun getFlightByNumber(flightNumber: String):Flight

    @Insert
    fun addFlights(flights: List<Flight>)

    @Update
    fun updateFlight(flight: Flight)
}