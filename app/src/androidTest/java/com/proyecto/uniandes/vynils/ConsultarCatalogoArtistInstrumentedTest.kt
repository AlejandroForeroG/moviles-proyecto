package com.proyecto.uniandes.vynils

import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ConsultarCatalogoArtistInstrumentedTest {
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

    private fun navigateToArtist() {
        scenario.onActivity { activity ->
            val navView = activity.findViewById<BottomNavigationView>(R.id.button_nav)
            navView.selectedItemId = R.id.navigation_artist
        }
        Thread.sleep(400)
    }

    @Test
    fun artistFragmentIsDisplayedByNavigation() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtist()

        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_artist, navController.currentDestination?.id)
        }

        onView(withId(R.id.rv_artists)).check(matches(isDisplayed()))
    }

    @Test
    fun artistsAreLoadedAndDisplayed() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtist()
        Thread.sleep(700)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_artists)
            assertNotNull("RecyclerView should not be null", recyclerView)
            val adapter = recyclerView.adapter
            assertNotNull("Adapter should not be null", adapter)
            val itemCount = adapter?.itemCount ?: 0
            assertEquals("RecyclerView should have exactly 3 mock artists", 3, itemCount)
        }

        onView(withText("Shakira")).check(matches(isDisplayed()))
    }

    @Test
    fun verifySpecificArtistsData() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtist()
        Thread.sleep(700)

        onView(withText("Shakira")).check(matches(isDisplayed()))
        onView(withText("Juanes")).check(matches(isDisplayed()))
        onView(withText("Carlos Vives")).check(matches(isDisplayed()))
    }

    @Test
    fun fabIsVisibleForColeccionista() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtist()
        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_artist, navController.currentDestination?.id)
        }

        onView(withId(R.id.fab_add_artist)).check(matches(isDisplayed()))
    }

    @Test
    fun fabIsHiddenForUsuario() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navigateToArtist()
        Thread.sleep(500)

        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_artist, navController.currentDestination?.id)
        }

        onView(withId(R.id.fab_add_artist)).check(matches(not(isDisplayed())))
    }
}