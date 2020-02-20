package com.uclgroupgh.form.login.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.uclgroupgh.form.R


class LogoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logo, container, false)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        fun newInstance(): LogoFragment {
            return LogoFragment()
        }
    }

}// Required empty public constructor
