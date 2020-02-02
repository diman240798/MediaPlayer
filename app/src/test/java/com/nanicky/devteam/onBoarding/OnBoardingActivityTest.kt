package com.nanicky.devteam.onBoarding

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.nanicky.devteam.common.App
import com.nanicky.devteam.getStarted.GetStartedActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf


@RunWith(RobolectricTestRunner::class)
@LargeTest
class OnBoardingActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(OnBoardingActivity::class.java)

    @Test
    fun clickingSkip_shouldShowGettingStartedActivity() {
        activityRule.activity.skipButton.performClick()
    }


    @Test
    fun clickingGotIt_shouldShowGettingStartedActivity() {
        activityRule.activity.skipButton.performClick()
    }

    @Test
    fun clickingNext_shouldShowNextItem() {
        val activity = activityRule.activity
        activity.next.performClick()
        val nextPage = activity.viewPager.currentItem + 1
        activity.viewPager.currentItem = nextPage
        val currentPage = activity.viewPager.currentItem
        assertEquals(nextPage, currentPage)
    }

    fun shouldShowGettingStartedActivity() {
        val expected = Intent(activityRule.activity, GetStartedActivity::class.java)
        val actual = shadowOf(ApplicationProvider.getApplicationContext<App>()).nextStartedActivity
        assertEquals(expected.component, actual.component)
    }

}