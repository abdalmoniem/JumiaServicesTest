package com.hifnawy.subtitle.jumiastaskphonenumberviewer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.adapters.CustomerItemAdapter;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.interfaces.DataRequestCallback;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.model.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * This is the main UI Class of the application which handles everything from sending REST requests
 * to updating the UI with the response data
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 22nd 2022
 */
public class MainActivity extends AppCompatActivity {

  /** gson object to parse and convert JSON string to Java Objects */
  private final Gson gson = new Gson();

  /** server port of the backend */
  private final String serverPort = "8080";

  /** server ip of the backend */
  private String serverIP;

  /** baseURL of the REST API request */
  private String baseURL;

  /** reference to SwipeRefreshLayout from XML */
  private SwipeRefreshLayout swipeRefreshLayout;

  /** reference to RecyclerView from XML */
  private RecyclerView customersRecyclerView;

  /** reference to CustomerItemAdapter from XML */
  private CustomerItemAdapter customerItemAdapter;

  /** reference to FloatingActionButton from XML */
  private FloatingActionButton scrollUpFAB;

  /** reference to ImageView from XML */
  private ImageView errorIconImageView;

  /** reference to ProgressBar from XML */
  private ProgressBar progressBar;

  /** volley request queue */
  private RequestQueue requestQueue;

  /** used to store user selected filter */
  private Filter selectedFilter = Filter.ALL;

  /** boolean variable to check if we need to load new pages from the database */
  private boolean needToLoadMore = false;

  /** pages left for a particular filter, reported from the backend */
  private int pagesLeft = Integer.MAX_VALUE;

  /** current page index being displayed */
  private int pageIndex = 1;

  /**
   * called when this activity is being created, used to fetch UI elements' references and map them
   * as well as initialize these UI elements
   *
   * @param savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestQueue = Volley.newRequestQueue(getApplicationContext());

    initializeLayout();
  }

  /** initialize the main UI of this activity */
  private void initializeLayout() {
    // initialize server ip and send initial REST request
    showIPInputDialog();

    customersRecyclerView = findViewById(R.id.customersRecyclerView);

    errorIconImageView = findViewById(R.id.errorIconImageView);

    progressBar = findViewById(R.id.loadingProgressBar);

    swipeRefreshLayout = findViewById(R.id.swipeRefreshSwipeLayout);

    customerItemAdapter = new CustomerItemAdapter(getApplicationContext());

    customersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    customersRecyclerView.setAdapter(customerItemAdapter);

    scrollUpFAB = findViewById(R.id.scrollUpFAB);

    scrollUpFAB.setOnClickListener(v -> customersRecyclerView.smoothScrollToPosition(0));

    swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN);
    swipeRefreshLayout.setOnRefreshListener(
        () ->
            requestMoreCustomers(
                1,
                selectedFilter,
                new DataRequestCallback() {
                  @Override
                  public void dataPreLoaded() {
                    // reset page index and clear items adapter before reloading/refreshing
                    customerItemAdapter.clear();
                    pageIndex = 1;
                  }

                  @Override
                  public void dataLoaded() {
                    customerItemAdapter.refreshAndAnimate();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                  }

                  @Override
                  public void dataLoadError() {
                    errorIconImageView.setVisibility(View.VISIBLE);
                    customersRecyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                  }
                }));

    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
    customersRecyclerView.setLayoutManager(mLinearLayoutManager);

