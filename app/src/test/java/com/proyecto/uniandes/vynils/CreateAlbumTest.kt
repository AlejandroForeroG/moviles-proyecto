package com.proyecto.uniandes.vynils

import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.google.android.material.textfield.TextInputLayout
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [28])
class CreateAlbumTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var database: VynilsDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking { repository.clearUser() }
    }

    @After
    fun tearDown() {
        runBlocking { repository.clearUser() }
        database.close()
    }

    @Test
    fun formShowsErrorsWhenFieldsEmpty() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(500)

            // Open create album screen by clicking FAB
            scenario.onActivity { activity ->
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_album, navController.currentDestination?.id)

                val fab = activity.findViewById<android.view.View>(R.id.fab_add_album)
                assertNotNull(fab)
                fab.performClick()
            }

            Thread.sleep(300)

            scenario.onActivity { activity ->
                // Click create without filling fields
                val btnCrear = activity.findViewById<android.view.View>(R.id.btnCrear)
                assertNotNull(btnCrear)
                btnCrear.performClick()

                // Check TextInputLayout errors
                val nombre = activity.findViewById<TextInputLayout>(R.id.nombreTextView)
                val descripcion = activity.findViewById<TextInputLayout>(R.id.descriptionTextField)
                val release = activity.findViewById<TextInputLayout>(R.id.releaseDateTextView)
                val url = activity.findViewById<TextInputLayout>(R.id.urlCoverTextView)
                val genero = activity.findViewById<TextInputLayout>(R.id.generoTextView)
                val disquera = activity.findViewById<TextInputLayout>(R.id.disqueraTextView)

                assertEquals("El nombre es obligatorio", nombre.error?.toString())
                assertEquals("La descripción es obligatoria", descripcion.error?.toString())
                assertEquals("La fecha de lanzamiento es obligatoria", release.error?.toString())
                assertEquals("Ingrese una URL válida", url.error?.toString())
                assertEquals("El género es obligatorio", genero.error?.toString())
                assertEquals("La disquera es obligatoria", disquera.error?.toString())
            }
        }
    }

    @Test
    fun createAlbumAddsAlbumToList() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(500)

            // Open create album screen
            scenario.onActivity { activity ->
                val fab = activity.findViewById<android.view.View>(R.id.fab_add_album)
                fab.performClick()
            }

            Thread.sleep(300)

            val albumName = "Robolectric Test Album"
            val description = "Descripción robolectric"
            val releaseDate = "01/01/2021"
            val coverUrl = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg"
            val genero = "Rock"
            val disquera = "Sony"

            scenario.onActivity { activity ->
                // Fill fields directly
                val nombreEdit = activity.findViewById<android.widget.EditText>(R.id.nombreEditText)
                val descriptionEdit = activity.findViewById<android.widget.EditText>(R.id.descriptionEditText)
                val releaseEdit = activity.findViewById<android.widget.EditText>(R.id.releaseDateInputEditText)
                val urlEdit = activity.findViewById<android.widget.EditText>(R.id.urlCoverEditText)
                val generoAuto = activity.findViewById<android.widget.AutoCompleteTextView>(R.id.generoAutoComplete)
                val disqueraAuto = activity.findViewById<android.widget.AutoCompleteTextView>(R.id.disqueraAutoComplete)

                nombreEdit.setText(albumName)
                descriptionEdit.setText(description)
                releaseEdit.setText(releaseDate)
                urlEdit.setText(coverUrl)
                generoAuto.setText(genero)
                disqueraAuto.setText(disquera)

                // Click create
                val btnCrear = activity.findViewById<android.view.View>(R.id.btnCrear)
                btnCrear.performClick()
            }

            Thread.sleep(500)

            // After navigate up, check album list
            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                assertNotNull("RecyclerView should not be null", recyclerView)

                val adapter = recyclerView.adapter
                assertNotNull("Adapter should not be null", adapter)

                val itemCount = adapter?.itemCount ?: 0
                // initial 3 + 1 created
                assertEquals("RecyclerView should have 4 albums", 4, itemCount)

                // verify last album name
                val lastPosition = (itemCount - 1).coerceAtLeast(0)
                val holder = recyclerView.findViewHolderForAdapterPosition(lastPosition)
                if (holder != null) {
                    val nameView = holder.itemView.findViewById<TextView>(R.id.tv_name)
                    assertEquals(albumName, nameView.text.toString())
                }
            }
        }
    }
}