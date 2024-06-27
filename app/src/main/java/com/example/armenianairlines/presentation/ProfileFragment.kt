package com.example.armenianairlines.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.User
import com.example.armenianairlines.domain.viewModel.SharedViewModel
import com.example.armenianairlines.presentation.adapter.SimpleFlightAdapter
import com.google.firebase.auth.FirebaseAuth


private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {

    private lateinit var logOut: Button
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var name: String
    private lateinit var firebaseAuth: FirebaseAuth
    private var callbacks: Callbacks? = null

    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var simpleFlight: RecyclerView
    private lateinit var textEmptyList: TextView
    private var simpleFlightAdapter = SimpleFlightAdapter()

    interface Callbacks {
        fun logOut()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        logOut = view.findViewById(R.id.buttonLogOut)
        firebaseAuth = FirebaseAuth.getInstance()
        textName = view.findViewById(R.id.textName)
        textEmptyList = view.findViewById(R.id.textViewEmptyList)
        simpleFlight = view.findViewById(R.id.recyclerViewFlightList)
        simpleFlight.adapter = simpleFlightAdapter
        simpleFlight.layoutManager = LinearLayoutManager(context)
        textEmail = view.findViewById(R.id.textEmail)
        sharedViewModel.flightList.observe(viewLifecycleOwner) { data ->
            Log.d(TAG, "DATA -> $data")
            if (data.isNotEmpty()) {
                textEmptyList.visibility = View.GONE
                simpleFlight.visibility = View.VISIBLE
                simpleFlightAdapter.setFlights(data)
            } else {
                textEmptyList.visibility = View.VISIBLE
                simpleFlight.visibility = View.INVISIBLE
            }
        }
        sharedViewModel.sharedUser.observe(viewLifecycleOwner) { data ->
            setUserData(data)
        }
        logOut.setOnClickListener {
            firebaseAuth.signOut()
            callbacks?.logOut()
            simpleFlightAdapter.clearFlights()

        }
        return view
    }

    private fun setUserData(user: User) {
        textName.text = user.name
        textEmail.text = user.email
    }


}