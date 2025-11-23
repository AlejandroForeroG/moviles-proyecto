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
import androidx.test.espresso.Espresso.pressBack

@HiltAndroidTest
class ConsultarArtistaDetalleInstrumentedTest {

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

    private fun navigateToArtistFragment() {
        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_artist)
        }
        Thread.sleep(1000)
    }

    @Test
    fun artistDetailFragmentIsOpenedWhenArtistIsClicked() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistFragment()

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.artistDetailFragment, navController.currentDestination?.id)
        }
    }

    @Test
    fun artistDetailFragmentDisplaysCorrectArtistData() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistFragment()

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(1000)

        onView(withId(R.id.tv_artist_name)).check(matches(withText("Shakira")))
        onView(withId(R.id.tv_birth_date)).check(matches(withText("1977-02-02")))
        onView(withId(R.id.tv_description)).check(matches(withText("Cantante y compositora colombiana")))
    }

    @Test
    fun artistDetailFragmentDisplaysArtistImage() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistFragment()

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(1000)

        onView(withId(R.id.iv_artist_image)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_artist_image)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun backButtonNavigatesBackToArtistFragment() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistFragment()

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.artistDetailFragment, navController.currentDestination?.id)
        }

        pressBack()

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_artist, navController.currentDestination?.id)
        }
    }

    @Test
    fun allArtistsCanBeViewedIndividually() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistFragment()

        val expectedArtistNames = listOf("Shakira", "Juanes", "Carlos Vives")
        val expectedBirthDates = listOf("1977-02-02", "1972-08-09", "1961-08-07")

        for (i in 0 until 3) {
            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
                recyclerView.scrollToPosition(i)
                Thread.sleep(100)
                recyclerView.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
            }

            Thread.sleep(1000)

            onView(withId(R.id.tv_artist_name)).check(matches(withText(expectedArtistNames[i])))
            onView(withId(R.id.tv_birth_date)).check(matches(withText(expectedBirthDates[i])))

            pressBack()

            Thread.sleep(500)
        }
    }

    @Test
    fun loadingPanelIsShownWhileFetchingArtistDetail() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistFragment()

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
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

