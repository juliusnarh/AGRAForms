package com.uclgroupgh.form.form.dialogs.previews


import android.app.Dialog
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import com.orm.SugarRecord

import com.uclgroupgh.form.R
import com.uclgroupgh.form.adapters.TrainingAttendanceAdapter
import com.uclgroupgh.form.databinding.PreviewAggregationcentresBinding
import com.uclgroupgh.form.databinding.PreviewCollectorBinding
import com.uclgroupgh.form.databinding.PreviewExtensionEventsBinding
import com.uclgroupgh.form.databinding.PreviewFertilizerblendsBinding
import com.uclgroupgh.form.databinding.PreviewSeedplantingBinding
import com.uclgroupgh.form.databinding.PreviewSupportedEnterprisesBinding
import com.uclgroupgh.form.databinding.PreviewTrainingBinding
import com.uclgroupgh.form.entities.AggregationCenters
import com.uclgroupgh.form.entities.CollectorInfo
import com.uclgroupgh.form.entities.FertilizerBlending
import com.uclgroupgh.form.entities.ParticipationInAgraEvent
import com.uclgroupgh.form.entities.SeedProduction
import com.uclgroupgh.form.entities.SupportedEnterprise
import com.uclgroupgh.form.entities.TrainingAttendance
import com.uclgroupgh.form.entities.TrainingInfo
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.AggregationCentersPojo
import com.uclgroupgh.form.pojo.CollectorPojo
import com.uclgroupgh.form.pojo.ExtensionEventsPojo
import com.uclgroupgh.form.pojo.FertilizerBlendingPojo
import com.uclgroupgh.form.pojo.SeedProductionPojo
import com.uclgroupgh.form.pojo.SupportEnterprisesPojo
import com.uclgroupgh.form.pojo.TrainingInfoPojo
import com.uclgroupgh.form.utils.AndroidUtils

/**
 * A simple [Fragment] subclass.
 */
