package com.proyecto.uniandes.vynils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
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
import androidx.navigation.findNavController

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [28])
class ConsultarArtistaDetalleTest {

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
        runBlocking {
            repository.clearUser()
        }
        Thread.sleep(100)
        if (::database.isInitialized && database.isOpen) {
            database.close()
        }
    }

    private fun waitForCondition(scenario: ActivityScenario<MainActivity>, timeoutMs: Long = 5000L, predicate: (MainActivity) -> Boolean) {
        val start = System.currentTimeMillis()
        var conditionMet = false
        while (!conditionMet && System.currentTimeMillis() - start < timeoutMs) {
            scenario.onActivity { activity ->
                conditionMet = predicate(activity)
            }
            if (!conditionMet) {
                Thread.sleep(50)
            }
        }
        if (!conditionMet) {
            throw AssertionError("Timed out waiting for condition")
        }
    }

    private fun navigateToArtistFragment(scenario: ActivityScenario<MainActivity>) {
        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_artist)
        }
        Thread.sleep(500)
    }

    @Test
    fun artistDetailFragmentIsOpenedWhenArtistIsClicked() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView.scrollToPosition(0)
                val firstItemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
                firstItemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.artistDetailFragment
            }

            scenario.onActivity { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.artistDetailFragment, navController.currentDestination?.id)
            }
        }
    }

    @Test
    fun artistDetailFragmentDisplaysCorrectArtistData() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView.scrollToPosition(0)
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.artistDetailFragment
            }

            waitForCondition(scenario) { activity ->
                val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                artistNameTextView?.text?.isNotBlank() == true
            }

            scenario.onActivity { activity ->
                val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                assertEquals("Shakira", artistNameTextView.text.toString())

                val birthDateTextView = activity.findViewById<TextView>(R.id.tv_birth_date)
                assertEquals("1977-02-02", birthDateTextView.text.toString())

                val descriptionTextView = activity.findViewById<TextView>(R.id.tv_description)
                assertNotNull(descriptionTextView.text)
                assertEquals("Cantante y compositora colombiana", descriptionTextView.text.toString())
            }
        }
    }

    @Test
    fun artistDetailFragmentDisplaysArtistImage() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView.scrollToPosition(0)
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.artistDetailFragment
            }

            waitForCondition(scenario) { activity ->
                activity.findViewById<ImageView>(R.id.iv_artist_image) != null
            }

            scenario.onActivity { activity ->
                val artistImageView = activity.findViewById<ImageView>(R.id.iv_artist_image)
                assertNotNull(artistImageView)
                assertEquals(View.VISIBLE, artistImageView.visibility)
            }
        }
    }

    @Test
    fun backButtonNavigatesBackToArtistFragment() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView.scrollToPosition(0)
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.artistDetailFragment
            }

            scenario.onActivity { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigateUp()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.navigation_artist
            }

            scenario.onActivity { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_artist, navController.currentDestination?.id)
            }
        }
    }

    @Test
    fun allArtistsCanBeViewedIndividually() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView?.adapter?.itemCount == 3
            }

            val expectedArtistNames = listOf("Shakira", "Juanes", "Carlos Vives")
            val expectedBirthDates = listOf("1977-02-02", "1972-08-09", "1961-08-07")

            for (i in 0 until 3) {
                scenario.onActivity { activity ->
                    val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                    recyclerView.scrollToPosition(i)
                    recyclerView.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                }

                waitForCondition(scenario) { activity ->
                    val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                    navController.currentDestination?.id == R.id.artistDetailFragment
                }

                waitForCondition(scenario) { activity ->
                    val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                    artistNameTextView?.text?.isNotBlank() == true
                }

                scenario.onActivity { activity ->
                    val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                    assertEquals("Artist ${i + 1}: ${expectedArtistNames[i]}", expectedArtistNames[i], artistNameTextView.text.toString())

                    val birthDateTextView = activity.findViewById<TextView>(R.id.tv_birth_date)
                    assertEquals("Birth Date ${i + 1}: ${expectedBirthDates[i]}", expectedBirthDates[i], birthDateTextView.text.toString())
                }

                scenario.onActivity { activity ->
                    val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                    navController.navigateUp()
                }

                waitForCondition(scenario) { activity ->
                    val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                    navController.currentDestination?.id == R.id.navigation_artist
                }
            }
        }
    }

    @Test
    fun loadingPanelIsShownWhileFetchingArtistDetail() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)
            scenario.onActivity { activity ->
                val loadingPanel = activity.findViewById<View>(R.id.loading_panel)
                assertEquals(View.GONE, loadingPanel.visibility)
            }
        }
    }
}

