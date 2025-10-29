package com.proyecto.uniandes.vynils

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.textfield.TextInputLayout
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CreateAlbumInstrumentedTest {

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
    fun createAlbumFormShowsErrorsWhenFieldsEmpty() {
        // Make sure user is coleccionista so FAB is visible
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(500)

        // Open create album screen
        onView(withId(R.id.fab_add_album)).perform(click())

        Thread.sleep(300)

        // Click create without filling fields
        onView(withId(R.id.btnCrear)).perform(scrollTo()).perform(click())

        // Expect errors on all required TextInputLayouts
        onView(withId(R.id.nombreTextView)).check(matches(hasTextInputLayoutErrorText("El nombre es obligatorio")))
        onView(withId(R.id.descriptionTextField)).check(matches(hasTextInputLayoutErrorText("La descripción es obligatoria")))
        onView(withId(R.id.releaseDateTextView)).check(matches(hasTextInputLayoutErrorText("La fecha de lanzamiento es obligatoria")))
        onView(withId(R.id.urlCoverTextView)).check(matches(hasTextInputLayoutErrorText("Ingrese una URL válida")))
        onView(withId(R.id.generoTextView)).check(matches(hasTextInputLayoutErrorText("El género es obligatorio")))
        onView(withId(R.id.disqueraTextView)).check(matches(hasTextInputLayoutErrorText("La disquera es obligatoria")))
    }

    @Test
    fun createAlbumAddsAlbumToListAndShowsCoverPreview() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(500)

        // Open create album screen
        onView(withId(R.id.fab_add_album)).perform(click())

        Thread.sleep(300)

        val albumName = "Espresso Test Album"
        val description = "Descripción de prueba"
        val releaseDate = "01/01/2020"
        val coverUrl = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg"
        val genero = "Rock"
        val disquera = "Sony"

        // Fill the form
        onView(withId(R.id.nombreEditText)).perform(scrollTo()).perform(replaceText(albumName), closeSoftKeyboard())
        onView(withId(R.id.descriptionEditText)).perform(scrollTo()).perform(replaceText(description), closeSoftKeyboard())
        onView(withId(R.id.releaseDateInputEditText)).perform(scrollTo()).perform(replaceText(releaseDate), closeSoftKeyboard())
        onView(withId(R.id.urlCoverEditText)).perform(scrollTo()).perform(replaceText(coverUrl), closeSoftKeyboard())

        // wait for cover preview to update (coil loads image async)
        Thread.sleep(800)

        // The cover preview should be visible
        onView(withId(R.id.coverPreviewImageView)).check(matches(isDisplayed()))

        // Fill dropdowns (AutoCompleteTextView)
        onView(withId(R.id.generoAutoComplete)).perform(scrollTo()).perform(replaceText(genero), closeSoftKeyboard())
        onView(withId(R.id.disqueraAutoComplete)).perform(scrollTo()).perform(replaceText(disquera), closeSoftKeyboard())

        // Create album
        onView(withId(R.id.btnCrear)).perform(scrollTo()).perform(click())

        // Wait for navigation back and for list update
        Thread.sleep(1000)

        // Now the new album name should be visible in the album list
        onView(withText(albumName)).check(matches(isDisplayed()))
    }

    // Helper matcher to assert TextInputLayout's error text
    private fun hasTextInputLayoutErrorText(expectedErrorText: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("TextInputLayout should have error text: $expectedErrorText")
            }

            public override fun matchesSafely(view: View): Boolean {
                if (view !is TextInputLayout) return false
                val error = view.error ?: return false
                return error.toString() == expectedErrorText
            }
        }
    }
}