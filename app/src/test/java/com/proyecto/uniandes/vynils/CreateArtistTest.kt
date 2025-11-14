package com.proyecto.uniandes.vynils

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
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
class CreateArtistTest {

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

    private fun navigateToArtist(scenario: ActivityScenario<MainActivity>) {
        scenario.onActivity { activity ->
            val navView = activity.findViewById<BottomNavigationView>(R.id.button_nav)
            navView.selectedItemId = R.id.navigation_artist
        }
        Thread.sleep(400)
    }

    @Test
    fun formShowsErrorsWhenFieldsEmpty() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)
            Thread.sleep(300)

            scenario.onActivity { activity ->
                val fab = activity.findViewById<View>(R.id.fab_add_artist)
                assertNotNull(fab)
                fab.performClick()
            }

            Thread.sleep(300)

            scenario.onActivity { activity ->
                val btnCrear = activity.findViewById<View>(R.id.btnCrear)
                btnCrear.performClick()

                val nombre = activity.findViewById<TextInputLayout>(R.id.nombreTextView)
                val descripcion = activity.findViewById<TextInputLayout>(R.id.descriptionTextField)
                val birthDate = activity.findViewById<TextInputLayout>(R.id.birthDateTextView)
                val url = activity.findViewById<TextInputLayout>(R.id.urlImageTextView)

                assertEquals("El nombre es obligatorio", nombre.error?.toString())
                assertEquals("La descripción es obligatoria", descripcion.error?.toString())
                assertEquals("La fecha de nacimiento es obligatoria", birthDate.error?.toString())
                assertEquals("Ingrese una URL válida", url.error?.toString())
            }
        }
    }

    @Test
    fun createArtistAddsArtistToList() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)
            Thread.sleep(300)

            scenario.onActivity { activity ->
                val fab = activity.findViewById<View>(R.id.fab_add_artist)
                fab.performClick()
            }

            Thread.sleep(300)

            val artistName = "Robolectric Test Artist"
            val description = "Descripción robolectric artista"
            val birthDate = "02/02/1992"
            val imageUrl = "https://example.com/robolectricartist.jpg"

            scenario.onActivity { activity ->
                activity.findViewById<EditText>(R.id.nombreEditText).setText(artistName)
                activity.findViewById<EditText>(R.id.descriptionEditText).setText(description)
                activity.findViewById<EditText>(R.id.birthDateInputEditText).setText(birthDate)
                activity.findViewById<EditText>(R.id.urlImageEditText).setText(imageUrl)

                val btnCrear = activity.findViewById<View>(R.id.btnCrear)
                btnCrear.performClick()
            }

            Thread.sleep(600)

            scenario.onActivity { activity ->
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_artist, navController.currentDestination?.id)

                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                assertNotNull(recyclerView)

                val adapter = recyclerView.adapter
                assertNotNull(adapter)
                val itemCount = adapter?.itemCount ?: 0
                assertEquals(4, itemCount) // initial 3 + 1 created

                recyclerView.measure(
                    View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.AT_MOST)
                )
                recyclerView.layout(0, 0, 1080, 1920)
                val lastPosition = (itemCount - 1).coerceAtLeast(0)
                val holder = recyclerView.findViewHolderForAdapterPosition(lastPosition)
                if (holder != null) {
                    val nameView = holder.itemView.findViewById<TextView>(R.id.tv_name)
                    assertEquals(artistName, nameView.text.toString())
                }
            }
        }
    }
}