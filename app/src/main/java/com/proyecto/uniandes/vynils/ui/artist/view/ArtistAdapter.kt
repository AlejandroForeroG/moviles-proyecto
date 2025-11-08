package com.proyecto.uniandes.vynils.ui.artist.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.uniandes.vynils.R
import coil.load
import com.proyecto.uniandes.vynils.data.model.ResponseArtist

class ArtistAdapter(private val onClick: ((ResponseArtist) -> Unit)? = null) : ListAdapter<ResponseArtist, ArtistAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View, private val onClick: ((ResponseArtist) -> Unit)?) : RecyclerView.ViewHolder(itemView) {
        private val imgCover: ImageView = itemView.findViewById(R.id.img_cover)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(album: ResponseArtist) {
            tvName.text = album.name
            imgCover.load(album.image) {
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
            }

            itemView.setOnClickListener {
                onClick?.invoke(album)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ResponseArtist>() {
            override fun areItemsTheSame(oldItem: ResponseArtist, newItem: ResponseArtist): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ResponseArtist, newItem: ResponseArtist): Boolean = oldItem == newItem
        }
    }
}