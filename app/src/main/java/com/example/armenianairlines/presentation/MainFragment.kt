package com.example.armenianairlines.presentation


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.armenianairlines.R
import com.example.armenianairlines.domain.viewModel.MainFragmentViewModel
import com.example.armenianairlines.presentation.adapter.CarouselAdapter
import com.google.android.material.carousel.CarouselSnapHelper


private const val TAG="MainFragment"

class MainFragment : Fragment(),CarouselAdapter.OnItemClickListener {

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val carousel = view.findViewById<RecyclerView>(R.id.carousel_recycler_view)
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carousel)
        carouselAdapter = CarouselAdapter(requireContext(),this)
        carousel.adapter = carouselAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragmentViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        carouselAdapter.setDestinations(mainFragmentViewModel.listDestinations)
    }

    override fun onItemClick(position: Int) {
        val city=carouselAdapter.getCurrentItem(position)
        Log.d(TAG,"DESTINATION -> $city")
        val bottomSheetDestinationFragment = BottomSheetDestinationFragment(city)
        Log.d(TAG,"TITLE -> ${carouselAdapter.getCurrentItem(position).title}")
        bottomSheetDestinationFragment.show(parentFragmentManager, bottomSheetDestinationFragment.tag)
    }
}