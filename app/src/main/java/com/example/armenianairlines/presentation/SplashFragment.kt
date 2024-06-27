package com.example.armenianairlines.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.armenianairlines.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SplashFragment:Fragment() {

    private lateinit var textViewAirlineNamel:TextView
    private lateinit var imageViewAirlineLogo:ImageView

    private lateinit var firebaseAuth: FirebaseAuth
    var user:FirebaseUser? = null

    private var callbacks: Callbacks? = null

    interface Callbacks{
        fun toMenu()
        fun toAccount()
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
       val view = inflater.inflate(R.layout.fragment_splash,container,false)
        firebaseAuth=FirebaseAuth.getInstance()
        user= firebaseAuth.currentUser
        imageViewAirlineLogo=view.findViewById(R.id.imageViewAirlineLogo)
        val animationAlpha=AnimationUtils.loadAnimation(requireContext(),R.anim.alpha)
        animationAlpha.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                if (user!=null){
                    callbacks?.toMenu()
                }
                else{
                    callbacks?.toAccount()
                }

            }
        })
        imageViewAirlineLogo.startAnimation(animationAlpha)
        textViewAirlineNamel=view.findViewById(R.id.textViewAirlineName)
        val animationSlideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        textViewAirlineNamel.startAnimation(animationSlideDown)

        return view
    }


}