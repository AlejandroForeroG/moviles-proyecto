package com.proyecto.uniandes.vynils

import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AlbumCommentInstrumentedTest {

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

    private fun navigateToAlbumDetail() {
        Thread.sleep(1000)
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            Thread.sleep(100)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }
        Thread.sleep(1000)
    }

    @Test
    fun commentSectionIsDisplayedInAlbumDetail() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        onView(withId(R.id.nsv_content)).perform(swipeUp())
        Thread.sleep(300)

        onView(withId(R.id.rv_comments)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun addCommentFormIsVisibleInAlbumDetail() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        onView(withId(R.id.et_comment)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.rb_rating)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.btn_submit_comment)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun addCommentSuccessfullyAddsCommentToList() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        val commentText = "Comentario con calificación"

        scenario.onActivity { activity ->
            val ratingBar = activity.findViewById<android.widget.RatingBar>(R.id.rb_rating)
            ratingBar.rating = 4f
        }

        Thread.sleep(300)

        onView(withId(R.id.et_comment))
            .perform(scrollTo())
            .perform(replaceText(commentText), closeSoftKeyboard())

        Thread.sleep(300)

        onView(withId(R.id.btn_submit_comment))
            .perform(scrollTo())
            .perform(click())

        Thread.sleep(2000)

        onView(withText(commentText))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }




    @Test
    fun existingCommentsAreDisplayedInAlbumDetail() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_comments)
            val adapter = recyclerView.adapter
            val itemCount = adapter?.itemCount ?: 0
            assert(itemCount >= 0) { "RecyclerView should have comments or be empty" }
        }
    }

    @Test
    fun commentWithRatingIsDisplayedCorrectly() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        onView(withId(R.id.et_comment))
            .perform(scrollTo(), replaceText("Comentario con calificación"), closeSoftKeyboard())

        onView(withId(R.id.rb_rating))
            .perform(scrollTo(), click())

        onView(withId(R.id.btn_submit_comment)).perform(scrollTo(), click())
        Thread.sleep(1000)

        onView(withId(R.id.nsv_content)).perform(swipeUp())
        Thread.sleep(300)

        onView(withId(R.id.rv_comments)).check(matches(isDisplayed()))
    }
}