    customersRecyclerView.addOnScrollListener(
        new RecyclerView.OnScrollListener() {

          @Override
          public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            // total items visible in the current window of the RecyclerView
            int totalItems = mLinearLayoutManager.getItemCount();

            // first visible item in the current window of the RecyclerView
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            // last visible item in the current window of the RecyclerView
            int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

            // show/hide the "Scroll Up FAB" based on the current scroll location of the
            // RecyclerView
            // 10 and 3 are used arbitrarily
            if (lastVisibleItem >= 10) {
              scrollUpFAB.setVisibility(View.VISIBLE);
            } else if (firstVisibleItem <= 3) {
              scrollUpFAB.setVisibility(View.GONE);
            } else {
              // do nothing
            }

            // load one more page if there are still pages left to load
            if (pagesLeft > 0) {
              // load before scrolling to the end, before the last 5 items of the current page are
              // visible on the screen (5 is used arbitrarily)
              if (!needToLoadMore && (lastVisibleItem >= totalItems - 5)) {
                needToLoadMore = true;
                // Scrolled to the bottom. load one more page
                requestMoreCustomers(
                    pageIndex,
                    selectedFilter,
                    new DataRequestCallback() {
                      @Override
                      public void dataPreLoaded() {}

                      @Override
                      public void dataLoaded() {
                        customersRecyclerView.setVisibility(View.VISIBLE);
                        errorIconImageView.setVisibility(View.GONE);
                        needToLoadMore = false;
                        pageIndex++;
                      }

                      @Override
                      public void dataLoadError() {
                        errorIconImageView.setVisibility(View.VISIBLE);
                        customersRecyclerView.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                      }
                    });
              } else {
                // do nothing
              }
            } else {
              // do nothing
            }
          }
        });
  }

  /**
   * called whenever a menu is being created for an activity, used to inflate and initialize the
   * menu layout
   *
   * @param menu
   * @return
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_options, menu);
    return true;
  }

  /**
   * callback method triggered whenever a user selects an item from the menu item, used to process
   * different UI interactions based on the selected menu item
   *
   * @param item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.filter) {
      final Dialog dialog = new Dialog(this);
      Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations =
          R.style.Animation_Design_BottomSheetDialog;

      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setCancelable(true);
      dialog.setCanceledOnTouchOutside(true);
      dialog.setContentView(R.layout.filter_dialog);

      RadioButton allRadioButton = dialog.findViewById(R.id.allRadioButton);
      RadioButton validStateRadioButton = dialog.findViewById(R.id.validStateRadioButton);
      RadioButton invalidStateRadioButton = dialog.findViewById(R.id.invalidStateRadioButton);
      RadioButton cameroonCountryRadioButton = dialog.findViewById(R.id.cameroonCountryRadioButton);
      RadioButton ethiopiaCountryRadioButton = dialog.findViewById(R.id.ethiopiaCountryRadioButton);
      RadioButton moroccoCountryRadioButton = dialog.findViewById(R.id.moroccoCountryRadioButton);
      RadioButton mozambiqueCountryRadioButton =
          dialog.findViewById(R.id.mozambiqueCountryRadioButton);
      RadioButton ugandaCountryRadioButton = dialog.findViewById(R.id.ugandaCountryRadioButton);

      ArrayList<RadioButton> radioButtons =
          new ArrayList<>(
              Arrays.asList(
                  allRadioButton,
                  validStateRadioButton,
                  invalidStateRadioButton,
                  cameroonCountryRadioButton,
                  ethiopiaCountryRadioButton,
                  moroccoCountryRadioButton,
                  mozambiqueCountryRadioButton,
                  ugandaCountryRadioButton));

      Button filterButton = dialog.findViewById(R.id.filterButton);

      // select correct radio button based on the current selected filter

      for (int i = 0; i < Filter.values().length; i++) {
        Filter filter = Filter.values()[i];
        if (filter == selectedFilter) {
          radioButtons.get(i).setChecked(true);
        } else {
          // do nothing
        }
      }

      // send a REST request based on the selected filter
      filterButton.setOnClickListener(
          view -> {
            for (int i = 0; i < radioButtons.size(); i++) {
              if (radioButtons.get(i).isChecked()) {
                requestMoreCustomers(
                    1,
                    Filter.values()[i],
                    new DataRequestCallback() {
                      @Override
                      public void dataPreLoaded() {
                        customerItemAdapter.clear();
                        pageIndex = 1;
                      }

                      @Override
                      public void dataLoaded() {
                        customersRecyclerView.setVisibility(View.VISIBLE);
                        errorIconImageView.setVisibility(View.GONE);
                        customerItemAdapter.refreshAndAnimate();
                        swipeRefreshLayout.setRefreshing(false);
                        pageIndex++;
                      }

                      @Override
                      public void dataLoadError() {
                        errorIconImageView.setVisibility(View.VISIBLE);
                        customersRecyclerView.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                      }
                    });

                selectedFilter = Filter.values()[i];
              } else {
                // do nothing
              }
            }

            dialog.dismiss();
          });

      dialog.show();
    } else if (item.getItemId() == R.id.ipAddress) {
      // configure ip address
      showIPInputDialog();
    } else {
      // do nothing
    }
    return true;
  }

  /**
   * sends a REST API request to the backend to fetch a paginated list of customer entries from the
   * database with a filter applied on them
   *
   * @param page - the page number to fetch from the database
   * @param filter - a {@link Filter} used for filtering the entries in the database
   * @param dataRequestCallback - callback interface implementation used to handle UI/UX before,
   *     after and if an error occurs in the REST API request
   */
  private void requestMoreCustomers(
      int page, Filter filter, DataRequestCallback dataRequestCallback) {
    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            String.format(
                Locale.US,
                "%s/%d",
                filter == Filter.VALID
                    ? baseURL + getResources().getString(R.string.validURL)
                    : filter == Filter.INVALID
                        ? baseURL + getResources().getString(R.string.invalidURL)
                        : filter == Filter.CAMEROON
                            ? baseURL + getResources().getString(R.string.cameroonURL)
                            : filter == Filter.ETHIOPIA
                                ? baseURL + getResources().getString(R.string.ethiopiaURL)
                                : filter == Filter.MOROCCO
                                    ? baseURL + getResources().getString(R.string.moroccoURL)
                                    : filter == Filter.MOZAMBIQUE
                                        ? baseURL + getResources().getString(R.string.mozambiqueURL)
                                        : filter == Filter.UGANDA
                                            ? baseURL + getResources().getString(R.string.ugandaURL)
                                            : baseURL
                                                + getResources().getString(R.string.allNumbersURL),
                page),
            response -> {
              if (dataRequestCallback != null) {
                dataRequestCallback.dataPreLoaded();
              } else {
                // do nothing
              }

              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.addAll(requestResponse.getData());
              if (dataRequestCallback != null) {
                dataRequestCallback.dataLoaded();
              } else {
                // do nothing
              }

              pagesLeft = requestResponse.getPagesLeft();
            },
            error -> {
              Log.e("REST_API", "Error: " + error.toString());
              error.printStackTrace();

              Toast.makeText(
                      getApplicationContext(),
                      "Could not connect with server, please try again later.",
                      Toast.LENGTH_SHORT)
                  .show();

              if (dataRequestCallback != null) {
                dataRequestCallback.dataLoadError();
              } else {
                // do nothing
              }
            });

    // Add the request to the RequestQueue.
    requestQueue.add(stringRequest);
  }

  /** show a non-dismissible pop-up dialog for the user to input in the server's IP address */
  private void showIPInputDialog() {
    final Dialog dialog = new Dialog(this);
    Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations =
        R.style.Animation_Design_BottomSheetDialog;

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);
    dialog.setContentView(R.layout.ip_address_dialog);

    EditText ipAddressEditText = dialog.findViewById(R.id.ipAddressEditText);
    Button connectButton = dialog.findViewById(R.id.connectButton);

    connectButton.setOnClickListener(
        v -> {
          serverIP = ipAddressEditText.getText().toString();

          baseURL = String.format("http://%s:%s", serverIP, serverPort);

          Log.d("REST", ipAddressEditText.getText().toString());

          // requestAllCustomers();

          requestMoreCustomers(
              pageIndex,
              selectedFilter,
              new DataRequestCallback() {
                @Override
                public void dataPreLoaded() {}

                @Override
                public void dataLoaded() {
                  customersRecyclerView.setVisibility(View.VISIBLE);
                  errorIconImageView.setVisibility(View.GONE);

                  customerItemAdapter.refreshAndAnimate();
                  swipeRefreshLayout.setRefreshing(false);
                  progressBar.setVisibility(View.GONE);
                  pageIndex++;
                }

                @Override
                public void dataLoadError() {
                  errorIconImageView.setVisibility(View.VISIBLE);
                  customersRecyclerView.setVisibility(View.GONE);
                  swipeRefreshLayout.setRefreshing(false);
                  progressBar.setVisibility(View.GONE);
                }
              });
          dialog.dismiss();
        });

    dialog.setCancelable(false);
    dialog.show();
  }

  /**
   * Filter values <br>
   * {@link #ALL} for all valid and invalid phone numbers from all countries <br>
   * {@link #VALID} filter for all valid phone numbers from all countries <br>
   * {@link #INVALID} filter for all invalid phone numbers from all countries <br>
   * {@link #CAMEROON} filter for phone numbers from Cameroon <br>
   * {@link #ETHIOPIA} filter for phone numbers from Ethiopia <br>
   * {@link #MOROCCO} filter for phone numbers from Morocco <br>
   * {@link #MOZAMBIQUE} filter for phone numbers from Mozambique <br>
   * {@link #UGANDA} filter for phone numbers from Uganda
   */
  private enum Filter {
    /** filter for all valid and invalid phone numbers from all countries */
    ALL,

    /** filter for all valid phone numbers from all countries */
    VALID,

    /** filter for all invalid phone numbers from all countries */
    INVALID,

    /** filter for phone numbers from Cameroon */
    CAMEROON,

    /** filter for phone numbers from Ethiopia */
    ETHIOPIA,

    /** filter for phone numbers from Morocco */
    MOROCCO,

    /** filter for phone numbers from Mozambique */
    MOZAMBIQUE,

    /** filter for phone numbers from Uganda */
    UGANDA
  }
}
