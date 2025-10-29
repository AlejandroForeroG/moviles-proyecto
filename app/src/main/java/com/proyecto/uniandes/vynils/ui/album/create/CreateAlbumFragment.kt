package com.proyecto.uniandes.vynils.ui.album.create

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.datepicker.MaterialDatePicker
import com.proyecto.uniandes.vynils.R
import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.databinding.FragmentCreateAlbumBinding
import com.proyecto.uniandes.vynils.utils.showErrorServer
import com.proyecto.uniandes.vynils.utils.toIso8601UtcFromDdMMyyyy
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

        setupView()
        setupViewModel()
        setupActions()
        return root
    }

    private fun setupView() {
        with(binding) {
            loadingPanel.root.visibility = View.GONE
        }
    }

    private fun setupViewModel() {
        viewModel.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            } else {
                binding.scrollView.visibility = View.VISIBLE
                binding.loadingPanel.root.visibility = View.GONE
                showErrorServer(requireContext()) {
                    cleanForm()
                }
            }
        }
    }

    private fun setupActions() {
        with(binding) {
            releaseDateInputEditText.apply {
                isFocusable = false
                setOnClickListener {
                    val builder = MaterialDatePicker.Builder.datePicker()
                    builder.setTitleText("Selecciona fecha")

                    if (releaseDateInputEditText.text?.toString()?.isNotEmpty() == true) {
                        try {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val parsedDate = dateFormat.parse(releaseDateInputEditText.text.toString())
                            parsedDate?.let { builder.setSelection(it.time) }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    val datePicker = builder.build()
                    datePicker.addOnPositiveButtonClickListener { selection: Long ->
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val formattedDate = dateFormat.format(Date(selection))
                        releaseDateInputEditText.setText(formattedDate)
                    }
                    datePicker.addOnNegativeButtonClickListener {
                        releaseDateInputEditText.clearFocus()
                    }
                    datePicker.addOnDismissListener {
                        releaseDateInputEditText.clearFocus()
                    }

                    datePicker.show(parentFragmentManager, "DATE_PICKER")
                }
            }

            urlCoverTextView.editText?.doOnTextChanged { text, _, _, _ ->
                val url = text?.toString()?.trim()
                if (!url.isNullOrEmpty() && Patterns.WEB_URL.matcher(url).matches()) {
                    urlCoverTextView.error = null
                    coverPreviewImageView.visibility = View.VISIBLE
                    coverPreviewImageView.load(url) {
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                    }
                } else {
                    urlCoverTextView.error = "Ingrese una URL válida"
                    coverPreviewImageView.visibility = View.GONE
                    coverPreviewImageView.setImageDrawable(null)
                }
            }

            btnCancelar.setOnClickListener {
                findNavController().navigateUp()
            }

            btnCrear.setOnClickListener {
                if (isFormValid()) {
                    createAlbum()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        var isValid = true

        with(binding) {
            val name = nombreTextView.editText?.text?.toString()?.trim()
            val description = descriptionTextField.editText?.text?.toString()?.trim()
            val releaseDate = releaseDateTextView.editText?.text?.toString()?.trim()
            val urlCover = urlCoverTextView.editText?.text?.toString()?.trim()
            val genero = generoTextView.editText?.text?.toString()?.trim()
            val disquera = disqueraTextView.editText?.text?.toString()?.trim()


            if (name.isNullOrEmpty()) {
                nombreTextView.error = "El nombre es obligatorio"
                isValid = false
            } else {
                nombreTextView.error = null
            }

            if (description.isNullOrEmpty()) {
                descriptionTextField.error = "La descripción es obligatoria"
                isValid = false
            } else {
                descriptionTextField.error = null
            }

            if (releaseDate.isNullOrEmpty()) {
                releaseDateTextView.error = "La fecha de lanzamiento es obligatoria"
                isValid = false
            } else {
                releaseDateTextView.error = null
            }

            if (urlCover.isNullOrEmpty() || !Patterns.WEB_URL.matcher(urlCover).matches()) {
                urlCoverTextView.error = "Ingrese una URL válida"
                isValid = false
            } else {
                urlCoverTextView.error = null
            }

            if (genero.isNullOrEmpty()) {
                generoTextView.error = "El género es obligatorio"
                isValid = false
            } else {
                generoTextView.error = null
            }

            if (disquera.isNullOrEmpty()) {
                disqueraTextView.error = "La disquera es obligatoria"
                isValid = false
            } else {
                disqueraTextView.error = null
            }
        }

        return isValid
    }

    private fun cleanForm() {
        with(binding) {
            nombreTextView.editText?.setText("")
            descriptionTextField.editText?.setText("")
            releaseDateTextView.editText?.setText("")
            urlCoverTextView.editText?.setText("")
            generoTextView.editText?.setText("")
            disqueraTextView.editText?.setText("")
            coverPreviewImageView.visibility = View.GONE
            coverPreviewImageView.setImageDrawable(null)
        }
    }

    private fun createAlbum() {
        with(binding) {
            val name = nombreTextView.editText?.text?.toString()?.trim() ?: ""
            val description = descriptionTextField.editText?.text?.toString()?.trim() ?: ""
            val releaseDate = releaseDateTextView.editText?.text?.toString()?.trim() ?: ""
            val urlCover = urlCoverTextView.editText?.text?.toString()?.trim() ?: ""
            val genre = generoTextView.editText?.text?.toString()?.trim() ?: ""
            val recordLabel = disqueraTextView.editText?.text?.toString()?.trim() ?: ""

            scrollView.visibility = View.GONE
            loadingPanel.root.visibility = View.VISIBLE
            loadingPanel.message.text = "Creando album..."

            viewModel.createAlbum(
                RequestAlbum(
                    name = name,
                    cover = urlCover,
                    releaseDate = releaseDate.toIso8601UtcFromDdMMyyyy(),
                    description = description,
                    genre = genre,
                    recordLabel = recordLabel
                )
            )
        }
    }
}