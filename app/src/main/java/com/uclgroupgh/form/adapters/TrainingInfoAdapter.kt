package com.uclgroupgh.form.adapters

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orm.SugarRecord

import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.TrainingAttendance
import com.uclgroupgh.form.entities.TrainingInfo
import com.uclgroupgh.form.form.dialogs.training.TrainingParticipationDialog
import com.uclgroupgh.form.utils.AndroidUtils

import es.dmoral.toasty.Toasty

class TrainingInfoAdapter(private val trainingInfoList: List<TrainingInfo>, private val fragmentManager: FragmentManager?) : RecyclerView.Adapter<TrainingInfoAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_training_info, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val trainingInfo = trainingInfoList[position]
        holder.collectorid.text = trainingInfo.getCollectorid()
        holder.date.text =
            AndroidUtils.dateToFormattedString(trainingInfo.getEntrydate(), "dd MMM, yyyy")
        holder.traineeid.text = trainingInfo.getTraineeid()
        holder.collectorname.text = trainingInfo.getCollectorname()
        holder.trainername.text = trainingInfo.getTrainerName()
        holder.trainernercontact.text = trainingInfo.getTrainerContact()
        holder.theme.text = trainingInfo.getTheme()
        holder.event.text = trainingInfo.getEvent()
        holder.disaggregation.text = trainingInfo.getDisaggregationLevels()
        holder.venue.text = trainingInfo.getVenue()
        holder.period.text = trainingInfo.getPeriod()
        val list = SugarRecord.find(
            TrainingAttendance::class.java,
            "traineeid = ?",
            trainingInfo.getTraineeid()
        )

        holder.itemView.setOnClickListener { v ->
            if (list.size > 0) {
                if (fragmentManager != null) {
                    TrainingParticipationDialog.newInstance(list)
                        .show(fragmentManager, TrainingParticipationDialog.TAG)
                }
            } else
                Toasty.error(holder.itemView.context, "No Participant").show()
        }
    }

    override fun getItemCount(): Int {
        return trainingInfoList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView
        var collectorid: TextView
        var collectorname: TextView
        var traineeid: TextView
        var trainername: TextView
        var trainernercontact: TextView
        var theme: TextView
        var event: TextView
        var disaggregation: TextView
        var venue: TextView
        var period: TextView

        init {
            collectorid = view.findViewById(R.id.collectorid)
            date = view.findViewById(R.id.date)
            traineeid = view.findViewById(R.id.traineeid)
            collectorname = view.findViewById(R.id.name)
            trainername = view.findViewById(R.id.trainername)
            trainernercontact = view.findViewById(R.id.trainercontact)
            theme = view.findViewById(R.id.theme)
            event = view.findViewById(R.id.event)
            disaggregation = view.findViewById(R.id.disaggregation)
            venue = view.findViewById(R.id.venue)
            period = view.findViewById(R.id.period)
        }
    }
}
