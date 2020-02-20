package com.uclgroupgh.form.form.dialogs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import br.com.ilhasoft.support.validation.Validator
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogIndicatorsBinding

/**
 * A simple [Fragment] subclass.
 */
class IndicatorDialog : DialogFragment(), View.OnClickListener {
    internal lateinit var binding: DialogIndicatorsBinding
    internal lateinit var validator: Validator
    private var mParam1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_indicators, container, false)
        val view = binding.root
        validator = Validator(binding)
        binding.close.setOnClickListener(this)
        when (mParam1) {
            "1" -> {
                binding.fertilizerBlends.visibility = View.GONE
                binding.extensionEvents.visibility = View.GONE
                binding.enterprises.visibility = View.GONE
                binding.aggregationCentres.visibility = View.GONE
                binding.types.visibility = View.GONE
            }
            "2" -> {
                binding.seedProduction.visibility = View.GONE
                binding.extensionEvents.visibility = View.GONE
                binding.enterprises.visibility = View.GONE
                binding.aggregationCentres.visibility = View.GONE

                val type =
                    "\n[1]Type 1= Government, 2=Commercial, 3=Academic\n\n[2]Type of support: 1= Technical Assistance, 2=Equipment, 3= Physical upgrades, 4=Training"
                binding.types.text = type
            }
            "3" -> {
                binding.fertilizerBlends.visibility = View.GONE
                binding.seedProduction.visibility = View.GONE
                binding.enterprises.visibility = View.GONE
                binding.aggregationCentres.visibility = View.GONE
                binding.types.visibility = View.GONE
            }
            "4" -> {
                binding.fertilizerBlends.visibility = View.GONE
                binding.extensionEvents.visibility = View.GONE
                binding.seedProduction.visibility = View.GONE
                binding.aggregationCentres.visibility = View.GONE
                val type1 =
                    "\n[1]Type: 1= agro dealer, 2=seed producer, 3=aggregator, 4= post-harvest technology provider\n\n[2]Old = already supported by AGRA, New = the enterprise did not exist prior to AGRA’s support; the idea of the enterprise might have existed, or an individual may have been working by themselves or with unpaid family members prior to AGRA’s support – but the work was not organized as a business entity."
                binding.types.text = type1
            }
            "5" -> {
                binding.fertilizerBlends.visibility = View.GONE
                binding.extensionEvents.visibility = View.GONE
                binding.seedProduction.visibility = View.GONE
                binding.enterprises.visibility = View.GONE
                val type2 =
                    "\n[1]Crops: 1=maize, 2=sorghum, 3=millet, 4=rice, 5=wheat, 6=cowpea; 7=beans, 8=soybean, 9=teff, 10=pulses, 11=pigeon pea, 10=groundnuts, 11=cassava, 12=irish potato."
                binding.types.text = type2
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog!!.setCancelable(false)
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes.windowAnimations = R.style.traininganimate
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.close -> dismiss()
        }
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "IndicatorDialog"

        fun newInstance(formtype: String): IndicatorDialog {
            val dialog = IndicatorDialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, formtype)
            dialog.arguments = args
            return dialog
        }
    }

}
