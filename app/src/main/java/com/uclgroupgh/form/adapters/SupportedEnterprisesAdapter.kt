package com.uclgroupgh.form.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.SupportedEnterprise
import com.uclgroupgh.form.utils.AndroidUtils

class SupportedEnterprisesAdapter(private val supportedEnterpriseList: List<SupportedEnterprise>) :
    RecyclerView.Adapter<SupportedEnterprisesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_enterprises, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val enterprise = supportedEnterpriseList[position]
        holder.collectorid.text = enterprise.getCollectorid()
        holder.date.text =
            AndroidUtils.dateToFormattedString(enterprise.getEntrydate(), "dd MMM, yyyy")
        holder.collectorName.text = enterprise.getCollectorName()
        holder.enterpriseName.text = enterprise.getEnterpriseName()
        holder.contacts.text = enterprise.getContacts()
        holder.agroDealerType.text = enterprise.getAgroDealerType()
        holder.seedProducer.text = enterprise.getSeedProducer()
        holder.femaleOwnerStatus.text = enterprise.getFemaleOwnerStatus()
        holder.youthOwnerStatus.text = enterprise.getYouthOwnerStatus()
        holder.fullTimeEmploymentGender.text = enterprise.getFullTimeEmploymentGender()
        holder.casualEmploymentGender.text = enterprise.getCasualEmploymentGender()
        holder.ageGroup.text = enterprise.getAgeGroup()
        holder.partTimeEmploymentGender.text = enterprise.getPartTimeEmploymentGender()
    }

    override fun getItemCount(): Int {
        return supportedEnterpriseList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView
        var collectorid: TextView
        var collectorName: TextView
        var enterpriseName: TextView
        var contacts: TextView
        var agroDealerType: TextView
        var seedProducer: TextView
        var femaleOwnerStatus: TextView
        var youthOwnerStatus: TextView
        var fullTimeEmploymentGender: TextView
        var casualEmploymentGender: TextView
        var ageGroup: TextView
        var partTimeEmploymentGender: TextView


        init {
            collectorid = view.findViewById(R.id.collectorid)
            date = view.findViewById(R.id.date)
            collectorName = view.findViewById(R.id.name)
            enterpriseName = view.findViewById(R.id.enterprisename)
            contacts = view.findViewById(R.id.contact)
            agroDealerType = view.findViewById(R.id.agrodealertype)
            seedProducer = view.findViewById(R.id.seedproducer)
            femaleOwnerStatus = view.findViewById(R.id.femaleownerstatus)
            youthOwnerStatus = view.findViewById(R.id.youthownerstatus)
            fullTimeEmploymentGender = view.findViewById(R.id.fullTimeEmploymentGender)
            casualEmploymentGender = view.findViewById(R.id.casualEmploymentGender)
            ageGroup = view.findViewById(R.id.agegroup)
            partTimeEmploymentGender = view.findViewById(R.id.partTimeEmploymentGender)
        }
    }
}
