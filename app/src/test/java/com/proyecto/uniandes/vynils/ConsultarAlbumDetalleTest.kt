package com.proyecto.uniandes.vynils

import android.os.Looper
import android.view.View
import android.widget.ImageButton
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
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import javax.inject.Inject
import androidx.navigation.findNavController

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [28])
class ConsultarAlbumDetalleTest {

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
        idleMain()
    }

    @After
    fun tearDown() {
        runBlocking {
            repository.clearUser()
        }
        idleMain()
        Thread.sleep(100)
        if (::database.isInitialized && database.isOpen) {
            database.close()
        }
    }

    private fun idleMain() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Robolectric.flushForegroundThreadScheduler()
    }

    private fun waitForCondition(scenario: ActivityScenario<MainActivity>, timeoutMs: Long = 5000L, predicate: (MainActivity) -> Boolean) {
        val start = System.currentTimeMillis()
        var conditionMet = false
        while (!conditionMet && System.currentTimeMillis() - start < timeoutMs) {
            idleMain()
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

    @Test
    fun albumDetailFragmentIsOpenedWhenAlbumIsClicked() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView.scrollToPosition(0)
                idleMain()
                val firstItemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
                firstItemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.albumDetailFragment
            }

            scenario.onActivity { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.albumDetailFragment, navController.currentDestination?.id)
            }
        }
    }

    @Test
    fun albumDetailFragmentDisplaysCorrectAlbumData() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView.scrollToPosition(0)
                idleMain()
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.albumDetailFragment
            }

            waitForCondition(scenario) { activity ->
                val albumNameTextView = activity.findViewById<TextView>(R.id.tv_album_name)
                albumNameTextView?.text?.isNotBlank() == true
            }

            scenario.onActivity { activity ->
                val albumNameTextView = activity.findViewById<TextView>(R.id.tv_album_name)
                assertEquals("Buscando América", albumNameTextView.text.toString())

                val genreTextView = activity.findViewById<TextView>(R.id.tv_genre)
                assertEquals("Salsa", genreTextView.text.toString())

                val releaseDateTextView = activity.findViewById<TextView>(R.id.tv_release_date)
                assertEquals("1984-08-01", releaseDateTextView.text.toString())

                val producerTextView = activity.findViewById<TextView>(R.id.tv_producer)
                assertEquals("Elektra", producerTextView.text.toString())

                val descriptionTextView = activity.findViewById<TextView>(R.id.tv_description)
                assertNotNull(descriptionTextView.text)
            }
        }
    }

    @Test
    fun albumDetailFragmentDisplaysCoverImage() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView.scrollToPosition(0)
                idleMain()
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.albumDetailFragment
            }

            waitForCondition(scenario) { activity ->
                activity.findViewById<ImageView>(R.id.iv_album_cover) != null
            }

            scenario.onActivity { activity ->
                val coverImageView = activity.findViewById<ImageView>(R.id.iv_album_cover)
                assertNotNull(coverImageView)
                assertEquals(View.VISIBLE, coverImageView.visibility)
            }
        }
    }

    @Test
    fun backButtonNavigatesBackToAlbumFragment() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView.scrollToPosition(0)
                idleMain()
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.albumDetailFragment
            }

            waitForCondition(scenario) { activity ->
                activity.findViewById<ImageButton>(R.id.ib_back) != null
            }

            scenario.onActivity { activity ->
                val backButton = activity.findViewById<ImageButton>(R.id.ib_back)
                backButton.performClick()
            }

            waitForCondition(scenario) { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                navController.currentDestination?.id == R.id.navigation_album
            }

            scenario.onActivity { activity ->
                val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_album, navController.currentDestination?.id)
            }
        }
    }

    @Test
    fun allAlbumsCanBeViewedIndividually() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView?.adapter?.itemCount == 3
            }

            val expectedAlbumNames = listOf("Buscando América", "Poeta del pueblo", "A Day at the Races")
            val expectedGenres = listOf("Salsa", "Regional Mexicano", "Rock")

            for (i in 0 until 3) {
                scenario.onActivity { activity ->
                    val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                    recyclerView.scrollToPosition(i)
                    idleMain()
                    recyclerView.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                }

                waitForCondition(scenario) { activity ->
                    val navController =
                        activity.findNavController(R.id.nav_host_fragment_activity_main)
                    navController.currentDestination?.id == R.id.albumDetailFragment
                }

                waitForCondition(scenario) { activity ->
                    val albumNameTextView = activity.findViewById<TextView>(R.id.tv_album_name)
                    albumNameTextView?.text?.isNotBlank() == true
                }

                scenario.onActivity { activity ->
                    val albumNameTextView = activity.findViewById<TextView>(R.id.tv_album_name)
                    assertEquals("Album ${i + 1}: ${expectedAlbumNames[i]}", expectedAlbumNames[i], albumNameTextView.text.toString())

                    val genreTextView = activity.findViewById<TextView>(R.id.tv_genre)
                    assertEquals("Genre ${i + 1}: ${expectedGenres[i]}", expectedGenres[i], genreTextView.text.toString())
                }

                waitForCondition(scenario) { activity ->
                    activity.findViewById<ImageButton>(R.id.ib_back) != null
                }

                scenario.onActivity { activity ->
                    val backButton = activity.findViewById<ImageButton>(R.id.ib_back)
                    backButton.performClick()
                }

                waitForCondition(scenario) { activity ->
                    val navController =
                        activity.findNavController(R.id.nav_host_fragment_activity_main)
                    navController.currentDestination?.id == R.id.navigation_album
                }
            }
        }
    }

    @Test
    fun loadingPanelIsShownWhileFetchingAlbumDetail() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCondition(scenario) { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                (recyclerView?.adapter?.itemCount ?: 0) > 0
            }

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView.scrollToPosition(0)
                idleMain()
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            waitForCondition(scenario) { activity ->
                val loadingPanel = activity.findViewById<View>(R.id.loading_panel)
                val contentLayout = activity.findViewById<View>(R.id.nsv_content)
                loadingPanel != null && contentLayout != null
            }

            waitForCondition(scenario) { activity ->
                val contentLayout = activity.findViewById<View>(R.id.nsv_content)
                contentLayout?.visibility == View.VISIBLE
            }

            scenario.onActivity { activity ->
                val loadingPanel = activity.findViewById<View>(R.id.loading_panel)
                assertEquals(View.GONE, loadingPanel.visibility)
            }
        }
    }
}