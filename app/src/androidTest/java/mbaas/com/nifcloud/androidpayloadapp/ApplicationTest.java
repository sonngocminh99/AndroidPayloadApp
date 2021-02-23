package mbaas.com.nifcloud.androidpayloadapp;

import android.content.Context;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ApplicationTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class, true);

    @Before
    public void init() {
        // Specify a valid string.
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Assert.assertEquals("mbaas.com.nifcloud.androidpayloadapp", appContext.getPackageName());
    }

    @Test
    public void validateDisplayScreen() {
        onView(withText("AndroidPayloadPush")).check(matches(isDisplayed()));
        onView(withText("com.nifcloud.mbaas.pushId")).check(matches(isDisplayed()));
        onView(withText("com.nifcloud.mbaas.richUrl")).check(matches(isDisplayed()));
        onView(withText("data (JSON)")).check(matches(isDisplayed()));
        onView(withId(R.id.txtPushid)).check(matches(withText("")));
        onView(withId(R.id.txtRichurl)).check(matches(withText("")));
        onData(allOf(is(instanceOf(String.class)), is(""))).inAdapterView(withId(R.id.lsJson));
    }
}