package com.uclgroupgh.form.fragments

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.FragmentIpsettingsBinding
import com.uclgroupgh.form.pojo.IPSettingsPojo
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants

import es.dmoral.toasty.Toasty

class IPSettingsFragment : DialogFragment() {
    internal lateinit var binding: FragmentIpsettingsBinding
    internal lateinit var ip: IPSettingsPojo
    private var mParam1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ipsettings, container, false)
        val view = binding.root
        dialog!!.setTitle("IP Settings")
        ip = IPSettingsPojo()
        binding.ip = ip

        loadDialogInfo()
        binding.btnsave.setOnClickListener { view1 ->
            AndroidUtils.savePreferenceData(
                activity!!,
                Constants.IPPREF,
                AndroidUtils.tolower(ip.ipaddress.get()!!)
            )
            AndroidUtils.savePreferenceData(
                activity!!,
                Constants.PROTOCOLPREF,
                AndroidUtils.tolower(ip.protocol.get()!!)
            )
            AndroidUtils.savePreferenceData(
                activity!!,
                Constants.ENDPOINTPREF,
                AndroidUtils.tolower(ip.context.get()!!)
            )
            AndroidUtils.savePreferenceData(activity!!, Constants.PORT, ip.port.get())
            AndroidUtils.savePreferenceData(
                activity!!,
                Constants.IPCOMPLETEPREF,
                binding.txtcomplete.text.toString()
            )
            Toasty.success(activity!!, "IP Address Saved Successfully").show()

            AndroidUtils.uploadFileToServer(context)
            dismiss()
        }
        return view
    }

    //method to load dialog info
    fun loadDialogInfo() {
        ip.ipaddress.set(
            AndroidUtils.getPreferenceData(
                activity!!,
                Constants.IPPREF,
                "webservice.uclgroupgh.com"
            )
        )
        ip.port.set(AndroidUtils.getPreferenceData(activity!!, Constants.PORT, "8080"))
        ip.protocol.set(AndroidUtils.getPreferenceData(activity!!, Constants.PROTOCOLPREF, "http"))
        ip.context.set(
            AndroidUtils.getPreferenceData(
                activity!!,
                Constants.ENDPOINTPREF,
                "uclservice/uclservice"
            )
        )
    }

    companion object {
        private val ARG_PARAM1 = "param1"

        fun newInstance(param1: String): IPSettingsFragment {
            val fragment = IPSettingsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }

}
