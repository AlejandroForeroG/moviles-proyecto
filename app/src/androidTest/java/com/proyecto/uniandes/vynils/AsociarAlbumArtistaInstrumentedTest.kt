package com.proyecto.uniandes.vynils

import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import androidx.navigation.findNavController

@HiltAndroidTest
class AsociarAlbumArtistaInstrumentedTest {

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

    private fun navigateToArtistDetail() {
        navigateToArtistFragment()

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }

        Thread.sleep(1000)
    }

    @Test
    fun associateAlbumButtonIsDisplayedForCollector() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistDetail()

        onView(withId(R.id.btn_associate_album)).check(matches(isDisplayed()))
    }


    @Test
    fun associateButtonInDialogAssociatesAlbum() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistDetail()

        var initialAlbumCount = 0
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_musician_albums)
            initialAlbumCount = recyclerView?.adapter?.itemCount ?: 0
        }

        onView(withId(R.id.btn_associate_album)).perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.spinner_albums)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_associate)).perform(click())

        Thread.sleep(2000)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_musician_albums)
            val newAlbumCount = recyclerView?.adapter?.itemCount ?: 0
            assertTrue(newAlbumCount >= initialAlbumCount)
        }
    }

    @Test
    fun cancelButtonClosesDialog() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistDetail()

        onView(withId(R.id.btn_associate_album)).perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.btn_cancel)).perform(click())

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.findFragmentByTag("AssociateAlbumDialog")
            assertEquals(null, fragment)
        }
    }

    @Test
    fun associatedAlbumsAreDisplayedInRecyclerView() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtistDetail()

        onView(withId(R.id.rv_musician_albums)).check(matches(isDisplayed()))
    }
}