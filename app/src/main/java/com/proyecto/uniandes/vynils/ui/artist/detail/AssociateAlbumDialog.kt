package com.proyecto.uniandes.vynils.ui.artist.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.databinding.DialogAssociateAlbumBinding

class AssociateAlbumDialog(
    private val albums: List<ResponseAlbum>,
    private val onAlbumSelected: (ResponseAlbum) -> Unit
) : DialogFragment() {

    private var _binding: DialogAssociateAlbumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAssociateAlbumBinding.inflate(layoutInflater)

        setupSpinner()
        setupButtons()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupSpinner() {
        val albumNames = albums.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            albumNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAlbums.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnAssociate.setOnClickListener {
            val selectedPosition = binding.spinnerAlbums.selectedItemPosition
            if (selectedPosition >= 0 && selectedPosition < albums.size) {
                onAlbumSelected(albums[selectedPosition])
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}