package com.uclgroupgh.form.form.dialogs.seedplanting


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
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.gson.Gson
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogSeedplantingBinding
import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.entities.SeedProduction
import com.uclgroupgh.form.form.dialogs.IndicatorDialog
import com.uclgroupgh.form.form.dialogs.previews.FormPreviews
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants
import com.uclgroupgh.form.utils.ListDialogFragment
import es.dmoral.toasty.Toasty
import java.math.BigDecimal
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SeedProductionDialog : DialogFragment(), View.OnClickListener, PreviewClickListener, ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var binding: DialogSeedplantingBinding
    internal lateinit var validator: Validator
    internal lateinit var seedProduction: SeedProduction
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var dialogFragment: ListDialogFragment
    private lateinit var fm: FragmentManager
    private lateinit var listitems: Array<String>
    internal lateinit var array: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_seedplanting, container, false)
        val view = binding.root
        validator = Validator(binding)
        binding.item.text = mParam2
        binding.ind8.setOnClickListener(this)
        binding.ind9.setOnClickListener(this)
        binding.ind10.setOnClickListener(this)
        binding.firsttimeinproduction.setOnClickListener(this)
        binding.currency.setOnClickListener(this)
        binding.variety.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.help.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        return view
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
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes.windowAnimations = R.style.traininganimate
        dialog.window!!.setLayout(width, height)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.ind8 -> binding.firsttimeinproduction.requestFocus()
            R.id.ind9 -> binding.quantityCertified.requestFocus()
            R.id.ind10 -> binding.quantitySold.requestFocus()
            R.id.currency -> {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N){
                    listitems = AndroidUtils.currencies.toTypedArray()
                    showOptionDialog(listitems, "Select Currency", "currency")
                }
                else
                    context?.let {
                        MaterialDialog(it)
                            .title(text = "Select Currency")
                            .cornerRadius(16f).show {
                                listItemsSingleChoice(items = AndroidUtils.currencies) { _, _, text ->
                                    binding.currency.setText(text)
                                }
                            }
                    }

            }
            R.id.variety -> {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N){
                    listitems = resources.getStringArray(R.array.seed_variety)
                    showOptionDialog(listitems, "Select Variety", "variety")
                }
                else
                context?.let {
                    MaterialDialog(it)
                        .title(text = "Select Variety")
                        .cornerRadius(16f).show {
                            listItemsSingleChoice(R.array.seed_variety) { _, _, text ->
                                binding.variety.setText(text)
                            }
                        }
                }
            }
            R.id.firsttimeinproduction -> {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N){
                    listitems = resources.getStringArray(R.array.type)
                    showOptionDialog(listitems, "Select Production", "production")
                }
                else
                context?.let {
                    MaterialDialog(it)
                        .title(text = "Select Production")
                        .cornerRadius(16f)
                        .show {
                            listItemsSingleChoice(R.array.type) { _, _, text ->
                                binding.firsttimeinproduction.setText(text)
                            }
                        }
                }
            }
            R.id.save -> saveForm()
            R.id.help -> {
                val dialog = IndicatorDialog.newInstance("1")
                dialog.show(fragmentManager!!, IndicatorDialog.TAG)
            }
            R.id.close -> closeDialog()
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
                seedProduction = SeedProduction()
                seedProduction.item = mParam2
                seedProduction.variety = binding.variety.text.toString().trim { it <= ' ' }
                seedProduction.firsttimeinproduction =
                    binding.firsttimeinproduction.text.toString().trim { it <= ' ' }
                seedProduction.plotsize = binding.plotsize.text.toString().trim { it <= ' ' }
                seedProduction.quantityproduced =
                    Integer.parseInt(binding.quantityProduced.text.toString().trim { it <= ' ' })
                seedProduction.quantitycertified =
                    Integer.parseInt(binding.quantityCertified.text.toString().trim { it <= ' ' })
                seedProduction.quantitysold =
                    Integer.parseInt(binding.quantitySold.text.toString().trim { it <= ' ' })
                seedProduction.unitprice =
                    BigDecimal.valueOf(java.lang.Double.parseDouble(binding.unitprice.text.toString().trim { it <= ' ' }))
                seedProduction.currency = binding.currency.text.toString().trim { it <= ' ' }
                seedProduction.collectorid = mParam1
                seedProduction.collectorname = AndroidUtils.getCollectorname(mParam1)
                seedProduction.agentname =
                    AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                seedProduction.imei = AndroidUtils.getIMEI(context)
                seedProduction.macaddress = AndroidUtils.getMacAddress(context)
                seedProduction.uniqueuid = AndroidUtils.uuid
                seedProduction.datecreated = Date()
                seedProduction.save()
                FormPreviews.newInstance(5, seedProduction.getUniqueuid(), this)
                    .show(fragmentManager!!, FormPreviews.TAG)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else
            Toasty.error(context!!, "An error occurred").show()
    }

    private fun uploadToServer() {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                internal var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {

                        val forms = FilledForms()
                        forms.category = mParam1
                        forms.subcategory = mParam2
                        forms.datecreated = Date()
                        forms.uniqueuid = AndroidUtils.uuid
                        forms.imei = AndroidUtils.getIMEI(context)
                        forms.macaddress = AndroidUtils.getMacAddress(context)
                        forms.uniqueuid = AndroidUtils.uuid
                        forms.save()

                        val transfer = ServerTransfer()
                        transfer.seedProduction = seedProduction
                        val jsonstring = Gson().toJson(transfer)
                        val uploadId = AndroidUtils.uuid
                        val uploadfilepath = AndroidUtils.writeToFile(jsonstring, "$uploadId.txt")
                        AndroidUtils.uploadFileToServer(context)

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

    private fun showOptionDialog(array: Array<String>, title: String, componentname: String) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, title, componentname)
        dialogFragment.show(fm, componentname)
    }

    override fun onListItemSelected(selection: String) {
        val componentname = dialogFragment.arguments!!.get("componentname") as String
        when {
            componentname.equals("currency", ignoreCase = true) -> {
                binding.currency.setText(selection)
            }
            componentname.equals("variety", ignoreCase = true) -> {
                binding.variety.setText(selection)
            }
            componentname.equals("production", ignoreCase = true) -> {
                binding.firsttimeinproduction.setText(selection)
            }
        }
    }

    override fun onPreviewClickListener(save: Boolean, dialog: Dialog) {
        if (save) {
            uploadToServer()
            Toasty.success(context!!, "Form saved").show()
            dismiss()
        } else
            seedProduction.delete()
        dialog.dismiss()
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        var TAG = "FullScreenDialog"

        fun newInstance(collectorid: String, seedtype: String): SeedProductionDialog {
            val dialog = SeedProductionDialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, collectorid)
            args.putString(ARG_PARAM2, seedtype)
            dialog.arguments = args
            return dialog
        }
    }
}
