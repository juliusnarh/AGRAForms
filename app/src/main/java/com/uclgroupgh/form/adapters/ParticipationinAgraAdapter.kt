package com.uclgroupgh.form.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.ParticipationInAgraEvent
import com.uclgroupgh.form.utils.AndroidUtils

class ParticipationinAgraAdapter(private val agraList: List<ParticipationInAgraEvent>) :
    RecyclerView.Adapter<ParticipationinAgraAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_extensionevents, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val agraEvent = agraList[position]
        holder.collectorid.text = agraEvent.getCollectorid()
        holder.date.text =
            AndroidUtils.dateToFormattedString(agraEvent.getEntrydate(), "dd MMM, yyyy")
        holder.extentionevent.text = agraEvent.getExtentionevent()
        holder.nocompleted.text = agraEvent.getNocompleted().toString()
        holder.farmergender.text = agraEvent.getFarmergender()
        holder.chainactorgender.text = agraEvent.getChainactorgender()
        holder.collectorname.text = agraEvent.getCollectorname()
    }

    override fun getItemCount(): Int {
        return agraList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView = view.findViewById(R.id.date)
        var collectorid: TextView = view.findViewById(R.id.collectorid)
        var extentionevent: TextView = view.findViewById(R.id.extensionevent)
        var nocompleted: TextView = view.findViewById(R.id.nocompleted)
        var farmergender: TextView = view.findViewById(R.id.farmergender)
        var chainactorgender: TextView = view.findViewById(R.id.chainactorgender)
        var collectorname: TextView = view.findViewById(R.id.name)
    }
}
