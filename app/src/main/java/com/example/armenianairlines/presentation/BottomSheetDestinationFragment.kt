package com.example.armenianairlines.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.CarouselModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val TAG = "BottomSheetFragment"

class BottomSheetDestinationFragment(val destination: CarouselModel) : BottomSheetDialogFragment() {


    lateinit var title: TextView
    lateinit var description: TextView
    lateinit var image:ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_destination, container, false)
        title=view.findViewById(R.id.textViewCity)
        description=view.findViewById(R.id.textViewCityDescription)
        image=view.findViewById(R.id.textViewCityImage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDestination()
    }

    private fun setDestination(){
        title.text=destination.title
        image.setImageResource(destination.imageId)
        description.text=destination.description
    }

}