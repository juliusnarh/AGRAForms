package com.uclgroupgh.form.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.TrainingAttendance
import com.uclgroupgh.form.utils.AndroidUtils

class TrainingAttendanceAdapter(private val trainingAttendanceList: List<TrainingAttendance>) :
    RecyclerView.Adapter<TrainingAttendanceAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training_attendance, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendance = trainingAttendanceList[position]
        holder.collectorid.text = attendance.collectorid
        holder.date.text =
            AndroidUtils.dateToFormattedString(attendance.entrydate!!, "dd MMM, yyyy")
        holder.traineeid.text = attendance.traineeid
        holder.gender.text = attendance.gender
        val temp =
            attendance.title + " " + attendance.lastname + " " + attendance.firstname
        holder.participantname.text = temp
        holder.participantType.text = attendance.participantType
        holder.function.text = attendance.function
        holder.institution.text = attendance.institution
        holder.region.text = attendance.region
        holder.districts.text = attendance.districts
        holder.telephone.text = attendance.telephone
        holder.email.text = attendance.email
    }

    override fun getItemCount(): Int {
        return trainingAttendanceList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var collectorid: TextView = view.findViewById(R.id.collectorid)
        var date: TextView = view.findViewById(R.id.date)
        var traineeid: TextView = view.findViewById(R.id.traineeid)
        var participantname: TextView = view.findViewById(R.id.participantname)
        var gender: TextView = view.findViewById(R.id.gender)
        var participantType: TextView = view.findViewById(R.id.participanttype)
        var function: TextView = view.findViewById(R.id.function)
        var institution: TextView = view.findViewById(R.id.institution)
        var region: TextView = view.findViewById(R.id.region)
        var districts: TextView = view.findViewById(R.id.district)
        var telephone: TextView = view.findViewById(R.id.contact)
        var email: TextView = view.findViewById(R.id.email)

    }
}
