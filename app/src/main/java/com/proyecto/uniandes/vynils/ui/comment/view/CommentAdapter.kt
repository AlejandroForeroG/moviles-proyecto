package com.proyecto.uniandes.vynils.ui.comment.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.model.ResponseComment

class CommentAdapter: ListAdapter<ResponseComment, CommentAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCollectorName: TextView = itemView.findViewById(R.id.tv_collector_name)
        private val tvRating: RatingBar = itemView.findViewById(R.id.tv_rating)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_commentdescription)

        fun bind(comment: ResponseComment) {
            tvCollectorName.text = "Usuario"
            tvRating.rating= comment.rating.toFloat()
            tvDescription.text = comment.description
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseComment>() {
            override fun areItemsTheSame(oldItem: ResponseComment, newItem: ResponseComment): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ResponseComment, newItem: ResponseComment): Boolean = oldItem == newItem
        }
    }
}