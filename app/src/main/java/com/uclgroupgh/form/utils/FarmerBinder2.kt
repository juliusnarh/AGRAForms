package com.uclgroupgh.form.utils

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.Farmers
import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder


class FarmerBinder2 : ItemBinder<Farmers, FarmerBinder2.CarViewHolder>() {

    override fun createViewHolder(parent: ViewGroup): CarViewHolder {
        return CarViewHolder(inflate(parent, R.layout.item_import))
    }

    override fun canBindData(item: Any): Boolean {
        return item is Farmers
    }

    override fun bindViewHolder(holder: CarViewHolder, item: Farmers) {
        holder.name.text = holder.itemView.resources.getString(R.string.farmer_fullname, item.surname, item.othername)
        holder.phone.text = item.tel
        holder.community.text = item.community
        holder.farmerid.text = item.farmerid
        Glide.with(holder.itemView.context).load(AndroidUtils.loadFarmerImg(item.farmerid!!, holder.itemView.resources)).apply(RequestOptions.circleCropTransform()).into(holder.thumbnail)
        holder.verified.visibility = if (holder.isItemSelected) View.VISIBLE else View.INVISIBLE
        holder.container.cardElevation = if (holder.isItemSelected) 5f else 1f
        holder.itemView.setOnClickListener { holder.toggleItemSelection() }
    }

    class CarViewHolder(view: View) : ItemViewHolder<Farmers>(view) {
        var container: CardView = view.findViewById(R.id.item_container)
        var verified: ImageView = view.findViewById(R.id.verified)
        var name: TextView = view.findViewById(R.id.name)
        var phone: TextView = view.findViewById(R.id.phone)
        var community: TextView = view.findViewById(R.id.community)
        var farmerid: TextView = view.findViewById(R.id.farmerid)
        var thumbnail: ImageView = view.findViewById(R.id.thumbnail)
    }
}