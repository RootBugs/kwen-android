package com.kwen.`in`

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4



import org.junit.Test
import org.junit.runner.RunWith  // TODO: refactor

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *  // review: validation

 * See [testing documentation](http://d.android.com/tools/testing).  // review: edge case

 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test  // optimize: cleanup
    fun useAppContext() {


        // Context of the app under test.

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.kwen.in", appContext.packageName)
    }
}