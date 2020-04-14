package com.christianfrom.mandatoryassignment;

import android.content.Context;

import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<LogInActivity> activityRule = new ActivityTestRule<>(LogInActivity.class);

    @Test
    public void loginTestNoEmailNoPass() {
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.loginErrorText)).check(matches(withText("You need to type in an e-mail and password to login!")));
    }

    @Test
    public void loginTestWrongUser() throws InterruptedException {
        onView(withId(R.id.loginEmailEditText)).perform(typeText("nonexistinguser@test.dk"));
        onView(withId(R.id.loginPasswordEditText)).perform(typeText("test123"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(500); // Find en bedre måde end det her
        onView(withId(R.id.loginErrorText)).check(matches(withText("Authentication failed")));

    }


    @Test
    public void loginTest() {
        onView(withId(R.id.loginEmailEditText)).perform(typeText("test@test.dk"));
        onView(withId(R.id.loginPasswordEditText)).perform(typeText("test123"));
    }



}
