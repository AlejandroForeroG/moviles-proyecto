package com.proyecto.uniandes.vynils.ui.album.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.proyecto.uniandes.vynils.databinding.FragmentAlbumDetailBinding
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.utils.toShortDate

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {

    private lateinit var binding: FragmentAlbumDetailBinding
    private val viewModel: AlbumDetailViewModel by viewModels()
    private val args: AlbumDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)

        setupView()
        setupViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getAlbumById(args.albumId)
    }

    private fun setupView() {
        with(binding) {
            loadingPanel.message.text = "Cargando detalles del álbum..."
        }
    }

    private fun setupViewModel() {
        viewModel.selectedAlbum.observe(viewLifecycleOwner) { album ->
            with(binding) {
                loadingPanel.root.visibility = View.GONE
                nsvContent.visibility = View.VISIBLE

                tvAlbumName.text = album.name
                tvReleaseDate.text = album.releaseDate.toShortDate()
                tvGenre.text = album.genre
                tvProducer.text = album.recordLabel
                tvDescription.text = album.description
                ivAlbumCover.load(album.cover) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }
            }
        }
    }
}
