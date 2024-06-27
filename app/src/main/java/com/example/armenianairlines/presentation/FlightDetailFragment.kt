package com.example.armenianairlines.presentation

import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import com.example.armenianairlines.MainActivity
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.Flight
import com.example.armenianairlines.data.model.UserFlight
import com.example.armenianairlines.domain.viewModel.FlightDetailViewModel
import com.example.armenianairlines.domain.viewModel.SharedViewModel


private const val TAG = "FlightDetailFragment"

class FlightDetailFragment() : Fragment() {


    private lateinit var gridLayoutSeats: GridLayout
    private var selectedSeatButton: Button? = null
    private lateinit var departureTime: TextView
    private lateinit var arrivalTime: TextView
    private lateinit var departureCity: TextView
    private lateinit var arrivalCity: TextView
    private lateinit var arrivalAirport: TextView
    private lateinit var departureAirport: TextView

    private var idToGetFlight: Int = -1
    private lateinit var currFlight: Flight

    private lateinit var flightDetailViewModel: FlightDetailViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()



    private var callbacks:Callbacks?=null


    interface Callbacks{
        fun toList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks =context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flight_detail, container, false)
        departureTime = view.findViewById(R.id.departureTime)
        arrivalTime = view.findViewById(R.id.arrivalTime)
        departureCity = view.findViewById(R.id.departureCity)
        arrivalCity = view.findViewById(R.id.arrivalCity)
        arrivalAirport = view.findViewById(R.id.arrivalAirport)
        departureAirport = view.findViewById(R.id.departureAirport)

        gridLayoutSeats = view.findViewById(R.id.gridLayoutSeats)

        arguments?.let {
            idToGetFlight = it.getInt("flight")
            Log.d(TAG, "ID OF CURRFLIGHT -> ${idToGetFlight}")
        }

        val buttonBuy: Button = view.findViewById(R.id.buttonBuy)

        createSeats()

        buttonBuy.setOnClickListener {
            buyTicket()
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        flightDetailViewModel = ViewModelProvider(this).get(FlightDetailViewModel::class.java)
        flightDetailViewModel.getFlight(idToGetFlight)
        flightDetailViewModel.flight.observe(this.viewLifecycleOwner) {
            currFlight = it
            setData()
        }
    }



    private fun createNotification() {
        val pendingIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.application_nav)
            .setDestination(R.id.menuFragment)
            .setArguments(Bundle().apply {
                putInt("flightId", 123)
            })
            .createPendingIntent()

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(requireContext(), MainActivity.channelID!!)
            .setContentTitle("Билеты")
            .setContentText("Поздравляем с покупкой билетов в город ${currFlight.arrivalCity}!")
            .setSmallIcon(R.drawable.main_icon)
            .setColor(ContextCompat.getColor(requireContext(),R.color.company_color))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notification = notificationBuilder.build()
        notificationManager.notify(1, notification)
    }

    private fun createSeats() {
        val rows = 20
        val columns = 4
        val seatLetters = arrayOf('A', 'B', 'C', 'D')

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val seatButton = Button(context).apply {
                    text = "${row + 1}${seatLetters[column]}"
                    setBackgroundColor(Color.GREEN)
                    setOnClickListener {
                        selectSeat(this)
                        Log.d(TAG, "SEAT -> ${this.text}")
                    }
                }

                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(column)
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setMargins(4, 4, 4, 4)
                }

                Log.d(TAG, "SEAT CREATED: ${seatButton.text}")

                gridLayoutSeats.addView(seatButton, params)
            }
        }
    }

    private fun selectSeat(seatButton: Button) {

        selectedSeatButton?.let {
            it.setBackgroundColor(Color.GREEN)
            it.setTextColor(Color.BLACK)
        }


        selectedSeatButton = seatButton.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.company_color))
            setTextColor(Color.WHITE)
        }
    }

    private fun setData() {
        departureTime.text = currFlight.departureTime
        arrivalTime.text = currFlight.arrivalTime
        departureCity.text = currFlight.departureCity
        arrivalCity.text = currFlight.arrivalCity
        arrivalAirport.text = currFlight.arrivalAirport
        departureAirport.text = currFlight.departureAirport
    }

    private fun buyTicket() {
        selectedSeatButton?.let {
            it.setBackgroundColor(Color.RED)
            val flight = UserFlight(
                currFlight.flightNumber,
                currFlight.flightDate,
                "Registered",
                selectedSeatButton?.text.toString(),
                "Standart"
            )
            sharedViewModel.addFlight(flight)
            Toast.makeText(context, "Билет куплен!", Toast.LENGTH_SHORT).show()
            createNotification()
            selectedSeatButton = null
            it.isEnabled = false
            callbacks?.toList()
        } ?: run {
            Toast.makeText(context, "Выберите место перед покупкой", Toast.LENGTH_SHORT).show()
        }
    }
}