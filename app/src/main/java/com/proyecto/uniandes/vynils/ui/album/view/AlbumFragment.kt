package com.proyecto.uniandes.vynils.ui.album.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import com.proyecto.uniandes.vynils.databinding.FragmentAlbumBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumFragment : Fragment() {

    private lateinit var binding: FragmentAlbumBinding
    private val viewModel: AlbumViewModel by viewModels()
    private val navHostFragment by lazy {
        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
    }

    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupView()
        setupViewModel()
        setupRecyclerView()
        return root
    }

    private fun setupView() {
        with(binding) {
            fabAddAlbum.setOnClickListener {
                val action = AlbumFragmentDirections.actionNavigationAlbumToCreateAlbumFragment()
                navHostFragment.findNavController().navigate(action)
            }
            loadingPanel.message.text = "Cargando albums..."
        }
    }

    private fun setupViewModel() {

        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                validateUser(it)
                viewModel.getAllAlbums()
            }
        }

        viewModel.albums.observe(viewLifecycleOwner) { list ->
            with(binding) {
                if (list.isNotEmpty()) {
                    albumAdapter.submitList(list)
                    rvAlbums.visibility = View.VISIBLE
                    loadingPanel.root.visibility = View.GONE
                } else {
                    rvAlbums.visibility = View.GONE
                    loadingPanel.root.visibility = View.VISIBLE
                    loadingPanel.message.text = "No hay albums disponibles"
                }
            }
        }
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter { album ->
            val action = AlbumFragmentDirections.actionNavigationAlbumToAlbumDetailFragment(album.id)
            findNavController().navigate(action)
        }

        binding.rvAlbums.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = albumAdapter
            setHasFixedSize(true)
        }
    }

    private fun validateUser(user: UserEntity) {
        when(user.userType) {
            "USUARIO" -> {
                binding.fabAddAlbum.hide()
            }
            "COLECCIONISTA" -> {
                binding.fabAddAlbum.show()
            }
            else -> { }
        }
    }
}