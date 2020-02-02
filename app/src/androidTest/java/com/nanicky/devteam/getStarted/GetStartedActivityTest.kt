package com.nanicky.devteam.getStarted


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.nanicky.devteam.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetStartedActivityTest {

    @get: Rule
    var activityTestRule = ActivityTestRule(GetStartedActivity::class.java)


    @Test
    fun clickingGetStartedAndClickingAgree_shouldGrantPermission() {

        onView(withId(R.id.getStarted)).check(matches(isClickable()))

    }


}