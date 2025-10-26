package com.proyecto.uniandes.vynils

import androidx.test.core.app.ActivityScenario
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import com.proyecto.uniandes.vynils.ui.user.UserActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class UserTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: UserRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking { repository.clearUser() }
    }

    @After
    fun tearDown() {
        runBlocking { repository.clearUser() }
    }

    @Test
    fun showsUserSelectionButtons() {
        ActivityScenario.launch(UserActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val btnUsuario = activity.findViewById<android.view.View>(R.id.btnUsuario)
                val btnColeccionista =
                    activity.findViewById<android.view.View>(R.id.btnColeccionista)

                assert(btnUsuario.visibility == android.view.View.VISIBLE)
                assert(btnColeccionista.visibility == android.view.View.VISIBLE)
            }
        }
    }

    @Test
    fun clickingUsuarioNavigatesToMain() {
        ActivityScenario.launch(UserActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val btnUsuario = activity.findViewById<android.view.View>(R.id.btnUsuario)
                btnUsuario.performClick()
            }

            Thread.sleep(100)

            scenario.onActivity { activity ->
                val shadowActivity = shadowOf(activity)
                val nextIntent = shadowActivity.peekNextStartedActivity()

                assert(nextIntent != null)
                assert(nextIntent.component?.className == MainActivity::class.java.name)
            }
        }
    }

}