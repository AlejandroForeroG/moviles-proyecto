package com.proyecto.uniandes.vynils.ui.artist.create

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
import com.proyecto.uniandes.vynils.data.model.RequestArtist
import com.proyecto.uniandes.vynils.databinding.FragmentCreateArtistBinding
import com.proyecto.uniandes.vynils.utils.showErrorServer
import com.proyecto.uniandes.vynils.utils.toIso8601UtcFromDdMMyyyy
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class CreateArtistFragment: Fragment() {

    private lateinit var binding: FragmentCreateArtistBinding
    private val viewModel: CreateArtistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateArtistBinding.inflate(inflater, container, false)
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
            birthDateInputEditText.apply {
                isFocusable = false
                setOnClickListener {
                    val builder = MaterialDatePicker.Builder.datePicker()
                    builder.setTitleText("Selecciona fecha")

                    if (birthDateInputEditText.text?.toString()?.isNotEmpty() == true) {
                        try {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val parsedDate = dateFormat.parse(birthDateInputEditText.text.toString())
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
                        birthDateInputEditText.setText(formattedDate)
                    }
                    datePicker.addOnNegativeButtonClickListener {
                        birthDateInputEditText.clearFocus()
                    }
                    datePicker.addOnDismissListener {
                        birthDateInputEditText.clearFocus()
                    }

                    datePicker.show(parentFragmentManager, "DATE_PICKER")
                }
            }

            urlImageTextView.editText?.doOnTextChanged { text, _, _, _ ->
                val url = text?.toString()?.trim()
                if (!url.isNullOrEmpty() && Patterns.WEB_URL.matcher(url).matches()) {
                    urlImageTextView.error = null
                    imagePreviewImageView.visibility = View.VISIBLE
                    imagePreviewImageView.load(url) {
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                    }
                } else {
                    urlImageTextView.error = "Ingrese una URL válida"
                    imagePreviewImageView.visibility = View.GONE
                    imagePreviewImageView.setImageDrawable(null)
                }
            }

            btnCancelar.setOnClickListener {
                findNavController().navigateUp()
            }

            btnCrear.setOnClickListener {
                if (isFormValid()) {
                    createArtist()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        var isValid = true

        with(binding) {
            val name = nombreTextView.editText?.text?.toString()?.trim()
            val description = descriptionTextField.editText?.text?.toString()?.trim()
            val birthDate = birthDateTextView.editText?.text?.toString()?.trim()
            val urlImage = urlImageTextView.editText?.text?.toString()?.trim()

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

            if (birthDate.isNullOrEmpty()) {
                birthDateTextView.error = "La fecha de nacimiento es obligatoria"
                isValid = false
            } else {
                birthDateTextView.error = null
            }

            if (urlImage.isNullOrEmpty() || !Patterns.WEB_URL.matcher(urlImage).matches()) {
                urlImageTextView.error = "Ingrese una URL válida"
                isValid = false
            } else {
                urlImageTextView.error = null
            }
        }

        return isValid
    }

    private fun cleanForm() {
        with(binding) {
            nombreTextView.editText?.setText("")
            descriptionTextField.editText?.setText("")
            birthDateTextView.editText?.setText("")
            urlImageTextView.editText?.setText("")
            imagePreviewImageView.visibility = View.GONE
            imagePreviewImageView.setImageDrawable(null)
        }
    }

    private fun createArtist() {
        with(binding) {
            val name = nombreTextView.editText?.text?.toString()?.trim() ?: ""
            val description = descriptionTextField.editText?.text?.toString()?.trim() ?: ""
            val birthDate = birthDateTextView.editText?.text?.toString()?.trim() ?: ""
            val urlImage = urlImageTextView.editText?.text?.toString()?.trim() ?: ""

            scrollView.visibility = View.GONE
            loadingPanel.root.visibility = View.VISIBLE
            loadingPanel.message.text = getString(R.string.creando_artistas)

            viewModel.createArtist(
                RequestArtist(
                    name = name,
                    image = urlImage,
                    description = description,
                    birthDate = birthDate.toIso8601UtcFromDdMMyyyy()
                )
            )
        }
    }
}