package com.proyecto.uniandes.vynils

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import com.proyecto.uniandes.vynils.ui.user.UserActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class UserInstrumentedTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var repository: UserRepository
    private lateinit var scenario: ActivityScenario<UserActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()
        runBlocking { repository.clearUser() }
        scenario = ActivityScenario.launch(UserActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        scenario.close()
        runBlocking { repository.clearUser() }
    }

    @Test
    fun showsUserSelectionButtons() {
        onView(withId(R.id.btnUsuario)).check(matches(isDisplayed()))
        onView(withId(R.id.btnColeccionista)).check(matches(isDisplayed()))
    }

    @Test
    fun clickingUsuarioNavigatesToMain() {
        onView(withId(R.id.btnUsuario)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun clickingColeccionistaNavigatesToMain() {
        onView(withId(R.id.btnColeccionista)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }
}