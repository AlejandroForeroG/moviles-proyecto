package com.proyecto.uniandes.vynils

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.uniandes.vynils.databinding.ActivityMainBinding
import com.proyecto.uniandes.vynils.ui.user.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val activityViewModel: MainViewModel by viewModels()
    private val navHost by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.buttonNav

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_album,
                R.id.navigation_artist
            )
        )
        setupActionBarWithNavController(navHost.navController, appBarConfiguration)
        navView.setupWithNavController(navHost.navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_user -> {
                showChangeUserDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHost.navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showChangeUserDialog() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Cambiar tipo de usuario")
            .setMessage("¿Deseas cambiar el tipo de usuario? Se cerrará la sesión actual.")
            .setPositiveButton("Sí") { _, _ ->
                clearUserAndNavigate()
            }
            .setNegativeButton("Cancelar", null)
            .show()

    }

    private fun clearUserAndNavigate() {
        activityViewModel.clearUser {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}