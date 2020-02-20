package com.uclgroupgh.form.form.dialogs.aggregationcenters


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.ilhasoft.support.validation.Validator
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogAggregationcentresBinding
import com.uclgroupgh.form.entities.AggregationCenters
import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.form.dialogs.IndicatorDialog
import com.uclgroupgh.form.form.dialogs.previews.FormPreviews
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants
import com.uclgroupgh.form.utils.ListDialogFragment
import es.dmoral.toasty.Toasty
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AggregationCentresDialog : DialogFragment(), View.OnClickListener, PreviewClickListener,
    ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var binding: DialogAggregationcentresBinding
    internal lateinit var validator: Validator
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var listitems: Array<String>
    internal lateinit var centers: AggregationCenters
    internal var popupcomponent =
        arrayOf("construction", "refurbished", "warehouse", "silos", "pics", "storage", "currency")
    internal var popuptitle = arrayOf(
        "Select Construction",
        "Select Refurbished",
        "Select Warehouse",
        "Select Silo",
        "Select Pics Bags",
        "Select Stores",
        "Select Currency"
    )
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_aggregationcentres, container, false)
        val view = binding.root
        validator = Validator(binding)

        binding.close.setOnClickListener { v -> closeDialog() }
        binding.location.isEnabled = false
        val temp = "Lat: " + AndroidUtils.getPreferenceData(
            context!!,
            Constants.LAT_PREF,
            ""
        ) + ", Long: " + AndroidUtils.getPreferenceData(context!!, Constants.LONG_PREF, "")
        binding.location.setText(temp)
        binding.ind24.setOnClickListener(this)
        binding.ind23.setOnClickListener(this)
        binding.ind25.setOnClickListener(this)
        binding.currency.setOnClickListener(this)
        binding.newConstructiontype.setOnClickListener(this)
        binding.refurbishedtype.setOnClickListener(this)
        binding.storestype.setOnClickListener(this)
        binding.silotype.setOnClickListener(this)
        binding.picsbagstype.setOnClickListener(this)
        binding.warehousetype.setOnClickListener(this)
        binding.help.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        binding.next.setOnClickListener(this)
        return view
    }

    private fun closeDialog() {
        context?.let {
            MaterialDialog(it).show {
                title(res = R.string.close_dialog_title)
                cancelable(false)
                cornerRadius(16f)
                message(res = R.string.close_dialog)
                negativeButton(res = R.string.dialog_negative)
                positiveButton(res = R.string.dialog_positive) { dialog!!.dismiss() }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                closeDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog!!.setCancelable(false)
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.attributes.windowAnimations = R.style.traininganimate
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ind23 -> binding.volume.requestFocus()
            R.id.ind24 -> binding.newConstructiontype.requestFocus()
            R.id.ind25 -> binding.handlingCost.requestFocus()
            R.id.currency -> {
                listitems = AndroidUtils.currencies.toTypedArray()
                showOptionDialog(listitems, popuptitle[6], popupcomponent[6])
            }
            R.id.newConstructiontype -> {
                listitems = resources.getStringArray(R.array.type)
                showOptionDialog(listitems, popuptitle[0], popupcomponent[0])
            }
            R.id.refurbishedtype -> {
                listitems = resources.getStringArray(R.array.type)
                showOptionDialog(listitems, popuptitle[1], popupcomponent[1])
            }
            R.id.warehousetype -> {
                listitems = resources.getStringArray(R.array.type)
                showOptionDialog(listitems, popuptitle[2], popupcomponent[2])
            }
            R.id.silotype -> {
                listitems = resources.getStringArray(R.array.type)
                showOptionDialog(listitems, popuptitle[3], popupcomponent[3])
            }
            R.id.picsbagstype -> {
                listitems = resources.getStringArray(R.array.type)
                showOptionDialog(listitems, popuptitle[4], popupcomponent[4])
            }
            R.id.storestype -> {
                listitems = resources.getStringArray(R.array.type)
                showOptionDialog(listitems, popuptitle[5], popupcomponent[5])
            }
            R.id.next -> shownextform()
            R.id.back -> showpreviousform()
            R.id.save -> saveForm()
            R.id.help -> {
                val dialog = IndicatorDialog.newInstance("5")
                dialog.show(fragmentManager!!, IndicatorDialog.TAG)
            }
        }
    }

    private fun shownextform() {
        binding.form1.visibility = View.GONE
        binding.form2.visibility = View.VISIBLE
        binding.next.visibility = View.INVISIBLE
        binding.addsave.visibility = View.VISIBLE
    }

    private fun showpreviousform() {
        binding.form1.visibility = View.VISIBLE
        binding.form2.visibility = View.GONE
        binding.next.visibility = View.VISIBLE
        binding.addsave.visibility = View.INVISIBLE
    }

    private fun saveForm() {
        if (validator.validate()) {
            try {
                centers = AggregationCenters()
                centers.storageFacilityName =
                    binding.storagefacilityname.text.toString().trim { it <= ' ' }
                centers.location = binding.location.text.toString().trim { it <= ' ' }
                centers.newConstructionType =
                    binding.newConstructiontype.text.toString().trim { it <= ' ' }
                centers.refurbishedType = binding.refurbishedtype.text.toString().trim { it <= ' ' }
                centers.warehouseType = binding.warehousetype.text.toString().trim { it <= ' ' }
                centers.silosType = binding.silotype.text.toString().trim { it <= ' ' }
                centers.storesType = binding.storestype.text.toString().trim { it <= ' ' }
                centers.picsbagsType = binding.picsbagstype.text.toString().trim { it <= ' ' }
                centers.volume = binding.volume.text.toString().trim { it <= ' ' }
                centers.crop = binding.crops.text.toString().trim { it <= ' ' }
                centers.quantityStored = binding.quantitystored.text.toString().trim { it <= ' ' }
                centers.handlingCost = binding.handlingCost.text.toString().trim { it <= ' ' }
                centers.currency = binding.currency.text.toString().trim { it <= ' ' }
                centers.servedFarmers = binding.farmersserved.text.toString().trim { it <= ' ' }
                centers.quantitySold = binding.quantitySold.text.toString().trim { it <= ' ' }
                centers.averagePrice = binding.averageprice.text.toString().trim { it <= ' ' }
                centers.collectorid = mParam1
                centers.collectorName = AndroidUtils.getCollectorname(mParam1)
                centers.agentname =
                    AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                centers.imei = AndroidUtils.getIMEI(context)
                centers.macaddress = AndroidUtils.getMacAddress(context)
                centers.uniqueuid = AndroidUtils.uuid
                centers.entrydate = Date()
                centers.save()
                fragmentManager?.let {
                    FormPreviews.newInstance(2, centers.getUniqueuid(), this)
                        .show(it, FormPreviews.TAG)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else
            Toasty.error(context!!, "An error occurred").show()
    }

    fun uploadToServer() {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                internal var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {

                        try {
                            val forms = FilledForms()
                            forms.category = mParam1
                            forms.datecreated = Date()
                            forms.uniqueuid = AndroidUtils.uuid
                            forms.imei = AndroidUtils.getIMEI(context)
                            forms.macaddress = AndroidUtils.getMacAddress(context)
                            forms.save()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val transfer = ServerTransfer()
                        transfer.aggregationCenters = centers
                        val jsonstring = Gson().toJson(transfer)
                        val uploadId = AndroidUtils.uuid
                        val uploadfilepath = AndroidUtils.writeToFile(jsonstring, uploadId + ".txt")

                        AndroidUtils.uploadFileToServer(context)
                        outcome = true
                    } catch (e: Exception) {
                        println("Exception" + e.message)
                        e.printStackTrace()
                    }

                    return outcome
                }

                override fun onPostExecute(outcome: Boolean?) {

                }


            }
        asyncTask.execute()
    }

    override fun onListItemSelected(selection: String) {
        val componentname = dialogFragment.arguments!!.get("componentname") as String
        when {
            componentname.equals(popupcomponent[0], ignoreCase = true) -> {
                binding.newConstructiontype.setText(selection)
            }
            componentname.equals(popupcomponent[1], ignoreCase = true) -> {
                binding.refurbishedtype.setText(selection)
            }
            componentname.equals(popupcomponent[2], ignoreCase = true) -> {
                binding.warehousetype.setText(selection)
            }
            componentname.equals(popupcomponent[3], ignoreCase = true) -> {
                binding.silotype.setText(selection)
            }
            componentname.equals(popupcomponent[4], ignoreCase = true) -> {
                binding.picsbagstype.setText(selection)
            }
            componentname.equals(popupcomponent[5], ignoreCase = true) -> {
                binding.storestype.setText(selection)
            }
            componentname.equals(popupcomponent[6], ignoreCase = true) -> {
                binding.currency.setText(selection)
            }
        }
    }

    private fun showOptionDialog(array: Array<String>, title: String, componentname: String) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, title, componentname)
        dialogFragment.show(fm, componentname)
    }

    override fun onPreviewClickListener(save: Boolean, dialog: Dialog) {
        if (save) {
            uploadToServer()
            Toasty.success(context!!, "Form saved").show()
            dismiss()
        } else
            centers.delete()
        dialog.dismiss()
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(collectorid: String): AggregationCentresDialog {
            val dialog = AggregationCentresDialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, collectorid)
            dialog.arguments = args
            return dialog
        }
    }
}
