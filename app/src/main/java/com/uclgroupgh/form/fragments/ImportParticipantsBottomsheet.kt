package com.uclgroupgh.form.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orm.SugarRecord
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.BottomsheetImportParticipantsBinding
import com.uclgroupgh.form.entities.Farmers
import com.uclgroupgh.form.entities.TrainingAttendance
import com.uclgroupgh.form.form.dialogs.training.TrainingAttendanceDialog
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants
import com.uclgroupgh.form.utils.FarmerBinder2
import mva2.adapter.ListSection
import mva2.adapter.MultiViewAdapter
import mva2.adapter.util.Mode
import mva2.adapter.util.OnSelectionChangedListener
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ImportParticipantsBottomsheet.newInstance] factory method to
 * create an instance of this fragment.
 */
// TODO: Rename and change types of parameters

class ImportParticipantsBottomsheet : BottomSheetDialogFragment() {
    internal lateinit var binding: BottomsheetImportParticipantsBinding
    private lateinit var farmersList: MutableList<Farmers>
    private lateinit var selectedFarmersList: MutableList<Farmers>
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var onImportParticipantsListener: OnImportParticipantsListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun getTheme(): Int {
        return R.style.BaseBottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_import_participants, container, false)
        val view = binding.root
        // Create Adapter
        farmersList = SugarRecord.listAll(Farmers::class.java).toMutableList()

        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.itemAnimator = DefaultItemAnimator()

        val adapter = MultiViewAdapter()
        binding.recycler.adapter = adapter

        adapter.registerItemBinders(FarmerBinder2())
        val listSection = ListSection<Farmers>()
        listSection.addAll(farmersList)
        adapter.addSection(listSection)
        adapter.setSelectionMode(Mode.MULTIPLE)

        val listener: OnSelectionChangedListener<Farmers> = OnSelectionChangedListener{ _: Farmers, _: Boolean, selectedItems: MutableList<Farmers>? ->
                selectedFarmersList = selectedItems!!


                if (binding.importActions.visibility == View.GONE)
                    binding.importActions.visibility = View.VISIBLE

            if (selectedItems.size > 0)
                binding.importActions.visibility = View.VISIBLE
            else
                binding.importActions.visibility = View.GONE

            Log.e("FARMER SELECTION: ", selectedItems.size.toString())
        }

        listSection.setOnSelectionChangedListener(listener)

        binding.resetSelection.setOnClickListener{
            listSection.clearSelections()
        }

        binding.importParticipants.setOnClickListener{
            SavingTrainingParticipants().execute(mParam1, "","")
        }

        return view
    }

    inner class SavingTrainingParticipants : AsyncTask<String?, String?, String?>() {
        lateinit var progressDialog: MaterialDialog
        lateinit var trainingAttendanceList: MutableList<TrainingAttendance>
        override fun onPreExecute() {
            super.onPreExecute()

            progressDialog = MaterialDialog(TrainingAttendanceDialog.infodialog!!.context)
                .cornerRadius(16f)
                .cancelable(false)
                .customView(R.layout.item_progressbar, scrollable = true)

            val customView = progressDialog.getCustomView()
            val tv: TextView = customView.findViewById(R.id.progress_text)
            tv.text = TrainingAttendanceDialog.infodialog!!.context.resources.getString(R.string.please_wait_import)
            progressDialog.show()
        }

        override fun doInBackground(vararg f_url: String?): String? {
            try {
                trainingAttendanceList = mutableListOf()
                for (farmer in selectedFarmersList) {
                    val attendance = TrainingAttendance()
                    attendance.traineeid = mParam1
                    attendance.title = if (farmer.gender!!.contains('f')) "Mrs" else "Mr"
                    attendance.firstname = farmer.othername
                    attendance.lastname = farmer.surname
                    attendance.gender = farmer.gender
                    attendance.participantType = "Farmer/FBO"
                    attendance.function = null
                    attendance.institution = null
                    attendance.region = farmer.region
                    attendance.districts = farmer.district
                    attendance.telephone = farmer.tel
                    attendance.email = farmer.email
                    attendance.collectorid = mParam2
                    attendance.collectorname = AndroidUtils.getCollectorname(mParam2)
                    attendance.agentname =
                        AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                    attendance.imei = AndroidUtils.getIMEI(context)
                    attendance.macaddress = AndroidUtils.getMacAddress(context)
                    attendance.uniqueuid = AndroidUtils.uuid
                    attendance.entrydate = Date()
                    attendance.signature =
                        farmer.farmerid?.let { AndroidUtils.loadFarmerSignature(it, context!!.resources) }

                    attendance.save()
                    trainingAttendanceList.add(attendance)
                }

            } catch (e: Exception) {
                Log.e("Error: ", e.message)
            }

            return null
        }

        override fun onPostExecute(file_url: String?) {
            progressDialog.dismiss()
            dialog!!.dismiss()
            onImportParticipantsListener!!.onFinishImport(trainingAttendanceList)

        }
    }

    fun setOnImportParticipantsListener(onImportParticipantsListener: OnImportParticipantsListener) {
        this.onImportParticipantsListener = onImportParticipantsListener
    }

    interface OnImportParticipantsListener {
        fun onFinishImport(attendanceList: MutableList<TrainingAttendance>)
    }

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ImportParticipantsBottomsheet {
            val fragment = ImportParticipantsBottomsheet()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
