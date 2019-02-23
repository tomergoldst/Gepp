package com.tomergoldst.gepp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tomergoldst.gepp.R
import com.tomergoldst.gepp.config.GlideApp
import com.tomergoldst.gepp.model.Place

class PlaceRecyclerListAdapter(val mListener: OnAdapterInteractionListener?) :
    ListAdapter<Place, PlaceRecyclerListAdapter.PlaceViewHolder>(DiffCallback()){

    companion object {
        val TAG: String = PlaceRecyclerListAdapter::class.java.simpleName
    }

    interface OnAdapterInteractionListener {
        fun onItemClicked(place: Place)
    }

    inner class PlaceViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val placeNameTxv: AppCompatTextView = v.findViewById(R.id.placeNameTxv)
        private val placeIconImv: AppCompatImageView = v.findViewById(R.id.placeIconImv)
        private val placeRatingTxv: AppCompatTextView = v.findViewById(R.id.placeRatingTxv)

        fun bind(place: Place){
            placeNameTxv.text = place.name

            placeRatingTxv.text = place.rating.toString()
            placeRatingTxv.visibility = if (place.rating > 0) View.VISIBLE else View.GONE

            itemView.setOnClickListener{
                mListener?.onItemClicked(place)
            }

            GlideApp.with(itemView.context)
                .load(place.icon)
                .placeholder(R.drawable.ic_place_black_24dp)
                .into(placeIconImv)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(v)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val item = getItem(holder.adapterPosition)
        holder.bind(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }
    }

}
