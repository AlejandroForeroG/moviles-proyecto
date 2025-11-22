package com.proyecto.uniandes.vynils.ui.album.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.proyecto.uniandes.vynils.databinding.FragmentAlbumDetailBinding
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.model.RequestComment
import com.proyecto.uniandes.vynils.ui.comment.view.CommentAdapter
import com.proyecto.uniandes.vynils.utils.toShortDate
import kotlin.text.clear

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {

    private lateinit var binding: FragmentAlbumDetailBinding
    private val viewModel: AlbumDetailViewModel by viewModels()
    private val args: AlbumDetailFragmentArgs by navArgs()
    private lateinit var commentAdapter: CommentAdapter


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

        viewModel.getAlbumById(args.albumId)
    }

    private fun setupView() {
        with(binding) {
            loadingPanel.message.text = getString(R.string.cargando_detalles_del_album)
            nsvContent.visibility = View.GONE
            loadingPanel.root.visibility = View.VISIBLE

            commentAdapter = CommentAdapter()
            rvComments.adapter = commentAdapter

            btnSubmitComment.setOnClickListener {
                createComment()
            }
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
            viewModel.getComments(album.id)
        }

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)
        }

        viewModel.addCommentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                cleanCommentForm()

                android.widget.Toast.makeText(
                    requireContext(),
                    "Comentario agregado exitosamente",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } else {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Error al agregar comentario",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun cleanCommentForm(){
        with(binding){
            etComment.setText("")
            rbRating.rating = 0f
        }
    }

    private fun createComment(){
        with(binding){
            val albumId = viewModel.selectedAlbum.value?.id
            val commentDescription = etComment.text?.toString()?.trim() ?: ""
            val rating = rbRating.rating.toInt()

            viewModel.addComment(
                albumId = albumId ?: 0,
                RequestComment(
                    description = commentDescription,
                    rating = rating,
                    collector = mapOf("id" to 101)
                )
            )
        }
    }
}
