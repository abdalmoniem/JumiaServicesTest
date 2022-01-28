package com.hifnawy.subtitle.jumiastaskphonenumberviewer.adapters;

import android.content.Intent;

import com.hifnawy.subtitle.jumiastaskphonenumberviewer.MainActivity;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.R;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.model.Customer;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@RunWith(AndroidJUnit4.class)
public class CustomerItemAdapterTest extends TestCase {

  private RecyclerView customersRecyclerView;
  private CustomerItemAdapter customerItemAdapter;
  private final Intent intent =
      new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

  @Rule
  public ActivityScenarioRule<MainActivity> activityScenarioRule =
      new ActivityScenarioRule<>(intent);

  @Before
  public void setUp() throws InterruptedException {
    onView(withId(R.id.ipAddressEditText))
        .check(matches(isDisplayed()))
        .perform(clearText(), typeText("192.168.1.120"));
    onView(withId(R.id.connectButton)).check(matches(isDisplayed())).perform(click());

    Thread.sleep(10);
  }

  @Test
  public void testGetItemCount() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;
              assertThat(
                  customerItemAdapter.getItemCount(),
                  both(greaterThanOrEqualTo(10)).and(lessThanOrEqualTo(20)));
            });

    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
  }

  @Test
  public void testAdd() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;
              customerItemAdapter.clear();

              for (int i = 0; i < 23; i++) {
                customerItemAdapter.add(new Customer());
              }

              assertEquals(23, customerItemAdapter.getItemCount());
            });

    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
  }

  @Test
  public void testAddAll() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;
              customerItemAdapter.clear();

              ArrayList<Customer> customers = new ArrayList<>();

              for (int i = 0; i < 23; i++) {
                customers.add(new Customer());
              }

              customerItemAdapter.addAll(customers);

              assertEquals(23, customerItemAdapter.getItemCount());

              customerItemAdapter.addAll(customers);

              assertEquals(46, customerItemAdapter.getItemCount());
            });

    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
  }

  @Test
  public void testSetItems() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;
              customerItemAdapter.clear();

              ArrayList<Customer> customers = new ArrayList<>();

              for (int i = 0; i < 23; i++) {
                customers.add(new Customer());
              }

              customerItemAdapter.setItems(customers);

              assertEquals(23, customerItemAdapter.getItemCount());

              customerItemAdapter.setItems(customers);

              assertEquals(23, customerItemAdapter.getItemCount());
            });

    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
  }

  @Test
  public void testClear() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;
              customerItemAdapter.clear();
              assertEquals(customerItemAdapter.getItemCount(), 0);
              assertEquals(customerItemAdapter.getItems().size(), 0);
            });

    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
  }

  @Test
  public void testGetItems() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;
              assertThat(
                  customerItemAdapter.getItems().size(),
                  both(greaterThanOrEqualTo(10)).and(lessThanOrEqualTo(20)));
            });

    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
  }
}
