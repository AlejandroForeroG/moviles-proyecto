package com.proyecto.uniandes.vynils.ui.artist.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.databinding.FragmentArtistDetailBinding
import com.proyecto.uniandes.vynils.ui.album.view.AlbumAdapter
import com.proyecto.uniandes.vynils.utils.toShortDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistDetailFragment : Fragment() {

    private lateinit var binding: FragmentArtistDetailBinding
    private val viewModel: ArtistDetailViewModel by viewModels()
    private val args: ArtistDetailFragmentArgs by navArgs()

    private lateinit var albumAdapter: AlbumAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        setupView()
        setupViewModel()
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getArtistById(args.artistId)
    }

    private fun setupView() {
        with(binding) {
            loadingPanel.message.text = getString(R.string.cargando_detalles_del_artista)
            nsvContent.visibility = View.GONE
            loadingPanelContainer.visibility = View.VISIBLE

            btnAssociateAlbum.setOnClickListener {
                viewModel.loadAvailableAlbums(args.artistId)
            }
        }
    }

    private fun setupViewModel() {
        viewModel.selectedArtist.observe(viewLifecycleOwner) { artist ->
            with(binding) {
                loadingPanelContainer.visibility = View.GONE
                nsvContent.visibility = View.VISIBLE

                tvArtistName.text = artist.name
                tvBirthDate.text = artist.birthDate?.toShortDate() ?: ""
                tvDescription.text = artist.description
                ivArtistImage.load(artist.image) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }
            }
        }

        viewModel.artistAlbums.observe(viewLifecycleOwner) { albums ->
            if (albums.isEmpty()) {
                binding.tvNoAlbums.visibility = View.VISIBLE
                binding.rvMusicianAlbums.visibility = View.GONE
            } else {
                binding.tvNoAlbums.visibility = View.GONE
                binding.rvMusicianAlbums.visibility = View.VISIBLE
                albumAdapter.submitList(albums)
            }
        }

        viewModel.availableAlbums.observe(viewLifecycleOwner) { albums ->  // ← CAMBIAR AQUÍ
            if (albums.isNotEmpty()) {
                showAssociateAlbumDialog(albums)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingPanelContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.associationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.album_associated_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearAssociationSuccess()
            }
        }

    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter { album ->
        }

        binding.rvMusicianAlbums.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = albumAdapter
        }
    }

    private fun showAssociateAlbumDialog(albums: List<com.proyecto.uniandes.vynils.data.model.ResponseAlbum>) {
        val dialog = AssociateAlbumDialog(albums) { selectedAlbum ->
            viewModel.associateAlbum(args.artistId, selectedAlbum.id)
        }
        dialog.show(childFragmentManager, "AssociateAlbumDialog")
    }
}

