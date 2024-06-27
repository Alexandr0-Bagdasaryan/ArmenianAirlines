package com.example.armenianairlines.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.armenianairlines.R
import com.example.armenianairlines.domain.viewModel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG: String = "MenuFragment"

class MenuFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.startTasksForAuthenticatedUser()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationMain =
            view.findViewById<BottomNavigationView>(R.id.bottomNavigationMain)
        val navController =
            (childFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment).navController
        NavigationUI.setupWithNavController(bottomNavigationMain, navController)
        sharedViewModel.setUser()
        bottomNavigationMain.setupWithNavController(navController)
    }
}