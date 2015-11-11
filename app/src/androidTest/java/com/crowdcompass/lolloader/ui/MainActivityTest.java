package com.crowdcompass.lolloader.ui;

import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.crowdcompass.lolloader.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity thisActivity;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		injectInstrumentation(InstrumentationRegistry.getInstrumentation());
		thisActivity = getActivity();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPreconditions() {
		assertNotNull("MainActivityTest is null", thisActivity);
	}

	public void testLayoutDisplay() {
		onView(withId(R.id.photo_image)).check(matches(isDisplayed()));
		onView(withId(R.id.photo_title)).check(matches(isDisplayed()));
	}

	public void testInitText() {
		onView(withId(R.id.photo_title)).check(matches(withText(R.string.photo_title)));
	}

	public void testLoadPhoto() {
		String title;

		onView(withId(R.id.photo_image)).perform(click());
		// get the actual title from FlickrPhoto object which is stored in RetainedFragment.
		title = thisActivity.getRetainedFragment().getPhoto().getTitle();
		onView(withId(R.id.photo_title)).check(matches(withText(title)));

		onView(withId(R.id.photo_image)).perform(click());
		title = thisActivity.getRetainedFragment().getPhoto().getTitle();
		onView(withId(R.id.photo_title)).check(matches(withText(title)));

		onView(withId(R.id.photo_image)).perform(click());
		title = thisActivity.getRetainedFragment().getPhoto().getTitle();
		onView(withId(R.id.photo_title)).check(matches(withText(title)));
	}

	public void testDeviceOrientationChange() {
		String title;

		onView(withId(R.id.photo_image)).perform(click());
		title = thisActivity.getRetainedFragment().getPhoto().getTitle();
		onView(withId(R.id.photo_title)).check(matches(withText(title)));

		thisActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		onView(withId(R.id.photo_title)).check(matches(withText(title)));
		pause(500);

		thisActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		onView(withId(R.id.photo_title)).check(matches(withText(title)));
		pause(500);

		onView(withId(R.id.photo_image)).perform(click());
		title = thisActivity.getRetainedFragment().getPhoto().getTitle();
		onView(withId(R.id.photo_title)).check(matches(withText(title)));
	}

	// only for human eye check...
	public static void pause(int ms){
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
