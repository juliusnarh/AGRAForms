package com.uclgroupgh.form.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.transitionseverywhere.Fade
import com.transitionseverywhere.TransitionManager
import com.transitionseverywhere.TransitionSet
import com.transitionseverywhere.extra.Scale
import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.CollectorInfo
import com.uclgroupgh.form.interfaces.CollecterinfoClickListener
import com.uclgroupgh.form.utils.AndroidUtils

class CollectorAdapter(
    private val mContext: Context,
    private val collectorInfoList: List<CollectorInfo>,
    internal var listener: CollecterinfoClickListener
) : RecyclerView.Adapter<CollectorAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_collector, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val info = collectorInfoList[position]
        holder.collectorid.text = info.collectorid
        holder.project.text = info.project
        holder.name.text = info.collectorname
        holder.region.text = info.region
        holder.country.text = info.country
        holder.year.text = info.year
        holder.quarter.text = info.quarter
        holder.grantee.text = info.grantee
        //        holder.district.setText(info.getProject());
        holder.date.text = AndroidUtils.dateToFormattedString(info.entrydate!!, "dd MMM, yyyy")

        holder.more.setOnClickListener(object : View.OnClickListener {
            internal var visible: Boolean = false

            override fun onClick(v: View) {
                val set = TransitionSet()
                    .addTransition(Scale(0.7f))
                    .addTransition(Fade())
                    .setInterpolator(
                        if (visible)
                            LinearOutSlowInInterpolator()
                        else
                            FastOutLinearInInterpolator()
                    )

                TransitionManager.beginDelayedTransition(holder.container, set)
                visible = !visible
                holder.viewmore.visibility = if (visible) View.VISIBLE else View.GONE
            }
        })

        holder.itemView.setOnClickListener { listener.onItemListener(info.collectorid) }
    }

    override fun getItemCount(): Int {
        return collectorInfoList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView
        var collectorid: TextView
        var name: TextView
        var region: TextView
        var district: TextView
        var grantee: TextView
        var country: TextView
        var year: TextView
        var quarter: TextView
        var project: TextView
        var container: ViewGroup
        var viewmore: LinearLayout
        internal var more: ImageView

        init {
            collectorid = view.findViewById(R.id.collectorid)
            date = view.findViewById(R.id.date)
            name = view.findViewById(R.id.name)
            region = view.findViewById(R.id.region)
            district = view.findViewById(R.id.district)
            grantee = view.findViewById(R.id.grantee)
            country = view.findViewById(R.id.country)
            year = view.findViewById(R.id.year)
            quarter = view.findViewById(R.id.quarter)
            project = view.findViewById(R.id.project)
            container = view.findViewById(R.id.container)
            viewmore = view.findViewById(R.id.viewmore)
            more = view.findViewById(R.id.vmore)
        }
    }
}
