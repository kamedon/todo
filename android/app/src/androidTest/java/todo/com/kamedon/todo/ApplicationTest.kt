package todo.com.kamedon.todo

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ApplicationTest {
    @Before
    fun setup() {
        // something setup
    }

    @After
    fun teardown() {
        // something teardown
    }

    @Test
    fun packageName() {
        Assert.assertEquals(InstrumentationRegistry.getTargetContext().getPackageName(), "todo.com.kamedon.todo")
    }
}