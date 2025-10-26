package com.proyecto.uniandes.vynils.ui.album.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.proyecto.uniandes.vynils.databinding.FragmentCreateAlbumBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAlbumFragment: Fragment() {

    private lateinit var binding: FragmentCreateAlbumBinding
    private val viewModel: CreateAlbumViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateAlbumBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
}