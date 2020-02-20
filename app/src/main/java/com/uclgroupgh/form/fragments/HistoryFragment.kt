package com.uclgroupgh.form.fragments


import android.annotation.SuppressLint
import androidx.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.orm.SugarRecord

import com.uclgroupgh.form.R
import com.uclgroupgh.form.adapters.AggregationCentersAdapter
import com.uclgroupgh.form.adapters.FertilizerBlendingAdapter
import com.uclgroupgh.form.adapters.ParticipationinAgraAdapter
import com.uclgroupgh.form.adapters.SeedProductionAdapter
import com.uclgroupgh.form.adapters.SupportedEnterprisesAdapter
import com.uclgroupgh.form.adapters.TrainingInfoAdapter
import com.uclgroupgh.form.databinding.FragmentHistoryBinding
import com.uclgroupgh.form.entities.AggregationCenters
import com.uclgroupgh.form.entities.FertilizerBlending
import com.uclgroupgh.form.entities.ParticipationInAgraEvent
import com.uclgroupgh.form.entities.SeedProduction
import com.uclgroupgh.form.entities.SupportedEnterprise
import com.uclgroupgh.form.entities.TrainingAttendance
import com.uclgroupgh.form.entities.TrainingInfo
import com.uclgroupgh.form.utils.ExportDatabaseCSVTask
import com.uclgroupgh.form.utils.ListDialogFragment

