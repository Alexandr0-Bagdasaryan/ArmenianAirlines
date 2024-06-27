package com.example.armenianairlines.data.api


import com.example.armenianairlines.data.model.Flight
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface FlightApi {
    @GET("flights")
    suspend fun getFlights(): List<Flight>

    @GET("flights/{flightNumber}")
    suspend fun getFlightByNumber(@Path("flightNumber") flightNumber: String): Flight

}


