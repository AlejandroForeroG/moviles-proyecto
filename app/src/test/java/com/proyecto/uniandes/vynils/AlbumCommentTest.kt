package com.proyecto.uniandes.vynils

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
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
class AlbumCommentTest {

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
        runBlocking { repository.clearUser() }
        if (::scenario.isInitialized) {
            scenario.close()
        }
        if (::database.isInitialized && database.isOpen) {
            database.close()
        }
    }

    private fun navigateToAlbumDetail() {
        Thread.sleep(500)
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_albums)
            recyclerView.scrollToPosition(0)
            recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }
        Thread.sleep(500)
    }

    private fun waitForCommentInRecycler(
        scenario: ActivityScenario<MainActivity>,
        text: String,
        timeoutMs: Long = 2000L
    ) {
        val start = System.currentTimeMillis()
        var found = false
        while (!found && System.currentTimeMillis() - start < timeoutMs) {
            scenario.onActivity { activity ->
                val rv = activity.findViewById<RecyclerView>(R.id.rv_comments) ?: return@onActivity
                val adapterCount = rv.adapter?.itemCount ?: 0
                for (i in 0 until adapterCount) {
                    val vh = rv.findViewHolderForAdapterPosition(i)
                    val itemView = vh?.itemView
                    if (itemView != null) {
                        val out = ArrayList<View>()
                        itemView.findViewsWithText(out, text, View.FIND_VIEWS_WITH_TEXT)
                        if (out.isNotEmpty()) {
                            found = true
                            break
                        }
                    }
                }
            }
            if (!found) Thread.sleep(50)
        }
        if (!found) org.junit.Assert.fail("Timed out waiting for comment \"$text\" in RecyclerView")
    }


    @Test
    fun commentFormIsVisibleInAlbumDetail() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        scenario.onActivity { activity ->
            val et = activity.findViewById<EditText>(R.id.et_comment)
            val rb = activity.findViewById<RatingBar>(R.id.rb_rating)
            val btn = activity.findViewById<Button>(R.id.btn_submit_comment)
            assertNotNull(et)
            assertNotNull(rb)
            assertNotNull(btn)
            assertTrue(et.isShown)
            assertTrue(rb.isShown)
            assertTrue(btn.isShown)
        }
    }

    @Test
    fun commentsRecyclerViewIsDisplayed() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        scenario.onActivity { activity ->
            val rv = activity.findViewById<RecyclerView>(R.id.rv_comments)
            assertNotNull(rv)
            assertTrue(rv.isShown)
        }
    }

    @Test
    fun addCommentSuccessfullyAddsCommentToList() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        val commentText = "Este es un comentario de prueba"

        scenario.onActivity { activity ->
            val ratingBar = activity.findViewById<RatingBar>(R.id.rb_rating)
            ratingBar.rating = 4f

            val et = activity.findViewById<EditText>(R.id.et_comment)
            et.setText(commentText)

            val btn = activity.findViewById<Button>(R.id.btn_submit_comment)
            btn.performClick()
        }

        waitForCommentInRecycler(scenario, commentText)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_comments)
            assertNotNull(recyclerView)
            assertTrue((recyclerView.adapter?.itemCount ?: 0) >= 1)
        }
    }

    @Test
    fun commentWithRatingIsDisplayedCorrectly() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        val commentText = "Comentario con calificación"

        scenario.onActivity { activity ->
            val ratingBar = activity.findViewById<RatingBar>(R.id.rb_rating)
            ratingBar.rating = 5f

            val et = activity.findViewById<EditText>(R.id.et_comment)
            et.setText(commentText)

            val btn = activity.findViewById<Button>(R.id.btn_submit_comment)
            btn.performClick()
        }

        waitForCommentInRecycler(scenario, commentText)
    }

    @Test
    fun commentFormClearsAfterSuccessfulSubmission() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        val commentText = "Este comentario se debe limpiar"

        scenario.onActivity { activity ->
            val ratingBar = activity.findViewById<RatingBar>(R.id.rb_rating)
            ratingBar.rating = 3f

            val et = activity.findViewById<EditText>(R.id.et_comment)
            et.setText(commentText)

            val btn = activity.findViewById<Button>(R.id.btn_submit_comment)
            btn.performClick()
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val et = activity.findViewById<EditText>(R.id.et_comment)
            assertEquals("", et.text.toString())

            val ratingBar = activity.findViewById<RatingBar>(R.id.rb_rating)
            assertEquals(0f, ratingBar.rating, 0.1f)
        }
    }

    @Test
    fun multipleCommentsCanBeAdded() {
        runBlocking { repository.saveUser("COLECCIONISTA") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        // Obtener el conteo inicial de items (puede haber comentarios previos)
        var initialCount = 0
        scenario.onActivity { activity ->
            initialCount = activity.findViewById<RecyclerView>(R.id.rv_comments)?.adapter?.itemCount ?: 0
        }

        val comments = listOf(
            "Primer comentario",
            "Segundo comentario",
            "Tercer comentario"
        )

        comments.forEachIndexed { index, comment ->
            scenario.onActivity { activity ->
                val ratingBar = activity.findViewById<RatingBar>(R.id.rb_rating)
                ratingBar.rating = (index + 1).toFloat()

                val et = activity.findViewById<EditText>(R.id.et_comment)
                et.setText(comment)

                val btn = activity.findViewById<Button>(R.id.btn_submit_comment)
                btn.performClick()
            }

            waitForCommentInRecycler(scenario, comment)
        }

        var adapterCount = 0
        val start = System.currentTimeMillis()
        val timeoutMs = 2000L
        while (System.currentTimeMillis() - start < timeoutMs) {
            scenario.onActivity { activity ->
                adapterCount = activity.findViewById<RecyclerView>(R.id.rv_comments)?.adapter?.itemCount ?: 0
            }
            if (adapterCount >= initialCount + comments.size) break
            Thread.sleep(50)
        }

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_comments)
            assertNotNull(recyclerView)
            assertEquals(initialCount + comments.size, adapterCount)
        }
    }

    @Test
    fun commentCanBeAddedAsUsuario() {
        runBlocking { repository.saveUser("USUARIO") }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        navigateToAlbumDetail()

        val commentText = "Comentario de usuario"

        scenario.onActivity { activity ->
            val ratingBar = activity.findViewById<RatingBar>(R.id.rb_rating)
            ratingBar.rating = 4f

            val et = activity.findViewById<EditText>(R.id.et_comment)
            et.setText(commentText)

            val btn = activity.findViewById<Button>(R.id.btn_submit_comment)
            btn.performClick()
        }

        waitForCommentInRecycler(scenario, commentText)

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_comments)
            assertNotNull(recyclerView)
            assertTrue((recyclerView.adapter?.itemCount ?: 0) >= 1)
        }
    }
}