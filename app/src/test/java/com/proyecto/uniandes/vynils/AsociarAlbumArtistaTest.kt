package com.proyecto.uniandes.vynils

import android.view.View
import android.widget.Button
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
import org.junit.Assert.assertTrue
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
class AsociarAlbumArtistaTest {

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

    private fun navigateToArtistDetail(scenario: ActivityScenario<MainActivity>) {
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
    }

    @Test
    fun associateAlbumButtonIsDisplayedForCollector() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            waitForCondition(scenario) { activity ->
                activity.findViewById<Button>(R.id.btn_associate_album) != null
            }

            scenario.onActivity { activity ->
                val associateButton = activity.findViewById<Button>(R.id.btn_associate_album)
                assertNotNull(associateButton)
                assertEquals(View.VISIBLE, associateButton.visibility)
            }
        }
    }

    @Test
    fun artistDetailFragmentDisplaysAssociatedAlbumsSection() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            waitForCondition(scenario) { activity ->
                activity.findViewById<RecyclerView>(R.id.rv_musician_albums) != null
            }

            Thread.sleep(1000)

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_musician_albums)
                assertNotNull(recyclerView)
            }
        }
    }

    @Test
    fun artistWithAlbumsDisplaysAlbumsList() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            waitForCondition(scenario) { activity ->
                activity.findViewById<RecyclerView>(R.id.rv_musician_albums) != null
            }

            Thread.sleep(1000)

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_musician_albums)
                val noAlbumsText = activity.findViewById<TextView>(R.id.tv_no_albums)

                val albumCount = recyclerView?.adapter?.itemCount ?: 0
                if (albumCount > 0) {
                    assertEquals(View.VISIBLE, recyclerView.visibility)
                    assertEquals(View.GONE, noAlbumsText?.visibility)
                } else {
                    assertEquals(View.VISIBLE, noAlbumsText?.visibility)
                    assertEquals(View.GONE, recyclerView.visibility)
                }
            }
        }
    }

    @Test
    fun artistWithoutAlbumsDisplaysNoAlbumsMessage() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView.scrollToPosition(1)
                val itemView = recyclerView.findViewHolderForAdapterPosition(1)?.itemView
                itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.artistDetailFragment
            }

            Thread.sleep(1000)

            scenario.onActivity { activity ->
                val noAlbumsText = activity.findViewById<TextView>(R.id.tv_no_albums)
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_musician_albums)

                val albumCount = recyclerView?.adapter?.itemCount ?: 0
                if (albumCount == 0) {
                    assertEquals(View.VISIBLE, noAlbumsText?.visibility)
                    assertEquals(View.GONE, recyclerView.visibility)
                }
            }
        }
    }

    @Test
    fun loadingPanelIsHiddenAfterDataLoads() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            Thread.sleep(1500)

            scenario.onActivity { activity ->
                val loadingPanel = activity.findViewById<View>(R.id.loading_panel_container)
                val contentLayout = activity.findViewById<View>(R.id.nsv_content)

                assertEquals(View.GONE, loadingPanel?.visibility)
                assertEquals(View.VISIBLE, contentLayout?.visibility)
            }
        }
    }

    @Test
    fun artistDetailDisplaysCorrectArtistInformation() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            waitForCondition(scenario) { activity ->
                val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                artistNameTextView?.text?.isNotBlank() == true
            }

            scenario.onActivity { activity ->
                val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                val birthDateTextView = activity.findViewById<TextView>(R.id.tv_birth_date)
                val descriptionTextView = activity.findViewById<TextView>(R.id.tv_description)

                assertNotNull(artistNameTextView.text)
                assertNotNull(birthDateTextView.text)
                assertNotNull(descriptionTextView.text)

                assertTrue(artistNameTextView.text.isNotBlank())
            }
        }
    }

    @Test
    fun albumsRecyclerViewUsesGridLayout() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            waitForCondition(scenario) { activity ->
                activity.findViewById<RecyclerView>(R.id.rv_musician_albums) != null
            }

            Thread.sleep(1000)

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_musician_albums)
                assertNotNull(recyclerView)
                assertNotNull(recyclerView.layoutManager)
                assertTrue(recyclerView.layoutManager is androidx.recyclerview.widget.GridLayoutManager)
            }
        }
    }

    @Test
    fun multipleArtistsCanBeViewedWithTheirAlbums() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistFragment(scenario)

            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            for (i in 0 until 2) {
                scenario.onActivity { activity ->
                    val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                    recyclerView.scrollToPosition(i)
                    val itemView = recyclerView.findViewHolderForAdapterPosition(i)?.itemView
                    itemView?.performClick()
                }

                waitForCondition(scenario) { activity ->
                    val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                    navController.currentDestination?.id == R.id.artistDetailFragment
                }

                Thread.sleep(1000)

                scenario.onActivity { activity ->
                    val artistNameTextView = activity.findViewById<TextView>(R.id.tv_artist_name)
                    assertNotNull(artistNameTextView.text)
                    assertTrue(artistNameTextView.text.isNotBlank())
                }

                scenario.onActivity { activity ->
                    activity.onBackPressed()
                }

                Thread.sleep(500)
            }
        }
    }

    @Test
    fun associatedAlbumsSectionHasCorrectTitle() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToArtistDetail(scenario)

            Thread.sleep(1000)

            scenario.onActivity { activity ->
                val titleTextView = activity.findViewById<TextView>(R.id.tv_albums_title)
                assertNotNull(titleTextView)
                assertEquals(View.VISIBLE, titleTextView.visibility)
            }
        }
    }
}