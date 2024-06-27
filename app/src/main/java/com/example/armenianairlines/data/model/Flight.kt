package com.example.armenianairlines.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Flight(
    val arrivalAirport: String,
    val arrivalCity: String,
    val arrivalTime: String,
    val departureAirport: String,
    val departureCity: String,
    val departureTime: String,
    val flightNumber: String,
    val flightDate:String,
    @PrimaryKey
    val id: Int
)