import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment(), ListDialogFragment.OnListDialogItemSelect {
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var listitems: Array<String>
    lateinit var binding: FragmentHistoryBinding
    internal lateinit var productionAdapter: SeedProductionAdapter
    internal lateinit var blendingAdapter: FertilizerBlendingAdapter
    internal lateinit var aggregationCentersAdapter: AggregationCentersAdapter
    internal lateinit var agraAdapter: ParticipationinAgraAdapter
    internal lateinit var trainingInfoAdapter: TrainingInfoAdapter
    internal lateinit var enterprisesAdapter: SupportedEnterprisesAdapter
    internal var trainingAttendanceList: MutableList<TrainingAttendance> = mutableListOf()
    internal var seedProductions: MutableList<SeedProduction> = mutableListOf()
    internal var blendings: MutableList<FertilizerBlending> = mutableListOf()
    internal var aggregationCenters: MutableList<AggregationCenters> = mutableListOf()
    internal var participation: MutableList<ParticipationInAgraEvent> = mutableListOf()
    internal var infos: MutableList<TrainingInfo> = mutableListOf()
    internal var enterprises: MutableList<SupportedEnterprise> = mutableListOf()
    internal var selection = arrayOf(
        "Seed Production",
        "Fertilizer Blends",
        "Extension Events",
        "Enterprises Supported By AGRA",
        "Aggregation Centres",
        "Training"
    )
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        val view = binding.root
        trainingAttendanceList = ArrayList()

        binding.formhistory.setOnClickListener {
            listitems = resources.getStringArray(R.array.forms)
            showOptionDialog(listitems, "Select Form Data to View", "form")
        }

        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.itemAnimator = DefaultItemAnimator()

        seedProductions = SeedProduction.listAll(SeedProduction::class.java)
        productionAdapter = SeedProductionAdapter(seedProductions)

        loadseeddata()
        getalldata(this)
        binding.export.setOnClickListener {
            val selected = binding.formhistory.text.toString()
            val seedProduction = SeedProduction()
            val participationInAgraEvent = ParticipationInAgraEvent()
            val aggregationCenters = AggregationCenters()
            val supportedEnterprise = SupportedEnterprise()
            val trainingAttendance = TrainingAttendance()
            val fertilizerBlending = FertilizerBlending()
            val csvTask: ExportDatabaseCSVTask
            when {
                selected.equals(this.selection[0], ignoreCase = true) -> {
                    csvTask = ExportDatabaseCSVTask(activity, seedProduction)
                    csvTask.execute()

                }
                selected.equals(this.selection[1], ignoreCase = true) -> {
                    csvTask = ExportDatabaseCSVTask(activity, fertilizerBlending)
                    csvTask.execute()
                }
                selected.equals(this.selection[2], ignoreCase = true) -> {
                    csvTask = ExportDatabaseCSVTask(activity, participationInAgraEvent)
                    csvTask.execute()
                }
                selected.equals(this.selection[3], ignoreCase = true) -> {
                    csvTask = ExportDatabaseCSVTask(activity, supportedEnterprise)
                    csvTask.execute()
                }
                selected.equals(this.selection[4], ignoreCase = true) -> {
                    csvTask = ExportDatabaseCSVTask(activity, aggregationCenters)
                    csvTask.execute()
                }
                selected.equals(this.selection[5], ignoreCase = true) -> {
                    csvTask = ExportDatabaseCSVTask(activity, trainingAttendance)
                    csvTask.execute()
                }
            }
        }

        return view
    }

    override fun onListItemSelected(selection: String) {
        when {
            selection.equals(this.selection[0], ignoreCase = true) -> loadseeddata()
            selection.equals(this.selection[1], ignoreCase = true) -> loadfertilizerdata()
            selection.equals(this.selection[2], ignoreCase = true) -> loadeventsdata()
            selection.equals(this.selection[3], ignoreCase = true) -> loadsupportedenterprisesdata()
            selection.equals(this.selection[4], ignoreCase = true) -> loadaggregationdata()
            selection.equals(this.selection[5], ignoreCase = true) -> loadtrainingdata()
        }
        binding.formhistory.setText(selection)
    }

    private fun showOptionDialog(array: Array<String>, title: String, componentname: String) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, title, componentname)
        dialogFragment.show(fm, componentname)
    }

    private fun loadseeddata() {
        if (seedProductions.size > 0) {
            binding.norecord.visibility = View.GONE
            binding.recycler.adapter = productionAdapter
            runLayoutAnimation()
        } else {
            if (binding.norecord.visibility == View.GONE) {
                binding.recycler.adapter = productionAdapter
                binding.norecord.visibility = View.VISIBLE
            }
        }
    }

    private fun loadfertilizerdata() {
        if (blendings.size > 0) {
            binding.norecord.visibility = View.GONE
            binding.recycler.adapter = blendingAdapter
            runLayoutAnimation()
        } else {
            if (binding.norecord.visibility == View.GONE) {
                binding.recycler.adapter = blendingAdapter
                binding.norecord.visibility = View.VISIBLE
            }
        }
    }

    private fun loadeventsdata() {
        if (participation.size > 0) {
            binding.norecord.visibility = View.GONE
            binding.recycler.adapter = agraAdapter
            runLayoutAnimation()
        } else {
            if (binding.norecord.visibility == View.GONE) {
                binding.recycler.adapter = agraAdapter
                binding.norecord.visibility = View.VISIBLE
            }
        }
    }

    private fun loadsupportedenterprisesdata() {
        if (enterprises.size > 0) {
            binding.norecord.visibility = View.GONE
            binding.recycler.adapter = enterprisesAdapter
            runLayoutAnimation()
        } else {
            if (binding.norecord.visibility == View.GONE) {
                binding.recycler.adapter = enterprisesAdapter
                binding.norecord.visibility = View.VISIBLE
            }
        }
    }

    private fun loadaggregationdata() {
        if (aggregationCenters.size > 0) {
            binding.norecord.visibility = View.GONE
            binding.recycler.adapter = aggregationCentersAdapter
            runLayoutAnimation()
        } else {
            if (binding.norecord.visibility == View.GONE) {
                binding.recycler.adapter = aggregationCentersAdapter
                binding.norecord.visibility = View.VISIBLE
            }
        }
    }

    private fun loadtrainingdata() {
        if (infos.isNotEmpty()) {
            binding.norecord.visibility = View.GONE
            binding.recycler.adapter = trainingInfoAdapter
            runLayoutAnimation()
        } else {
            if (binding.norecord.visibility == View.GONE) {
                binding.recycler.adapter = trainingInfoAdapter
                binding.norecord.visibility = View.VISIBLE
            }
        }
    }


    fun reloaddata(fragment: HistoryFragment) {
        getalldata(fragment)
    }

    fun getalldata(fragment: HistoryFragment) {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {

                        seedProductions = SugarRecord.listAll(SeedProduction::class.java).toMutableList()
                        blendings = SugarRecord.listAll(FertilizerBlending::class.java).toMutableList()
                        aggregationCenters = SugarRecord.listAll(AggregationCenters::class.java).toMutableList()
                        participation = SugarRecord.listAll(ParticipationInAgraEvent::class.java).toMutableList()
                        infos = SugarRecord.listAll(TrainingInfo::class.java).toMutableList()
                        enterprises = SugarRecord.listAll(SupportedEnterprise::class.java).toMutableList()

                        productionAdapter = SeedProductionAdapter(seedProductions)
                        blendingAdapter = FertilizerBlendingAdapter(blendings)
                        aggregationCentersAdapter = AggregationCentersAdapter(aggregationCenters)
                        agraAdapter = ParticipationinAgraAdapter(participation)
                        trainingInfoAdapter = TrainingInfoAdapter(infos, fragmentManager)
                        enterprisesAdapter = SupportedEnterprisesAdapter(enterprises)

                        outcome = true
                    } catch (e: Exception) {
                        println("Exception" + e.message)
                        e.printStackTrace()
                    }

                    return outcome
                }

                override fun onPostExecute(outcome: Boolean?) {
                    if (outcome!!){
//                        binding.recycler.adapter = productionAdapter
//                                runLayoutAnimation()
                    }
                }


            }
        asyncTask.execute()
    }
    fun runLayoutAnimation() {
        val context = context
        if (context != null) {
            val controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

            binding.recycler.layoutAnimation = controller
            binding.recycler.adapter!!.notifyDataSetChanged()
            binding.recycler.scheduleLayoutAnimation()
        } else
            binding.recycler.adapter!!.notifyDataSetChanged()
    }

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
