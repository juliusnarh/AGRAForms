package com.uclgroupgh.form.form.dialogs.extensionevents


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
import com.uclgroupgh.form.databinding.DialogExtensionEventsBinding
import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.entities.ParticipationInAgraEvent
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
class ExtensionEvents : DialogFragment(), View.OnClickListener, PreviewClickListener,
    ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var binding: DialogExtensionEventsBinding
    internal lateinit var validator: Validator
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var farmergender: String
    internal lateinit var chainactorgender: String
    internal lateinit var event: ParticipationInAgraEvent
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_extension_events, container, false)
        val view = binding.root
        validator = Validator(binding)

        binding.male2.isClickable = false
        binding.female2.isClickable = false
        binding.male.isClickable = false
        binding.female.isClickable = false
        binding.typeofextension.setOnClickListener(this)
        binding.help.setOnClickListener(this)
        binding.ind17.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.genderLayout.setOnClickListener(this)
        binding.genderLayout2.setOnClickListener(this)
        binding.genderLayout3.setOnClickListener(this)
        binding.genderLayout4.setOnClickListener(this)
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
            R.id.ind17 -> binding.numbercompleted.requestFocus()
            R.id.close -> closeDialog()
            R.id.save -> saveform()
            R.id.gender_layout4 -> if (binding.female2.isChecked) {
                binding.female2.isChecked = false
                binding.male2.isChecked = true
                chainactorgender = "male"
            } else {
                binding.female2.isChecked = true
                binding.male2.isChecked = false
                chainactorgender = "female"
            }
            R.id.gender_layout3 -> if (binding.male2.isChecked) {
                binding.male2.isChecked = false
                binding.female2.isChecked = true
                chainactorgender = "female"
            } else {
                binding.male2.isChecked = true
                binding.female2.isChecked = false
                chainactorgender = "male"
            }
            R.id.gender_layout2 -> if (binding.female.isChecked) {
                binding.female.isChecked = false
                binding.male.isChecked = true
                farmergender = "male"
            } else {
                binding.female.isChecked = true
                binding.male.isChecked = false
                farmergender = "female"
            }
            R.id.gender_layout -> if (binding.male.isChecked) {
                binding.male.isChecked = false
                binding.female.isChecked = true
                farmergender = "female"
            } else {
                binding.male.isChecked = true
                binding.female.isChecked = false
                farmergender = "male"
            }
            R.id.help -> {
                val dialog = IndicatorDialog.newInstance("3")
                dialog.show(fragmentManager!!, IndicatorDialog.TAG)
            }
            R.id.typeofextension -> {
                val listitems = resources.getStringArray(R.array.extension_event)
                showOptionDialog(listitems, "Type of Extension Event", "toe")
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

    override fun onListItemSelected(selection: String) {
        val componentname = dialogFragment.arguments!!.get("componentname") as String
        if (componentname.equals("toe", ignoreCase = true)) {
            binding.typeofextension.setText(selection)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                closeDialog()
            }
        }
    }

    private fun showOptionDialog(array: Array<String>, title: String, componentname: String) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, title, componentname)
        dialogFragment.show(fm, componentname)
    }

    private fun saveform() {
        if (validator.validate()) {
            try {
                event = ParticipationInAgraEvent()
                event.extentionevent = binding.typeofextension.text.toString().trim { it <= ' ' }
                event.nocompleted =
                    Integer.parseInt(binding.numbercompleted.text.toString().trim { it <= ' ' })
                event.farmergender = farmergender
                event.chainactorgender = chainactorgender
                event.collectorid = mParam1
                event.collectorname = AndroidUtils.getCollectorname(mParam1)
                event.agentname =
                    AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                event.imei = AndroidUtils.getIMEI(context)
                event.macaddress = AndroidUtils.getMacAddress(context)
                event.uniqueuid = AndroidUtils.uuid
                event.entrydate = Date()
                event.save()
                FormPreviews.newInstance(3, event.getUniqueuid(), this)
                    .show(fragmentManager!!, FormPreviews.TAG)

            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        } else {
            Toasty.error(context!!, "An error occurred").show()
        }
    }

    fun uploadToServer(event: ParticipationInAgraEvent) {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                internal var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {
                        val transfer = ServerTransfer()
                        transfer.participationInAgraEvent = event
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

                override fun onPostExecute(outcome: Boolean?) {}


            }
        asyncTask.execute()
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
            Toasty.success(context!!, "Form saved").show()
            uploadToServer(event)
            dismiss()
        } else {
            event.delete()
        }
        dialog.dismiss()

    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(collectorid: String): ExtensionEvents {
            val dialog = ExtensionEvents()
            val args = Bundle()
            args.putString(ARG_PARAM1, collectorid)
            dialog.arguments = args
            return dialog
        }
    }
}
