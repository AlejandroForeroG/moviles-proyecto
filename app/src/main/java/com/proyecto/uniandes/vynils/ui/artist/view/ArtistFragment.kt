package com.proyecto.uniandes.vynils.ui.artist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import com.proyecto.uniandes.vynils.databinding.FragmentArtistBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : Fragment() {

    private lateinit var binding: FragmentArtistBinding
    private val viewModel: ArtistViewModel by viewModels()
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentArtistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupView()
        setupViewModel()
        setupRecyclerView()
        return root
    }

    private fun setupView() {
        with(binding) {
            loadingPanel.message.text = getString(R.string.cargando_artistas)
        }
    }

    private fun setupViewModel() {

        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                validateUser(it)
                viewModel.getAllArtist()
            }
        }

        viewModel.albums.observe(viewLifecycleOwner) { list ->
            with(binding) {
                if (list.isNotEmpty()) {
                    artistAdapter.submitList(list)
                    rvArtists.visibility = View.VISIBLE
                    loadingPanel.root.visibility = View.GONE
                } else {
                    rvArtists.visibility = View.GONE
                    loadingPanel.root.visibility = View.VISIBLE
                    loadingPanel.message.text = getString(R.string.no_hay_artistas_disponibles)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        artistAdapter = ArtistAdapter { }

        binding.rvArtists.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = artistAdapter
            setHasFixedSize(true)
        }
    }

    private fun validateUser(user: UserEntity) {
        when(user.userType) {
            "USUARIO" -> {
                binding.fabAddArtist.hide()
            }
            "COLECCIONISTA" -> {
                binding.fabAddArtist.show()
            }
            else -> { }
        }
    }
}