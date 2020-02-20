package com.uclgroupgh.form.form.dialogs.training


import android.annotation.SuppressLint
import android.app.Dialog
import androidx.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogTrainingInfoBinding
import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.entities.TrainingInfo
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils

import java.util.Date

import br.com.ilhasoft.support.validation.Validator
import com.uclgroupgh.form.utils.Constants
import es.dmoral.toasty.Toasty

/**
 * A simple [Fragment] subclass.
 */
class TrainingInfoDialog : DialogFragment(), View.OnClickListener {
    internal lateinit var binding: DialogTrainingInfoBinding
    internal lateinit var validator: Validator
    internal var policy: Boolean = false
    internal var harvest: Boolean = false
    internal var business: Boolean = false
    internal var production: Boolean = false
    private var mParam1: String? = null
    private var event: String? = null
    private val nameBuilder = StringBuilder()
    private val contactBuilder = StringBuilder()
    private val dissaggregationLevels = StringBuilder()

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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_training_info, container, false)
        val view = binding.root
        validator = Validator(binding)

        binding.fieldday.isClickable = false
        binding.training.isClickable = false
        binding.trainingLayout.setOnClickListener(this)
        binding.fielddayLayout.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.add.setOnClickListener(this)
        binding.close.setOnClickListener(this)

        binding.policychip.setOnSelectClickListener { _, selected -> policy = selected }

        binding.productionchip.setOnSelectClickListener { _, selected -> production = selected }
        binding.businesschip.setOnSelectClickListener { _, selected -> business = selected }
        binding.harvestchip.setOnSelectClickListener { _, selected -> harvest = selected }

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

            R.id.close -> closeDialog()
            R.id.add -> {
                if (binding.name.text.toString().isNotEmpty()) {
                    if (nameBuilder.toString().isEmpty()) {
                        nameBuilder.append(binding.name.text.toString()).append("\n")
                    } else
                        nameBuilder.append(binding.name.text.toString()).append("\n")
                    binding.name.setText("")
                } else
                    binding.name.error = "Field cannot be empty"

                if (binding.contact.text.toString().isNotEmpty()) {
                    if (contactBuilder.toString().isEmpty()) {
                        contactBuilder.append(binding.contact.text.toString()).append("\n")
                    } else
                        contactBuilder.append(binding.contact.text.toString()).append("\n")
                    binding.contact.setText("")
                } else {
                    if (contactBuilder.toString().isEmpty()) {
                        contactBuilder.append("n/a").append("\n")
                    } else
                        contactBuilder.append("n/a").append("\n")
                    binding.contact.setText("")
                }
                binding.trainername.text = nameBuilder.toString()
                binding.trainercontact.text = contactBuilder.toString()
            }
            R.id.save -> saveform()
            R.id.fieldday_layout -> if (binding.fieldday.isChecked) {
                binding.fieldday.isChecked = false
                binding.training.isChecked = true
                event = "training"
            } else {
                binding.fieldday.isChecked = true
                binding.training.isChecked = false
                event = "field day"
            }
            R.id.training_layout -> if (binding.training.isChecked) {
                binding.fieldday.isChecked = true
                binding.training.isChecked = false
                event = "field day"
            } else {
                binding.fieldday.isChecked = false
                binding.training.isChecked = true
                event = "training"
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
                title(text = "Cancel?")
                cancelable(false)
                cornerRadius(16f)
                message(text = "Do you want to close form?")
                negativeButton(text = "No")
                positiveButton(text = "Yes") { dialog!!.dismiss() }
            }
        }
    }

    //   TRAINEE ID
    private fun saveform() {
        if (validator.validate()) {
            try {
                if (policy)
                    dissaggregationLevels.append(resources.getString(R.string.policy)).append(", ")

                if (production)
                    dissaggregationLevels.append(resources.getString(R.string.production)).append(", ")

                if (business)
                    dissaggregationLevels.append(resources.getString(R.string.business)).append(", ")

                if (harvest)
                    dissaggregationLevels.append(resources.getString(R.string.postharvest))

                var temp = dissaggregationLevels.toString().trim { it <= ' ' }
                if (temp.endsWith(",")) {
                    temp = temp.substring(0, temp.length - 1)
                }
                val info = TrainingInfo()
                info.event = event
                info.traineeid = gettraineeid()
                info.theme = binding.theme.text.toString().trim { it <= ' ' }
                info.disaggregationLevels = temp
                info.period = binding.period.text.toString().trim { it <= ' ' }
                info.venue = binding.venue.text.toString().trim { it <= ' ' }

                if (nameBuilder.toString().isEmpty()) {
                    info.trainerName = binding.name.text.toString()
                } else
                    info.trainerName = nameBuilder.toString()

                if (contactBuilder.toString().isEmpty()) {
                    info.trainerContact = binding.contact.text.toString()
                } else
                    info.trainerContact = contactBuilder.toString()
                info.collectorid = mParam1
                info.collectorname = AndroidUtils.getCollectorname(mParam1)
                info.agentname =
                    AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                info.imei = AndroidUtils.getIMEI(context)
                info.macaddress = AndroidUtils.getMacAddress(context)
                info.uniqueuid = AndroidUtils.uuid
                info.entrydate = Date()
                info.save()
                val forms = FilledForms()
                forms.category = mParam1
                forms.datecreated = Date()
                forms.uniqueuid = AndroidUtils.uuid
                forms.imei = AndroidUtils.getIMEI(context)
                forms.macaddress = AndroidUtils.getMacAddress(context)
                forms.uniqueuid = AndroidUtils.uuid
                forms.save()
                TrainingAttendanceDialog.newInstance(info.getTraineeid(), mParam1!!, dialog!!)
                    .show(fragmentManager!!, TrainingAttendanceDialog.TAG)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else
            Toasty.error(context!!, "An error occurred").show()

    }

    private fun gettraineeid(): String {
        val timestamp = System.currentTimeMillis().toString()
        return "TRA-" + timestamp.substring(0, 3) + "-" + timestamp.substring(3, 7)+
                "-" + timestamp.substring(7, 11) + "-" + timestamp.substring(11)
    }

    fun uploadToServer(info: TrainingInfo) {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                internal var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {
                        val transfer = ServerTransfer()
                        transfer.trainingInfo = info
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

                override fun onPostExecute(outcome: Boolean) {
                    //some logic logic logic
                    if (outcome) {
                        TrainingAttendanceDialog.newInstance(
                            info.getTraineeid(),
                            mParam1!!,
                            dialog!!
                        ).show(fragmentManager!!, TrainingAttendanceDialog.TAG)
                        dismiss()
                    }
                }
            }
        asyncTask.execute()
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(collectorid: String): TrainingInfoDialog {
            val dialog = TrainingInfoDialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, collectorid)
            dialog.arguments = args
            return dialog
        }
    }

}
