package com.example.armenianairlines.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.Flight
import com.example.armenianairlines.databinding.ItemFlightBinding


private const val TAG="FlightAdapter"

class FlightAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {


    var flightList = ArrayList<Flight>()


    fun setFlights(flights:List<Flight>){
        flightList= flights as ArrayList<Flight>
        notifyDataSetChanged()
    }


    inner class FlightViewHolder(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val binding = ItemFlightBinding.bind(itemView)

        fun bind(flight: Flight) = with(binding) {
            textViewArrivalCity.text = flight.arrivalCity
            textViewArrivalTime.text = flight.arrivalTime
            textViewDepartureCity.text = flight.departureCity
            textViewDepartureTime.text = flight.departureTime
            textViewFlightDate.text = flight.flightDate
            textViewFlightNumber.text = flight.flightNumber
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                Log.d(TAG,"FLIGHT CLICKED")
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight, parent, false)

        return FlightViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return flightList.size
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flightList[position])
    }

    fun getCurrentItem(position: Int): Flight {
        return flightList[position]
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}