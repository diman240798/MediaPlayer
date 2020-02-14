package com.nanicky.devteam.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.nanicky.devteam.R
import com.nanicky.devteam.main.settings.ColorChangeSharedObject
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject

class MainFragment : Fragment() {

    private val colorChangeSharedObject: ColorChangeSharedObject by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireActivity(), R.id.bottomNavHostFragment)
        navigationBar.setupWithNavController(navController)
        colorChangeSharedObject.backgrColorBottomNavView.observe(viewLifecycleOwner, Observer { navigationBar.setBackgroundColor(resources.getColor(it)) })
    }
}
