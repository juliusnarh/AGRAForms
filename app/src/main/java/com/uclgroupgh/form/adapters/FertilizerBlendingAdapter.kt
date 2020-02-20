package com.uclgroupgh.form.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.FertilizerBlending
import com.uclgroupgh.form.utils.AndroidUtils

class FertilizerBlendingAdapter(private val fertilizerBlendingList: List<FertilizerBlending>) :
    RecyclerView.Adapter<FertilizerBlendingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fertilizerblending, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val res = holder.itemView.resources
        val blending = fertilizerBlendingList[position]
        holder.collectorid.text = blending.getCollectorid()
        holder.date.text =
            AndroidUtils.dateToFormattedString(blending.getEntrydate(), "dd MMM, yyyy")
        holder.collectorname.text = blending.getCollectorname()
        holder.soillabtest.text = blending.getSoillabtest()
        holder.quantityproduced.text =
            res.getString(R.string.mt, blending.getQuantityproduced().toString())
        holder.quantitysold.text = res.getString(R.string.mt, blending.getQuantitysold().toString())
        holder.soillabname.text = blending.getSoillabname()
        holder.numberofblends.text = blending.getNumberofblends().toString()
        holder.typeofsupport.text = blending.getTypeofagrasupport()
        holder.crops.text = blending.getCrops()
        holder.fertilizercompanies.text = blending.getFertilizercompanies()
    }

    override fun getItemCount(): Int {
        return fertilizerBlendingList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView
        var collectorid: TextView
        var collectorname: TextView
        var quantitysold: TextView
        var quantityproduced: TextView
        var soillabname: TextView
        var soillabtest: TextView
        var numberofblends: TextView
        var typeofsupport: TextView
        var crops: TextView
        var fertilizercompanies: TextView

        init {
            collectorid = view.findViewById(R.id.collectorid)
            date = view.findViewById(R.id.date)
            quantitysold = view.findViewById(R.id.quantity_sold)
            collectorname = view.findViewById(R.id.name)
            quantityproduced = view.findViewById(R.id.quantity_produced)
            soillabname = view.findViewById(R.id.soillabname)
            soillabtest = view.findViewById(R.id.soillabtest)
            numberofblends = view.findViewById(R.id.noofblends)
            typeofsupport = view.findViewById(R.id.typeofsupport)
            crops = view.findViewById(R.id.crops)
            fertilizercompanies = view.findViewById(R.id.companies)
        }
    }
}
