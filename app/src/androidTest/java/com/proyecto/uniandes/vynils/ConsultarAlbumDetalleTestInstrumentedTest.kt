package com.proyecto.uniandes.vynils

import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import androidx.navigation.findNavController

@HiltAndroidTest
class ConsultarAlbumDetalleInstrumentedTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var database: VynilsDatabase

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking { repository.clearUser() }
    }

    @After
    fun tearDown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
        runBlocking { repository.clearUser() }
        database.close()
    }

    @Test
    fun albumDetailFragmentIsOpenedWhenAlbumIsClicked() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.albumDetailFragment, navController.currentDestination?.id)
        }
    }

    @Test
    fun albumDetailFragmentDisplaysCorrectAlbumData() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(1000)

        onView(withId(R.id.tv_album_name)).check(matches(withText("Buscando América")))
        onView(withId(R.id.tv_genre)).check(matches(withText("Salsa")))
        onView(withId(R.id.tv_release_date)).check(matches(withText("1984-08-01")))
        onView(withId(R.id.tv_producer)).check(matches(withText("Elektra")))
        onView(withId(R.id.tv_description)).check(matches(isDisplayed()))
    }

    @Test
    fun albumDetailFragmentDisplaysCoverImage() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(1000)

        onView(withId(R.id.iv_album_cover)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_album_cover)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun backButtonNavigatesBackToAlbumFragment() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.albumDetailFragment, navController.currentDestination?.id)
        }

        scenario.onActivity { activity ->
            activity.findViewById<View>(R.id.ib_back)?.performClick()
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_album, navController.currentDestination?.id)
        }
    }

    @Test
    fun allAlbumsCanBeViewedIndividually() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000)

        val expectedAlbumNames = listOf("Buscando América", "Poeta del pueblo", "A Day at the Races")
        val expectedGenres = listOf("Salsa", "Regional Mexicano", "Rock")

        for (i in 0 until 3) {
            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                recyclerView.scrollToPosition(i)
                Thread.sleep(100)
                recyclerView.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
            }

            Thread.sleep(1000)

            onView(withId(R.id.tv_album_name)).check(matches(withText(expectedAlbumNames[i])))
            onView(withId(R.id.tv_genre)).check(matches(withText(expectedGenres[i])))

            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.ib_back)?.performClick()
            }

            Thread.sleep(500)
        }
    }

    @Test
    fun loadingPanelIsShownWhileFetchingAlbumDetail() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val loadingPanel = activity.findViewById<View>(R.id.loading_panel)
            val contentLayout = activity.findViewById<View>(R.id.nsv_content)

            assertEquals(View.GONE, loadingPanel.visibility)
            assertEquals(View.VISIBLE, contentLayout.visibility)
        }
    }
}