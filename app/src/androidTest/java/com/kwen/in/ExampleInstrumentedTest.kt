package com.kwen.`in`

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4



import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**  // TODO: performance
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test

    fun useAppContext() {
        // checked: util
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.kwen.in", appContext.packageName)
    }
}