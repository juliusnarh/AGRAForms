package com.uclgroupgh.form.adapters

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.SeedProduction
import com.uclgroupgh.form.utils.AndroidUtils

class SeedProductionAdapter(private val seedProductionList: List<SeedProduction>) :
    RecyclerView.Adapter<SeedProductionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_seedproduction, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val res = holder.itemView.resources
        val production = seedProductionList[position]
        holder.collectorid.text = production.getCollectorid()
        holder.date.text =
            AndroidUtils.dateToFormattedString(production.getDatecreated(), "dd MMM, yyyy")
        holder.collectorname.text = production.getCollectorname()
        holder.currency.text = production.getCurrency()
        holder.unitprice.text = production.getUnitprice().toString()
        holder.quantitycertified.text =
            res.getString(R.string.mt, production.getQuantitycertified().toString())
        holder.quantityproduced.text =
            res.getString(R.string.mt, production.getQuantityproduced().toString())
        holder.quantitysold.text =
            res.getString(R.string.mt, production.getQuantitysold().toString())
        holder.variety.text = production.getVariety()
        holder.firstimeproduction.text = production.getFirsttimeinproduction()
        holder.plotsize.text = res.getString(R.string.ha, production.getPlotsize())
        holder.seedproductionitem.text = production.getItem()
    }

    override fun getItemCount(): Int {
        return seedProductionList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView
        var collectorid: TextView
        var collectorname: TextView
        var currency: TextView
        var unitprice: TextView
        var quantitysold: TextView
        var quantitycertified: TextView
        var quantityproduced: TextView
        var variety: TextView
        var firstimeproduction: TextView
        var plotsize: TextView
        var seedproductionitem: TextView

        init {
            collectorid = view.findViewById(R.id.collectorid)
            date = view.findViewById(R.id.date)
            currency = view.findViewById(R.id.currency)
            unitprice = view.findViewById(R.id.unitprice)
            quantitysold = view.findViewById(R.id.quantity_sold)
            collectorname = view.findViewById(R.id.name)
            quantityproduced = view.findViewById(R.id.quantity_produced)
            quantitycertified = view.findViewById(R.id.quantity_certified)
            variety = view.findViewById(R.id.variety)
            firstimeproduction = view.findViewById(R.id.firstimeinproduction)
            plotsize = view.findViewById(R.id.plotsize)
            seedproductionitem = view.findViewById(R.id.seedproductionitem)
        }
    }
}
