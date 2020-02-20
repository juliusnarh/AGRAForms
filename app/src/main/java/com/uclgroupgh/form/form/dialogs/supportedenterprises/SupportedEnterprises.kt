package com.uclgroupgh.form.form.dialogs.supportedenterprises


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
import com.uclgroupgh.form.databinding.DialogSupportedEnterprisesBinding
import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.entities.SupportedEnterprise
import com.uclgroupgh.form.form.dialogs.IndicatorDialog
import com.uclgroupgh.form.form.dialogs.previews.FormPreviews
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.ListDialogFragment

import java.util.Date

import br.com.ilhasoft.support.validation.Validator
import com.uclgroupgh.form.utils.Constants
import es.dmoral.toasty.Toasty

/**
 * A simple [Fragment] subclass.
 */
class SupportedEnterprises : DialogFragment(), View.OnClickListener, PreviewClickListener,
    ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var binding: DialogSupportedEnterprisesBinding
    internal lateinit var validator: Validator
    internal lateinit var fulltimegender: String
    internal lateinit var parttimegender: String
    internal lateinit var casualgender: String
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var listitems: Array<String>
    internal lateinit var enterprise: SupportedEnterprise
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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_supported_enterprises,
            container,
            false
        )
        val view = binding.root
        validator = Validator(binding)
        binding.ind63.setOnClickListener(this)
        binding.ind67.setOnClickListener(this)
        binding.femaleownerstatus.setOnClickListener(this)
        binding.youthownerstatus.setOnClickListener(this)
        binding.agrodealertype.setOnClickListener(this)
        binding.seedproducer.setOnClickListener(this)
        binding.casualFemale.isClickable = false
        binding.casualMale.isClickable = false
        binding.fulltimeFemale.isClickable = false
        binding.fulltimeMale.isClickable = false
        binding.parttimeFemale.isClickable = false
        binding.parttimeMale.isClickable = false
        binding.close.setOnClickListener(this)
        binding.help.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.fulltimeLayout.setOnClickListener(this)
        binding.fulltimeLayout2.setOnClickListener(this)
        binding.parttimeLayout.setOnClickListener(this)
        binding.parttimeLayout2.setOnClickListener(this)
        binding.casualLayout.setOnClickListener(this)
        binding.casualLayout2.setOnClickListener(this)
        binding.agegroup.setOnClickListener(this)
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
            R.id.agegroup -> {
                listitems = resources.getStringArray(R.array.agegroup)
                showOptionDialog(listitems, "Select Age Group", "agegroup")
            }
            R.id.agrodealertype -> {
                listitems = resources.getStringArray(R.array.agrodealer)
                showOptionDialog(listitems, "Select AgroDealer Type", "agrodealer")
            }
            R.id.seedproducer -> {
                listitems = resources.getStringArray(R.array.seedproducer)
                showOptionDialog(listitems, "Select Seed Producer Type", "seedproducer")
            }
            R.id.femaleownerstatus -> {
                listitems = resources.getStringArray(R.array.femaleowner)
                showOptionDialog(listitems, "Select Female Ownership Status", "female")
            }
            R.id.youthownerstatus -> {
                listitems = resources.getStringArray(R.array.youthowner)
                showOptionDialog(listitems, "Select Youth Female Ownership Status", "youth")
            }
            R.id.ind63 -> binding.femaleownerstatus.requestFocus()
            R.id.ind67 -> binding.youthownerstatus.requestFocus()
            R.id.save -> saveForm()
            R.id.help -> {
                val dialog = IndicatorDialog.newInstance("4")
                dialog.show(fragmentManager!!, IndicatorDialog.TAG)
            }
            R.id.close -> closeDialog()
            R.id.fulltime_layout -> if (binding.fulltimeMale.isChecked) {
                binding.fulltimeMale.isChecked = false
                binding.fulltimeFemale.isChecked = true
                fulltimegender = "female"
            } else {
                binding.fulltimeFemale.isChecked = false
                binding.fulltimeMale.isChecked = true
                fulltimegender = "male"
            }
            R.id.fulltime_layout2 -> if (binding.fulltimeFemale.isChecked) {
                binding.fulltimeFemale.isChecked = false
                binding.fulltimeMale.isChecked = true
                fulltimegender = "male"
            } else {
                binding.fulltimeMale.isChecked = false
                binding.fulltimeFemale.isChecked = true
                fulltimegender = "female"
            }
            R.id.parttime_layout -> if (binding.parttimeMale.isChecked) {
                binding.parttimeMale.isChecked = false
                binding.parttimeFemale.isChecked = true
                parttimegender = "female"
            } else {
                binding.parttimeFemale.isChecked = false
                binding.parttimeMale.isChecked = true
                parttimegender = "male"
            }
            R.id.parttime_layout2 -> if (binding.parttimeFemale.isChecked) {
                binding.parttimeFemale.isChecked = false
                binding.parttimeMale.isChecked = true
                parttimegender = "male"
            } else {
                binding.parttimeMale.isChecked = false
                binding.parttimeFemale.isChecked = true
                parttimegender = "female"
            }
            R.id.casual_layout -> if (binding.casualMale.isChecked) {
                binding.casualMale.isChecked = false
                binding.casualFemale.isChecked = true
                casualgender = "female"
            } else {
                binding.casualFemale.isChecked = false
                binding.casualMale.isChecked = true
                casualgender = "male"
            }
            R.id.casual_layout2 -> if (binding.casualFemale.isChecked) {
                binding.casualFemale.isChecked = false
                binding.casualMale.isChecked = true
                casualgender = "male"
            } else {
                binding.casualMale.isChecked = false
                binding.casualFemale.isChecked = true
                casualgender = "female"
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

    private fun closeDialog() {
        context?.let {
            MaterialDialog(it).show {
                title(res = R.string.close_dialog_title)
                cancelable(false)
                cornerRadius(16f)
                message(res = R.string.close_dialog)
                negativeButton(res = R.string.dialog_negative)
                positiveButton(res = R.string.dialog_positive) { dismiss() }
            }
        }
    }

    private fun saveForm() {
        if (validator.validate()) {
            try {
                enterprise = SupportedEnterprise()
                enterprise.collectorid = mParam1
                enterprise.collectorName = AndroidUtils.getCollectorname(mParam1)
                enterprise.agentname =
                    AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                enterprise.imei = AndroidUtils.getIMEI(context)
                enterprise.macaddress = AndroidUtils.getMacAddress(context)
                enterprise.uniqueuid = AndroidUtils.uuid
                enterprise.entrydate = Date()
                enterprise.enterpriseName =
                    binding.enterprisename.text.toString().trim { it <= ' ' }
                enterprise.contacts = binding.contact.text.toString().trim { it <= ' ' }
                enterprise.agroDealerType =
                    binding.agrodealertype.text.toString().trim { it <= ' ' }
                enterprise.seedProducer = binding.seedproducer.text.toString().trim { it <= ' ' }
                enterprise.femaleOwnerStatus =
                    binding.femaleownerstatus.text.toString().trim { it <= ' ' }
                enterprise.youthOwnerStatus =
                    binding.youthownerstatus.text.toString().trim { it <= ' ' }
                enterprise.fullTimeEmploymentGender = fulltimegender
                enterprise.partTimeEmploymentGender = parttimegender
                enterprise.casualEmploymentGender = casualgender
                enterprise.ageGroup = binding.agegroup.text.toString().trim { it <= ' ' }
                enterprise.save()
                FormPreviews.newInstance(6, enterprise.getUniqueuid(), this)
                    .show(fragmentManager!!, FormPreviews.TAG)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            Toasty.error(context!!, "An error occurred").show()
        }
    }

    fun uploadToServer(enterprise: SupportedEnterprise) {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                internal var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {
                        val transfer = ServerTransfer()
                        transfer.supportedEnterprise = enterprise
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
        if (componentname.equals("agegroup", ignoreCase = true)) {
            binding.agegroup.setText(selection)
        } else if (componentname.equals("agrodealer", ignoreCase = true)) {
            binding.agrodealertype.setText(selection)
        } else if (componentname.equals("seedproducer", ignoreCase = true)) {
            binding.seedproducer.setText(selection)
        } else if (componentname.equals("female", ignoreCase = true)) {
            binding.femaleownerstatus.setText(selection)
        } else if (componentname.equals("youth", ignoreCase = true)) {
            binding.youthownerstatus.setText(selection)
        }
    }

    private fun showOptionDialog(array: Array<String>, title: String, componentname: String) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, title, componentname)
        dialogFragment.show(fm, componentname)
    }

    override fun onPreviewClickListener(save: Boolean, dialog: Dialog) {
        if (save) {
            val forms = FilledForms()
            forms.category = mParam1
            forms.datecreated = Date()
            forms.uniqueuid = AndroidUtils.uuid
            forms.imei = AndroidUtils.getIMEI(context)
            forms.macaddress = AndroidUtils.getMacAddress(context)
            forms.uniqueuid = AndroidUtils.uuid
            forms.save()
            uploadToServer(enterprise)
            Toasty.success(context!!, "Form saved").show()
            dismiss()
        } else
            enterprise.delete()
        dialog.dismiss()

    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(collectorid: String): SupportedEnterprises {
            val dialog = SupportedEnterprises()
            val args = Bundle()
            args.putString(ARG_PARAM1, collectorid)
            dialog.arguments = args
            return dialog
        }
    }
}
