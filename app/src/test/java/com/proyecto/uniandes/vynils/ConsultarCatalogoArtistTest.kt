package com.proyecto.uniandes.vynils

import android.view.View
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
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
class ConsultarCatalogoArtistTest {
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
        // Allow navigation + data load
        Thread.sleep(400)
    }

    @Test
    fun artistFragmentIsDisplayedByNavigation() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)

            scenario.onActivity { activity ->
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_artist, navController.currentDestination?.id)

                val recyclerView = activity.findViewById<View>(R.id.rv_artists)
                assertNotNull(recyclerView)
                assertEquals(View.VISIBLE, recyclerView.visibility)
            }
        }
    }

    @Test
    fun artistsAreLoadedAndDisplayed() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)
            Thread.sleep(700) // Wait for artist list to load

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                assertNotNull("RecyclerView should not be null", recyclerView)

                val adapter = recyclerView.adapter
                assertNotNull("Adapter should not be null", adapter)

                val itemCount = adapter?.itemCount ?: 0
                assertEquals("RecyclerView should have exactly 3 mock artists", 3, itemCount)

                // Ensure layout so that view holders are created
                recyclerView.measure(
                    View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.AT_MOST)
                )
                recyclerView.layout(0, 0, 1080, 1920)

                val firstItemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
                assertNotNull("First artist item view should not be null", firstItemView)

                val artistName = firstItemView?.findViewById<TextView>(R.id.tv_name)
                assertNotNull("Artist name TextView should not be null", artistName)
                // We assume first artist is Shakira based on mock ordering
                assertEquals("First artist should be 'Shakira'", "Shakira", artistName?.text.toString())
            }
        }
    }

    @Test
    fun verifySpecificArtistsData() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)
            Thread.sleep(700) // Wait for artist list to load

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                assertNotNull(recyclerView)
                assertEquals(3, recyclerView.adapter?.itemCount)

                // Force layout
                recyclerView.measure(
                    View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.AT_MOST)
                )
                recyclerView.layout(0, 0, 1080, 1920)

                val firstItem = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
                val firstName = firstItem?.findViewById<TextView>(R.id.tv_name)
                assertEquals("Shakira", firstName?.text.toString())

                val secondItem = recyclerView.findViewHolderForAdapterPosition(1)?.itemView
                val secondName = secondItem?.findViewById<TextView>(R.id.tv_name)
                assertEquals("Juanes", secondName?.text.toString())

                val thirdItem = recyclerView.findViewHolderForAdapterPosition(2)?.itemView
                val thirdName = thirdItem?.findViewById<TextView>(R.id.tv_name)
                assertEquals("Carlos Vives", thirdName?.text.toString())
            }
        }
    }

    @Test
    fun fabIsVisibleForColeccionista() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)
            Thread.sleep(500)

            scenario.onActivity { activity ->
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_artist, navController.currentDestination?.id)

                val fab = activity.findViewById<View>(R.id.fab_add_artist)
                assertNotNull(fab)
                assertEquals(View.VISIBLE, fab.visibility)
            }
        }
    }

    @Test
    fun fabIsHiddenForUsuario() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtist(scenario)
            Thread.sleep(500)

            scenario.onActivity { activity ->
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_artist, navController.currentDestination?.id)

                val fab = activity.findViewById<View>(R.id.fab_add_artist)
                assertNotNull(fab)
                assert(fab.visibility == View.GONE || fab.visibility == View.INVISIBLE)
            }
        }
    }
}

