package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SHUVOJIT GAMING", appName)
  }

  @Test
  fun `verify game registry has 100+ games`() {
    val games = com.example.data.GameRegistry.games
    org.junit.Assert.assertTrue("Should have 100+ games", games.size >= 100)
  }
}
