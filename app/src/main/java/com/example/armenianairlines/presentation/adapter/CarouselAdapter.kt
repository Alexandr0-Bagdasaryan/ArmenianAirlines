package com.example.armenianairlines.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.armenianairlines.R
import com.example.armenianairlines.data.model.CarouselModel
import com.example.armenianairlines.databinding.CarouselItemBinding

class CarouselAdapter(val context: Context,
    val listener:OnItemClickListener) :
    RecyclerView.Adapter<CarouselAdapter.ItemViewHolder>() {


    var list = mutableListOf<CarouselModel>()


    fun setDestinations(destinations:MutableList<CarouselModel>){
        list= destinations
        notifyDataSetChanged()
    }


    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val binding = CarouselItemBinding.bind(itemView)
        fun bind(model: CarouselModel) {
            binding.apply {
                carouselImageView.setImageResource(model.imageId)
                textViewCity.text = model.title
            }
        }
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carousel_item,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselAdapter.ItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getCurrentItem(position: Int):CarouselModel{
        return list[position]
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}