package com.proyecto.uniandes.vynils.ui.artist.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.databinding.FragmentArtistDetailBinding
import com.proyecto.uniandes.vynils.utils.toShortDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistDetailFragment : Fragment() {

    private lateinit var binding: FragmentArtistDetailBinding
    private val viewModel: ArtistDetailViewModel by viewModels()
    private val args: ArtistDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        setupView()
        setupViewModel()

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
            loadingPanel.root.visibility = View.VISIBLE
        }
    }

    private fun setupViewModel() {
        viewModel.selectedArtist.observe(viewLifecycleOwner) { artist ->
            with(binding) {
                loadingPanel.root.visibility = View.GONE
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
    }
}

