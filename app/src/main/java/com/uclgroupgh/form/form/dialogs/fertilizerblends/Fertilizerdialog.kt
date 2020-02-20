package com.uclgroupgh.form.form.dialogs.fertilizerblends


import android.annotation.SuppressLint
import android.app.Dialog
import androidx.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogFertilizerblendsBinding
import com.uclgroupgh.form.entities.FertilizerBlending
import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.form.dialogs.IndicatorDialog
import com.uclgroupgh.form.form.dialogs.previews.FormPreviews
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.ListDialogFragment

import java.util.ArrayList
import java.util.Date

import br.com.ilhasoft.support.validation.Validator
import com.uclgroupgh.form.utils.Constants
import es.dmoral.toasty.Toasty

/**
 * A simple [Fragment] subclass.
 */
class Fertilizerdialog : DialogFragment(), View.OnClickListener, PreviewClickListener,
    ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var fertilizerBlendingList: List<FertilizerBlending>
    internal lateinit var binding: DialogFertilizerblendsBinding
    internal lateinit var validator: Validator
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var listitems: Array<String>
    internal lateinit var blending: FertilizerBlending
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_fertilizerblends, container, false)
        val view = binding.root
        validator = Validator(binding)
        fertilizerBlendingList = ArrayList()
        binding.typeofsoillab.setOnClickListener(this)
        binding.typeofsupport.setOnClickListener(this)
        binding.ind11.setOnClickListener(this)
        binding.ind12.setOnClickListener(this)
        binding.ind74.setOnClickListener(this)
        binding.indbmz.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.help.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        return view
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
            R.id.ind11 -> binding.typeofsoillab.requestFocus()
            R.id.ind12 -> binding.noofblends.requestFocus()
            R.id.ind74 -> binding.companies.requestFocus()
            R.id.indbmz -> binding.quantityProduced.requestFocus()
            R.id.typeofsoillab -> {
                listitems = resources.getStringArray(R.array.typeofsoillab)
                showOptionDialog(listitems, "Select Type of Soil Lab", "tosl")
            }
            R.id.typeofsupport -> {
                listitems = resources.getStringArray(R.array.typeofsupport)
                showOptionDialog(listitems, "Select Type of Soil Lab", "tos")
            }
            R.id.save -> saveForm()
            R.id.help -> {
                val dialog = IndicatorDialog.newInstance("2")
                dialog.show(fragmentManager!!, IndicatorDialog.TAG)
            }
            R.id.close -> closeDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                closeDialog()
            }
        }
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

    private fun saveForm() {
        if (validator.validate()) {
            try {
                blending = FertilizerBlending()
                blending.soillabname = binding.nameofsoillab.text.toString().trim { it <= ' ' }
                blending.soillabtest = binding.typeofsoillab.text.toString().trim { it <= ' ' }
                blending.numberofblends =
                    Integer.parseInt(binding.noofblends.text.toString().trim { it <= ' ' })
                blending.typeofagrasupport =
                    binding.typeofsupport.text.toString().trim { it <= ' ' }
                blending.crops = binding.crops.text.toString().trim { it <= ' ' }
                blending.fertilizercompanies = binding.companies.text.toString().trim { it <= ' ' }
                blending.quantityproduced =
                    Integer.parseInt(binding.quantityProduced.text.toString().trim { it <= ' ' })
                blending.quantitysold =
                    Integer.parseInt(binding.quantitySold.text.toString().trim { it <= ' ' })
                blending.collectorid = mParam1
                blending.collectorname = AndroidUtils.getCollectorname(mParam1)
                blending.agentname =
                    AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                blending.imei = AndroidUtils.getIMEI(context)
                blending.macaddress = AndroidUtils.getMacAddress(context)
                blending.uniqueuid = AndroidUtils.uuid
                blending.entrydate = Date()
                blending.save()

                FormPreviews.newInstance(4, blending.getUniqueuid(), this)
                    .show(fragmentManager!!, FormPreviews.TAG)

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
                            forms.uniqueuid = AndroidUtils.uuid
                            forms.save()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val transfer = ServerTransfer()
                        transfer.fertilizerBlending = blending
                        val jsonstring = Gson().toJson(transfer)
                        val uploadId = AndroidUtils.uuid
                        val uploadfilepath = AndroidUtils.writeToFile(jsonstring, "$uploadId.txt")

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
        if (componentname.equals("tosl", ignoreCase = true)) {
            binding.typeofsoillab.setText(selection)
        } else if (componentname.equals("tos", ignoreCase = true)) {
            binding.typeofsupport.setText(selection)
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
            blending.delete()
        dialog.dismiss()

    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(collectorid: String): Fertilizerdialog {
            val dialog = Fertilizerdialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, collectorid)
            dialog.arguments = args
            return dialog
        }
    }
}
