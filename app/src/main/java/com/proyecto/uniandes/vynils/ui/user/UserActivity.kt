package com.proyecto.uniandes.vynils.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.uniandes.vynils.MainActivity
import com.proyecto.uniandes.vynils.databinding.ActivityUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        viewModel.getUser {
            if (it != null) {
                navigateToMain()
            } else {
                binding = ActivityUserBinding.inflate(layoutInflater)
                setContentView(binding.root)
                setupButtons()
            }
        }

        viewModel.gotoMain.observe(this) {
            if (it) {
                navigateToMain()
            }
        }
    }

    private fun setupButtons() {
        binding.btnUsuario.setOnClickListener {
            viewModel.saveUser("USUARIO")
        }

        binding.btnColeccionista.setOnClickListener {
            viewModel.saveUser("COLECCIONISTA")
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}