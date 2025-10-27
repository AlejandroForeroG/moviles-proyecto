package com.proyecto.uniandes.vynils.ui.album.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import coil.load

class AlbumAdapter(private val onClick: ((ResponseAlbum) -> Unit)? = null) : ListAdapter<ResponseAlbum, AlbumAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View, private val onClick: ((ResponseAlbum) -> Unit)?) : RecyclerView.ViewHolder(itemView) {
        private val imgCover: ImageView = itemView.findViewById(R.id.img_cover)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(album: ResponseAlbum) {
            tvName.text = album.name
            imgCover.load(album.cover) {
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
            }

            itemView.setOnClickListener {
                onClick?.invoke(album)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseAlbum>() {
            override fun areItemsTheSame(oldItem: ResponseAlbum, newItem: ResponseAlbum): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ResponseAlbum, newItem: ResponseAlbum): Boolean = oldItem == newItem
        }
    }
}

