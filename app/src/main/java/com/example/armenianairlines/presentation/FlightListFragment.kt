package com.example.armenianairlines.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.armenianairlines.R
import com.example.armenianairlines.domain.viewModel.FlightListViewModel
import com.example.armenianairlines.presentation.adapter.FlightAdapter

private const val TAG = "FlightListFragment"

class FlightListFragment : Fragment(),FlightAdapter.OnItemClickListener {


    private lateinit var flightRecyclerView: RecyclerView
    private var adapter = FlightAdapter(this)
    private lateinit var linearLayout: LinearLayout

    private lateinit var flightListViewModel: FlightListViewModel

    private var callbacks:Callbacks?=null


    interface Callbacks{
        fun toDetail(id:Int)
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flight_list, container, false)
        flightRecyclerView = view.findViewById(R.id.recyclerViewFlightList)
        linearLayout=view.findViewById(R.id.linearLayoutConnectionError)
        flightRecyclerView.layoutManager = LinearLayoutManager(context)
        flightRecyclerView.adapter = adapter
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flightListViewModel=ViewModelProvider(this).get(FlightListViewModel::class.java)
        flightListViewModel.getFlights()
        flightListViewModel.flightList.observe(viewLifecycleOwner) { data ->
            adapter.setFlights(data)
            flightRecyclerView.visibility=View.VISIBLE
            linearLayout.visibility=View.GONE
            Log.d(TAG,"LiveDataChanged")
        }
        flightListViewModel.dataError.observe(viewLifecycleOwner){error ->
            if(error){
                linearLayout.visibility=View.VISIBLE
                flightRecyclerView.visibility=View.GONE
            }
        }
    }

    override fun onItemClick(position: Int) {
        val flight = adapter.getCurrentItem(position)
        callbacks?.toDetail(flight.id)
    }

}
