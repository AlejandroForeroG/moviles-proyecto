package com.proyecto.uniandes.vynils.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showErrorServer(context: Context, onSuccess: () -> Unit) {

    MaterialAlertDialogBuilder(context)
        .setTitle("Error de servidor")
        .setMessage("Ha ocurrido un error en el servidor. Por favor, intenta de nuevo..")
        .setPositiveButton("Aceptar") { _, _ ->
            onSuccess()
        }
        .show()
}