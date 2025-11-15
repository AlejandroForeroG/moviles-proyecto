package com.proyecto.uniandes.vynils

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.bottomnavigation.BottomNavigationView
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
class CreateArtistInstrumentTest {

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

    private fun navigateToArtistTab() {
        scenario.onActivity { activity ->
            val navView = activity.findViewById<BottomNavigationView>(R.id.button_nav)
            navView.selectedItemId = R.id.navigation_artist
        }
        Thread.sleep(400)
    }

    @Test
    fun createArtistFormShowsErrorsWhenFieldsEmpty() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToArtistTab()

        Thread.sleep(300)

        onView(withId(R.id.fab_add_artist)).perform(click())
        Thread.sleep(300)
        onView(withId(R.id.btnCrear)).perform(scrollTo(), click())

        onView(withId(R.id.nombreTextView)).check(matches(hasTextInputLayoutErrorText("El nombre es obligatorio")))
        onView(withId(R.id.descriptionTextField)).check(matches(hasTextInputLayoutErrorText("La descripción es obligatoria")))
        onView(withId(R.id.birthDateTextView)).check(matches(hasTextInputLayoutErrorText("La fecha de nacimiento es obligatoria")))
        onView(withId(R.id.urlImageTextView)).check(matches(hasTextInputLayoutErrorText("Ingrese una URL válida")))
    }

    @Test
    fun createArtistAddsArtistToListAndShowsImagePreview() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToArtistTab()

        Thread.sleep(300)
        onView(withId(R.id.fab_add_artist)).perform(click())
        Thread.sleep(300)

        val artistName = "Espresso Test Artist"
        val description = "Descripción de prueba artista"
        val birthDate = "01/01/1990"
        val imageUrl = "https://example.com/newartist.jpg"

        onView(withId(R.id.nombreEditText)).perform(scrollTo(), replaceText(artistName), closeSoftKeyboard())
        onView(withId(R.id.descriptionEditText)).perform(scrollTo(), replaceText(description), closeSoftKeyboard())
        onView(withId(R.id.birthDateInputEditText)).perform(scrollTo(), replaceText(birthDate), closeSoftKeyboard())
        onView(withId(R.id.urlImageEditText)).perform(scrollTo(), replaceText(imageUrl), closeSoftKeyboard())

        Thread.sleep(800)
        onView(withId(R.id.imagePreviewImageView)).check(matches(isDisplayed()))

        onView(withId(R.id.btnCrear)).perform(scrollTo(), click())
        Thread.sleep(1000)

        onView(withText(artistName)).check(matches(isDisplayed()))
    }

    private fun hasTextInputLayoutErrorText(expectedErrorText: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("TextInputLayout should have error text: $expectedErrorText")
            }
            override fun matchesSafely(view: View): Boolean {
                if (view !is TextInputLayout) return false
                val error = view.error ?: return false
                return error.toString() == expectedErrorText
            }
        }
    }
}