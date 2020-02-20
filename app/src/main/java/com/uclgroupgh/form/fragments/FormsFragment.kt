package com.uclgroupgh.form.fragments


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.databinding.DataBindingUtil
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.FragmentFormsBinding
import com.uclgroupgh.form.form.dialogs.AllCollectorsDialog
import com.uclgroupgh.form.service.LocationService
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants

/**
 * A simple [Fragment] subclass.
 * Use the [FormsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FormsFragment : Fragment() {
    private var binding: FragmentFormsBinding? = null
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    internal var a: Activity? = null
    internal lateinit var locintent: Intent

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateLoc(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forms, container, false)
        val view = binding!!.root
        if (activity != null) {
            a = activity
        }
        binding!!.seedProduction.setOnClickListener { v -> allcollectors("Seed Production") }

        binding!!.fertilizerBlends.setOnClickListener { v -> allcollectors("Fertilizer Blends") }

        binding!!.extensionEvents.setOnClickListener { v -> allcollectors("Extension Events") }

        binding!!.enterprises.setOnClickListener { v -> allcollectors("Supported Enterprises") }

        binding!!.training.setOnClickListener { v -> allcollectors("Training") }

        binding!!.aggregationCentres.setOnClickListener { v -> allcollectors("Aggregation Centres") }

        return view
    }

    internal fun allcollectors(formtype: String) {
        AllCollectorsDialog.newInstance(formtype).show(fragmentManager!!, AllCollectorsDialog.TAG)
    }


    private fun turnonLocation() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun checkLocation(): Boolean {
        val lm =
            a!!.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        return !(!gps_enabled && !network_enabled)

    }

    private fun updateLoc(intent: Intent) {
        AndroidUtils.savePreferenceData(
            a!!,
            Constants.LAT_PREF,
            intent.getStringExtra(Constants.LAT_PREF)
        )
        AndroidUtils.savePreferenceData(
            a!!,
            Constants.LONG_PREF,
            intent.getStringExtra(Constants.LONG_PREF)
        )
    }

    override fun onResume() {
        super.onResume()
        if (checkLocation()) {
            locintent = Intent(context, LocationService::class.java)
            if (a != null) {
                a!!.startService(locintent)
                a!!.registerReceiver(
                    broadcastReceiver,
                    IntentFilter(LocationService.BROADCAST_ACTION)
                )
            }
        } else {
            turnonLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        if (checkLocation()) {
            if (a != null) {
                a!!.unregisterReceiver(broadcastReceiver)
                a!!.stopService(locintent)
            }
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FormsFragment {
            val fragment = FormsFragment()
            val args = Bundle()

            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