class FormPreviews : DialogFragment() {
    internal lateinit var aggregationBinding: PreviewAggregationcentresBinding
    internal lateinit var collectorBinding: PreviewCollectorBinding
    internal lateinit var eventsBinding: PreviewExtensionEventsBinding
    internal lateinit var fertilizerblendsBinding: PreviewFertilizerblendsBinding
    internal lateinit var seedplantingBinding: PreviewSeedplantingBinding
    internal lateinit var enterprisesBinding: PreviewSupportedEnterprisesBinding
    internal lateinit var trainingBinding: PreviewTrainingBinding
    private var mParam1: Int = 0
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        if (arguments != null) {
            mParam1 = arguments!!.getInt(ARG_PARAM1, 1)
            mParam2 = arguments!!.getString(ARG_PARAM2, "")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View

        if (mParam1 == 1) {
            collectorBinding =
                DataBindingUtil.inflate(inflater, R.layout.preview_collector, container, false)
            view = collectorBinding.root
            val list = SugarRecord.find(CollectorInfo::class.java, "uniqueuid = ?", mParam2)
            val info = list[0]
            val pojo = CollectorPojo()
            pojo.name.set(info.collectorname)
            pojo.country.set(info.country)
            pojo.region.set(info.region)
            pojo.district.set(info.district)
            pojo.year.set(info.year)
            pojo.quarter.set(info.quarter)
            pojo.project.set(info.project)
            pojo.grantee.set(info.grantee)
            collectorBinding.signature.setImageDrawable(
                AndroidUtils.arraytodrawable(
                    info.signature!!,
                    context!!
                )
            )
            collectorBinding.pojo = pojo
            collectorscreen()
            return view
        } else if (mParam1 == 2) {
            aggregationBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.preview_aggregationcentres,
                container,
                false
            )
            view = aggregationBinding.root
            val list =
                AggregationCenters.find(AggregationCenters::class.java, "uniqueuid = ?", mParam2)
            val centers = list[0]
            val pojo = AggregationCentersPojo()
            pojo.storageFacilityName.set(centers.getStorageFacilityName())
            pojo.location.set(centers.getLocation())
            pojo.newConstructionType.set(centers.getNewConstructionType())
            pojo.refurbishedType.set(centers.getRefurbishedType())
            pojo.warehouseType.set(centers.getWarehouseType())
            pojo.silosType.set(centers.getSilosType())
            pojo.picsbagsType.set(centers.getPicsbagsType())
            pojo.storesType.set(centers.getStorageFacilityName())
            pojo.volume.set(centers.getVolume())
            pojo.crop.set(centers.getCrop())
            pojo.quantityStored.set(centers.getQuantityStored())
            pojo.handlingCost.set(centers.getHandlingCost())
            pojo.currency.set(centers.getCurrency())
            pojo.servedFarmers.set(centers.getServedFarmers())
            pojo.quantitySold.set(centers.getQuantitySold())
            pojo.averagePrice.set(centers.getAveragePrice())
            aggregationBinding.pojo = pojo
            aggregationscreen()
            return view

        } else if (mParam1 == 3) {
            eventsBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.preview_extension_events,
                container,
                false
            )
            view = eventsBinding.root
            val list = ParticipationInAgraEvent.find(
                ParticipationInAgraEvent::class.java,
                "uniqueuid = ?",
                mParam2
            )
            val event = list[0]
            val pojo = ExtensionEventsPojo()
            pojo.typeofextension.set(event.getExtentionevent())
            pojo.nocompleted.set(event.getNocompleted().toString())
            pojo.farmergender.set(event.getFarmergender())
            pojo.chainactorgender.set(event.getChainactorgender())
            eventsBinding.pojo = pojo
            eventscreen()
            return view
        } else if (mParam1 == 4) {
            fertilizerblendsBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.preview_fertilizerblends,
                container,
                false
            )
            view = fertilizerblendsBinding.root
            val list =
                FertilizerBlending.find(FertilizerBlending::class.java, "uniqueuid = ?", mParam2)
            val blending = list[0]
            val pojo = FertilizerBlendingPojo()
            pojo.soillabname.set(blending.getSoillabname())
            pojo.soillabtest.set(blending.getSoillabtest())
            pojo.numberofblends.set(blending.getNumberofblends().toString())
            pojo.typeofagrasupport.set(blending.getTypeofagrasupport())
            pojo.crops.set(blending.getCrops())
            pojo.fertilizercompanies.set(blending.getFertilizercompanies())
            pojo.quantityproduced.set(blending.getQuantityproduced().toString())
            pojo.quantitysold.set(blending.getQuantitysold().toString())
            fertilizerblendsBinding.pojo = pojo
            fertilizerblendscreen()
            return view
        } else if (mParam1 == 5) {
            seedplantingBinding =
                DataBindingUtil.inflate(inflater, R.layout.preview_seedplanting, container, false)
            view = seedplantingBinding.root
            val list = SeedProduction.find(SeedProduction::class.java, "uniqueuid = ?", mParam2)
            val production = list[0]
            val pojo = SeedProductionPojo()
            pojo.item.set(production.getItem())
            pojo.variety.set(production.getVariety())
            pojo.firsttimeinproduction.set(production.getFirsttimeinproduction())
            pojo.plotsize.set(production.getPlotsize())
            pojo.quantityproduced.set(production.getQuantityproduced().toString())
            pojo.quantitycertified.set(production.getQuantitycertified().toString())
            pojo.quantitysold.set(production.getQuantitysold().toString())
            pojo.unitprice.set(production.getUnitprice().toString())
            pojo.currency.set(production.getCurrency())
            seedplantingBinding.pojo = pojo
            seedplantingscreen()
            return view
        } else if (mParam1 == 6) {
            enterprisesBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.preview_supported_enterprises,
                container,
                false
            )
            view = enterprisesBinding.root
            val list =
                SupportedEnterprise.find(SupportedEnterprise::class.java, "uniqueuid = ?", mParam2)
            val enterprise = list[0]
            val pojo = SupportEnterprisesPojo()
            pojo.enterpriseName.set(enterprise.getEnterpriseName())
            pojo.contacts.set(enterprise.getContacts())
            pojo.agroDealerType.set(enterprise.getAgroDealerType())
            pojo.seedProducer.set(enterprise.getSeedProducer())
            pojo.femaleOwnerStatus.set(enterprise.getFemaleOwnerStatus())
            pojo.youthOwnerStatus.set(enterprise.getYouthOwnerStatus())
            pojo.fullTimeEmploymentGender.set(enterprise.getCasualEmploymentGender())
            pojo.casualEmploymentGender.set(enterprise.getCasualEmploymentGender())
            pojo.partTimeEmploymentGender.set(enterprise.getPartTimeEmploymentGender())
            pojo.ageGroup.set(enterprise.getAgeGroup())
            enterprisesBinding.pojo = pojo
            enterprisesscreen()
            return view
        } else {
            trainingBinding =
                DataBindingUtil.inflate(inflater, R.layout.preview_training, container, false)
            view = trainingBinding.root
            val list = TrainingInfo.find(TrainingInfo::class.java, "traineeid = ?", mParam2)
            val info = list[0]
            val pojo = TrainingInfoPojo()
            pojo.theme.set(info.getTheme())
            pojo.disaggregationLevels.set(info.getDisaggregationLevels())
            pojo.period.set(info.getPeriod())
            pojo.venue.set(info.getVenue())
            pojo.trainerName.set(info.getTrainerName())
            pojo.trainerContact.set(info.getTrainerContact())
            pojo.event.set(info.getEvent())
            trainingBinding.pojo = pojo
            val list2 =
                SugarRecord.find(TrainingAttendance::class.java, "traineeid = ?", mParam2)
            trainingBinding.recycler.layoutManager = LinearLayoutManager(context)
            trainingBinding.recycler.itemAnimator = DefaultItemAnimator()
            val adapter = TrainingAttendanceAdapter(list2)
            trainingBinding.recycler.adapter = adapter
            runLayoutAnimation()
            trainingscreen()
            return view
        }
    }

    private fun runLayoutAnimation() {
        val context = context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        trainingBinding.recycler.layoutAnimation = controller
        trainingBinding.recycler.adapter!!.notifyDataSetChanged()
        trainingBinding.recycler.scheduleLayoutAnimation()
    }

    private fun eventscreen() {
        eventsBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        eventsBinding.save.setOnClickListener { v -> listener.onPreviewClickListener(true, dialog) }
    }

    private fun fertilizerblendscreen() {
        fertilizerblendsBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        fertilizerblendsBinding.save.setOnClickListener { v ->
            listener.onPreviewClickListener(
                true,
                dialog
            )
        }
    }

    private fun seedplantingscreen() {
        seedplantingBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        seedplantingBinding.save.setOnClickListener { v ->
            listener.onPreviewClickListener(
                true,
                dialog
            )
        }
    }

    private fun enterprisesscreen() {
        enterprisesBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        enterprisesBinding.save.setOnClickListener { v ->
            listener.onPreviewClickListener(
                true,
                dialog
            )
        }
    }

    private fun trainingscreen() {
        trainingBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        trainingBinding.save.setOnClickListener { v ->
            listener.onPreviewClickListener(
                true,
                dialog
            )
        }
    }

    private fun collectorscreen() {
        collectorBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        collectorBinding.save.setOnClickListener { v ->
            listener.onPreviewClickListener(
                true,
                dialog
            )
        }
    }

    private fun aggregationscreen() {
        aggregationBinding.back.setOnClickListener { v ->
            listener.onPreviewClickListener(
                false,
                dialog
            )
        }

        aggregationBinding.save.setOnClickListener { v ->
            listener.onPreviewClickListener(
                true,
                dialog
            )
        }
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
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes.windowAnimations = R.style.traininganimate
            dialog.window!!.setLayout(width, height)
        }
    }

    companion object {

        private val ARG_PARAM1 = "index"
        private val ARG_PARAM2 = "uuid"
        var TAG = "FormPreviewDialog"
        internal lateinit var listener: PreviewClickListener

        fun newInstance(
            index: Int,
            uuid: String,
            clickListener: PreviewClickListener
        ): FormPreviews {
            listener = clickListener
            val dialog = FormPreviews()
            val args = Bundle()
            args.putInt(ARG_PARAM1, index)
            args.putString(ARG_PARAM2, uuid)
            dialog.arguments = args
            return dialog
        }
    }
}
