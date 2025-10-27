package com.proyecto.uniandes.vynils

import android.widget.TextView
import androidx.navigation.Navigation
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

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [28])
class ConsultarCatalogoAlbumTest {
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
        // Close database to prevent resource leaks
        database.close()
    }

    @Test
    fun albumFragmentIsDisplayedByDefault() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(500) // Wait for fragment to load

            scenario.onActivity { activity ->
                // Verify we are in the correct fragment
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_album, navController.currentDestination?.id)

                // Verify RecyclerView is displayed in AlbumFragment
                val recyclerView = activity.findViewById<android.view.View>(R.id.rv_albums)
                assertNotNull(recyclerView)
                assertEquals(android.view.View.VISIBLE, recyclerView.visibility)
            }
        }
    }

    @Test
    fun albumsAreLoadedAndDisplayed() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(1000) // Wait for albums to load

            scenario.onActivity { activity ->
                // Verify we are in the correct fragment
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_album, navController.currentDestination?.id)

                // Verify RecyclerView has items
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                assertNotNull("RecyclerView should not be null", recyclerView)

                val adapter = recyclerView.adapter
                assertNotNull("Adapter should not be null", adapter)

                val itemCount = adapter?.itemCount ?: 0
                assertEquals("RecyclerView should have exactly 3 mock albums", 3, itemCount)

                // Verify first album is displayed with correct data
                val firstItemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
                assertNotNull("First item view should not be null", firstItemView)

                val albumName = firstItemView?.findViewById<TextView>(R.id.tv_name)
                assertNotNull("Album name TextView should not be null", albumName)
                assertEquals("First album should be 'Buscando América'", "Buscando América", albumName?.text.toString())
            }
        }
    }

    @Test
    fun verifySpecificAlbumData() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(1000) // Wait for albums to load

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
                assertNotNull(recyclerView)

                // Verify we have 3 albums
                assertEquals(3, recyclerView.adapter?.itemCount)

                // Verify first album: "Buscando América"
                val firstItem = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
                val firstName = firstItem?.findViewById<TextView>(R.id.tv_name)
                assertEquals("Buscando América", firstName?.text.toString())

                // Verify second album: "Poeta del pueblo"
                val secondItem = recyclerView.findViewHolderForAdapterPosition(1)?.itemView
                val secondName = secondItem?.findViewById<TextView>(R.id.tv_name)
                assertEquals("Poeta del pueblo", secondName?.text.toString())

                // Verify third album: "A Day at the Races"
                val thirdItem = recyclerView.findViewHolderForAdapterPosition(2)?.itemView
                val thirdName = thirdItem?.findViewById<TextView>(R.id.tv_name)
                assertEquals("A Day at the Races", thirdName?.text.toString())
            }
        }
    }

    @Test
    fun fabIsHiddenForUsuario() {
        runBlocking { repository.saveUser("USUARIO") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(500) // Wait for user to be loaded

            scenario.onActivity { activity ->
                // Verify we are in AlbumFragment
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_album, navController.currentDestination?.id)

                // Verify FAB is hidden for USUARIO
                val fab = activity.findViewById<android.view.View>(R.id.fab_add_album)
                assertNotNull(fab)
                assert(fab.visibility == android.view.View.GONE || fab.visibility == android.view.View.INVISIBLE)
            }
        }
    }

    @Test
    fun fabIsVisibleForColeccionista() {
        runBlocking { repository.saveUser("COLECCIONISTA") }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(500) // Wait for user to be loaded and FAB to show

            scenario.onActivity { activity ->
                // Verify we are in AlbumFragment
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
                assertEquals(R.id.navigation_album, navController.currentDestination?.id)

                // Verify FAB is visible for COLECCIONISTA
                val fab = activity.findViewById<android.view.View>(R.id.fab_add_album)
                assertNotNull(fab)
                assertEquals(android.view.View.VISIBLE, fab.visibility)
            }
        }
    }
}
