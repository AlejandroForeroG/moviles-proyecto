package com.proyecto.uniandes.vynils

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
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ConsultarCatalogoAlbumInstrumentedTest {
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
        // Close database to prevent resource leaks
        database.close()
    }

    @Test
    fun albumFragmentIsDisplayedByDefault() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(500) // Wait for fragment to load

        // Verify we are in the correct fragment
        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_album, navController.currentDestination?.id)
        }

        // Verify RecyclerView is displayed in AlbumFragment
        onView(withId(R.id.rv_albums)).check(matches(isDisplayed()))
    }

    @Test
    fun albumsAreLoadedAndDisplayed() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000) // Wait for albums to load

        // Verify we are in the correct fragment
        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_album, navController.currentDestination?.id)

            // Verify RecyclerView has exactly 3 mock albums
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            assertNotNull("RecyclerView should not be null", recyclerView)

            val adapter = recyclerView.adapter
            assertNotNull("Adapter should not be null", adapter)

            val itemCount = adapter?.itemCount ?: 0
            assertEquals("RecyclerView should have exactly 3 mock albums", 3, itemCount)
        }

        // Verify RecyclerView is displayed
        onView(withId(R.id.rv_albums)).check(matches(isDisplayed()))

        // Verify first album "Buscando América" is displayed
        onView(withText("Buscando América")).check(matches(isDisplayed()))
    }

    @Test
    fun verifySpecificAlbumData() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1000) // Wait for albums to load

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)

            // Verify we have exactly 3 albums
            assertEquals(3, recyclerView.adapter?.itemCount)
        }

        // Verify all three album names are displayed
        onView(withText("Buscando América")).check(matches(isDisplayed()))
        onView(withText("Poeta del pueblo")).check(matches(isDisplayed()))
        onView(withText("A Day at the Races")).check(matches(isDisplayed()))
    }

    @Test
    fun fabIsVisibleForColeccionista() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(500) // Wait for user to be loaded and FAB to show

        // Verify we are in AlbumFragment
        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_album, navController.currentDestination?.id)
        }

        // Verify FAB is visible for COLECCIONISTA
        onView(withId(R.id.fab_add_album)).check(matches(isDisplayed()))
    }

    @Test
    fun fabIsHiddenForUsuario() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(500) // Wait for user to be loaded

        // Verify we are in AlbumFragment
        scenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
            assertEquals(R.id.navigation_album, navController.currentDestination?.id)
        }

        // Verify FAB is hidden for USUARIO
        onView(withId(R.id.fab_add_album)).check(matches(not(isDisplayed())))
    }
}
