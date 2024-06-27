package com.example.armenianairlines.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.Flight
import com.example.armenianairlines.databinding.ItemSimpleFlightBinding

private const val TAG="SimpleFlightAdapter"

class SimpleFlightAdapter : RecyclerView.Adapter<SimpleFlightAdapter.FlightViewHolder>() {


    var flightList = ArrayList<Flight>()


    fun setFlights(flights: List<Flight>) {
        flightList = flights as ArrayList<Flight>
        notifyDataSetChanged()
    }

    fun clearFlights(){
        flightList.clear()
        Log.d(TAG,"CLEAR RESULT -> ${flightList}")
    }


    inner class FlightViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSimpleFlightBinding.bind(itemView)

        fun bind(flight: Flight) = with(binding) {
            textViewFlightDate.text = flight.flightDate
            textViewFlightNumber.text = flight.flightNumber
            textViewFlightDestination.text = "${flight.departureCity} - ${flight.arrivalCity}"
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_flight, parent, false)

        return FlightViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flightList.size
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flightList[position])
    }
}