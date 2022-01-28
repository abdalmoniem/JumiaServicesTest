package com.hifnawy.subtitle.jumiastaskphonenumberviewer;

import android.content.Intent;

import com.hifnawy.subtitle.jumiastaskphonenumberviewer.adapters.CustomerItemAdapter;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.model.Customer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

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
  public void initializeLayout() throws InterruptedException {
    onView(withId(R.id.swipeRefreshSwipeLayout)).check(matches(isDisplayed()));
    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));
    onView(withId(R.id.scrollUpFAB)).check(matches(not(isDisplayed())));

    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);

              try {
                for (int i = 0; i < 3; i++) {
                  customersRecyclerView.smoothScrollToPosition(
                      Objects.requireNonNull(customersRecyclerView.getAdapter()).getItemCount()
                          - 1);
                  Thread.sleep(10);
                }
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
  }

  @Test
  public void requestMoreCustomers() {
    onView(withId(R.id.swipeRefreshSwipeLayout)).check(matches(isDisplayed()));
    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));

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
  }

  public void requestMoreCustomersWithFilters() {
    onView(withId(R.id.swipeRefreshSwipeLayout)).check(matches(isDisplayed()));
    onView(withId(R.id.customersRecyclerView)).check(matches(isDisplayed()));

    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              customersRecyclerView = activity.findViewById(R.id.customersRecyclerView);
              customerItemAdapter = (CustomerItemAdapter) customersRecyclerView.getAdapter();

              assert customerItemAdapter != null;

              customerItemAdapter.clear();

              try {
                for (MainActivity.Filter filter : MainActivity.Filter.values()) {
                  activity.requestMoreCustomers(1, filter, null);

                  Thread.sleep(100);

                  Pattern pattern;

                  for (Customer customer : customerItemAdapter.getItems()) {
                    switch (filter) {
                      case VALID:
                      case INVALID:
                        pattern =
                            Pattern.compile(
                                "\\((237\\)\\s?[2368]\\d{7,8}$)|(\\(251\\)\\s?[1-59]\\d{8}$)|(\\(212\\)\\s?[5-9]\\d{8}$)"
                                    + "|(\\(258\\)\\s?[28]\\d{7,8}$)|(\\(256\\)\\s?\\d{9}$)");
                        break;
                      case CAMEROON:
                        pattern = Pattern.compile("\\(237\\)\\s?[2368]\\d{7,8}$");
                        break;
                      case ETHIOPIA:
                        pattern = Pattern.compile("\\(251\\)\\s?[1-59]\\d{8}$");
                        break;
                      case MOROCCO:
                        pattern = Pattern.compile("\\(212\\)\\s?[5-9]\\d{8}$");
                        break;
                      case MOZAMBIQUE:
                        pattern = Pattern.compile("\\(258\\)\\s?[28]\\d{7,8}$");
                        break;
                      case UGANDA:
                        pattern = Pattern.compile("\\(256\\)\\s?\\d{9}$");
                        break;
                      default:
                        pattern = null;
                        break;
                    }

                    Matcher matcher = pattern.matcher(customer.getPhone());

                    if (filter == MainActivity.Filter.INVALID) {
                      assertFalse(matcher.matches());
                    } else {
                      assertTrue(matcher.matches());
                    }
                  }
                }
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
  }

  @Test
  public void showIPInputDialog() throws InterruptedException {
    activityScenarioRule.getScenario().onActivity(MainActivity::showIPInputDialog);
    Thread.sleep(10);
    onView(withId(R.id.ipAddressEditText)).check(matches(isDisplayed()));
    onView(withId(R.id.connectButton)).check(matches(isDisplayed()));
  }
}
