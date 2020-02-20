package com.uclgroupgh.form.form.dialogs


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.ilhasoft.support.validation.Validator
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.orm.SugarRecord
import com.uclgroupgh.form.R
import com.uclgroupgh.form.adapters.CollectorAdapter
import com.uclgroupgh.form.databinding.DialogAllCollectorsBinding
import com.uclgroupgh.form.entities.CollectorInfo
import com.uclgroupgh.form.form.dialogs.aggregationcenters.AggregationCentresDialog
import com.uclgroupgh.form.form.dialogs.extensionevents.ExtensionEvents
import com.uclgroupgh.form.form.dialogs.fertilizerblends.Fertilizerdialog
import com.uclgroupgh.form.form.dialogs.seedplanting.SeedProductionDialog
import com.uclgroupgh.form.form.dialogs.supportedenterprises.SupportedEnterprises
import com.uclgroupgh.form.form.dialogs.training.TrainingInfoDialog
import com.uclgroupgh.form.interfaces.CollecterinfoClickListener
import com.uclgroupgh.form.utils.ListDialogFragment

/**
 * A simple [Fragment] subclass.
 */
class AllCollectorsDialog : DialogFragment(), View.OnClickListener, CollecterinfoClickListener,
    ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var binding: DialogAllCollectorsBinding
    internal lateinit var validator: Validator
    private var mParam1: String? = null
    private var collectorid: String = ""

    private lateinit var dialogFragment: ListDialogFragment
    private lateinit var fm: FragmentManager
    private lateinit var listitems: Array<String>
    internal lateinit var array: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_all_collectors, container, false)
        validator = Validator(binding)

        binding.add.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        val info = SugarRecord.listAll(CollectorInfo::class.java)

        if (info.size > 0) {
            binding.recycler.layoutManager = LinearLayoutManager(context)
            binding.recycler.itemAnimator = DefaultItemAnimator()
            val adapter = context?.let { CollectorAdapter(it, info, this) }
            binding.recycler.adapter = adapter
        } else {
            binding.norecord.visibility = View.VISIBLE
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                dismiss()
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
            R.id.close -> dismiss()

            R.id.add -> {
                CollectorDialog.newInstance(mParam1!!).show(fragmentManager!!, CollectorDialog.TAG)
                dismiss()
            }
        }
    }

    override fun onItemListener(collectoridx: String) {
        this.collectorid = collectoridx
        when {
            mParam1!!.equals("seed production", ignoreCase = true) -> {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N){
                    listitems = resources.getStringArray(R.array.title)
                    showOptionDialog(listitems)
                }
                else
                    context?.let {
                        MaterialDialog(it).cornerRadius(16f).show {
                            listItemsSingleChoice(R.array.seed_production_type) { _, _, text ->
                                SeedProductionDialog.newInstance(collectorid, text.toString())
                                    .show(fragmentManager!!, Fertilizerdialog.TAG)
                            }
                        }
                    }
                dismiss()
            }
            mParam1!!.equals("fertilizer blends", ignoreCase = true) -> {
                Fertilizerdialog.newInstance(collectorid)
                    .show(fragmentManager!!, Fertilizerdialog.TAG)
                dismiss()
            }
            mParam1!!.equals("Extension Events", ignoreCase = true) -> {
                ExtensionEvents.newInstance(collectorid)
                    .show(fragmentManager!!, ExtensionEvents.TAG)
                dismiss()

            }
            mParam1!!.equals("Supported Enterprises", ignoreCase = true) -> {
                SupportedEnterprises.newInstance(collectorid)
                    .show(fragmentManager!!, SupportedEnterprises.TAG)
                dismiss()

            }
            mParam1!!.equals("Training", ignoreCase = true) -> {
                TrainingInfoDialog.newInstance(collectorid)
                    .show(fragmentManager!!, TrainingInfoDialog.TAG)
                dismiss()

            }
            mParam1!!.equals("Aggregation Centres", ignoreCase = true) -> {
                AggregationCentresDialog.newInstance(collectorid)
                    .show(fragmentManager!!, AggregationCentresDialog.TAG)
                dismiss()
            }
        }
    }

    private fun showOptionDialog(array: Array<String>) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, "Select Seed Type", "seed_type")
        dialogFragment.show(fm, "seed_type")
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(eventtype: String): AllCollectorsDialog {
            val dialog = AllCollectorsDialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, eventtype)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onListItemSelected(selection: String?) {
        SeedProductionDialog.newInstance(collectorid, selection!!)
            .show(fragmentManager!!, Fertilizerdialog.TAG)
    }

}
