package com.uclgroupgh.form.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.transitionseverywhere.Fade
import com.transitionseverywhere.TransitionManager
import com.transitionseverywhere.TransitionSet
import com.transitionseverywhere.extra.Scale
import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.Farmers

import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder

class FarmerBinder(
    private val mContext: Context,
    private val farmersList: List<Farmers>
) : RecyclerView.Adapter<FarmerBinder.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_import, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = farmersList[position]

//        val bgColor = ContextCompat.getColor(
//            holder.name.context,
//            if (holder.isItemSelected) R.color.badgebackground else R.color.white
//        )

//        holder.container.cardElevation = (if (holder.isItemSelected) 16 else 0).toFloat()

        holder.name.text =
            mContext.resources.getString(
                R.string.farmer_fullname,
                item.surname,
                item.othername
            )
        holder.phone.text = item.tel
        holder.community.text = item.community
        holder.farmerid.text = item.farmerid
        Glide.with(mContext)
            .load(AndroidUtils.loadFarmerImg(item.farmerid!!, holder.itemView.resources))
            .apply(RequestOptions.circleCropTransform()).into(holder.thumbnail)
//        holder.container.setBackgroundColor(bgColor)
    }

    override fun getItemCount(): Int {
        return farmersList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView
        val name: TextView
        val phone: TextView
        val community: TextView
        val farmerid: TextView
        val container: CardView

        init {
            container = itemView.findViewById(R.id.item_container)
            name = itemView.findViewById(R.id.name)
            phone = itemView.findViewById(R.id.phone)
            community = itemView.findViewById(R.id.community)
            farmerid = itemView.findViewById(R.id.farmerid)
            thumbnail = itemView.findViewById(R.id.thumbnail)
        }
    }
}
