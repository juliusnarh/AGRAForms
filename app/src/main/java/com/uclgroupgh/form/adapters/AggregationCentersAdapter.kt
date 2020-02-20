package com.uclgroupgh.form.adapters

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.AggregationCenters
import com.uclgroupgh.form.utils.AndroidUtils

class AggregationCentersAdapter(private val aggregationCentersList: List<AggregationCenters>) :
    RecyclerView.Adapter<AggregationCentersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aggregationcenters, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val res = holder.itemView.resources
        val centers = aggregationCentersList[position]
        holder.collectorid.text = centers.getCollectorid()
        holder.date.text =
            AndroidUtils.dateToFormattedString(centers.getEntrydate(), "dd MMM, yyyy")
        holder.collectorname.text = centers.getCollectorName()
        holder.storagefacilityname.text = centers.getStorageFacilityName()
        holder.location.text = centers.getLocation()
        holder.newConstructionType.text = centers.getNewConstructionType()
        holder.refurbishedType.text = centers.getRefurbishedType()
        holder.warehouseType.text = centers.getWarehouseType()
        holder.silosType.text = centers.getSilosType()
        holder.picsbagsType.text = centers.getPicsbagsType()
        holder.storesType.text = centers.getStoresType()
        holder.volume.text = centers.getVolume()
        holder.crop.text = centers.getCrop()
        holder.quantityStored.text = res.getString(R.string.mt, centers.getQuantityStored())
        holder.handlingCost.text = centers.getHandlingCost()
        holder.currency.text = centers.getCurrency()
        holder.servedFarmers.text = centers.getServedFarmers()
        holder.quantitySold.text = res.getString(R.string.mt, centers.getQuantitySold())
        holder.averagePrice.text = centers.getAveragePrice()
    }

    override fun getItemCount(): Int {
        return aggregationCentersList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView
        var collectorid: TextView
        var collectorname: TextView
        var storagefacilityname: TextView
        var location: TextView
        var newConstructionType: TextView
        var refurbishedType: TextView
        var warehouseType: TextView
        var silosType: TextView
        var picsbagsType: TextView
        var storesType: TextView
        var volume: TextView
        var crop: TextView
        var quantityStored: TextView
        var handlingCost: TextView
        var currency: TextView
        var servedFarmers: TextView
        var quantitySold: TextView
        var averagePrice: TextView

        init {
            collectorid = view.findViewById(R.id.collectorid)
            date = view.findViewById(R.id.date)
            collectorname = view.findViewById(R.id.name)
            storagefacilityname = view.findViewById(R.id.storagefacilityname)
            location = view.findViewById(R.id.location)
            newConstructionType = view.findViewById(R.id.newConstructiontype)
            refurbishedType = view.findViewById(R.id.refurbishedtype)
            warehouseType = view.findViewById(R.id.warehousetype)
            silosType = view.findViewById(R.id.silotype)
            picsbagsType = view.findViewById(R.id.picsbagstype)
            storesType = view.findViewById(R.id.storestype)
            volume = view.findViewById(R.id.volume)
            crop = view.findViewById(R.id.crops)
            quantityStored = view.findViewById(R.id.quantitystored)
            handlingCost = view.findViewById(R.id.handlingCost)
            currency = view.findViewById(R.id.currency)
            servedFarmers = view.findViewById(R.id.farmersserved)
            quantitySold = view.findViewById(R.id.quantity_sold)
            averagePrice = view.findViewById(R.id.averageprice)
        }
    }
}
