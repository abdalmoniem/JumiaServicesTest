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

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

  private final Gson gson = new Gson();
  private final String serverPort = "8080";
  private String serverIP = "";
  private String baseURL;

  private SwipeRefreshLayout swipeRefreshLayout;
  private RecyclerView customersRecyclerView;
  private CustomerItemAdapter customerItemAdapter;
  private FloatingActionButton scrollUpFAB;
  private ImageView errorIconImageView;
  private ProgressBar progressBar;

  private RequestQueue requestQueue;

  private Filter selectedFilter = Filter.ALL;

  private boolean mLoading = false;

  private int pagesLeft = Integer.MAX_VALUE;
  private int pageIndex = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestQueue = Volley.newRequestQueue(getApplicationContext());

    initializeLayout();
  }

  private void initializeLayout() {
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

            int totalItems = mLinearLayoutManager.getItemCount();
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

            if (lastVisibleItem >= 10) {
              scrollUpFAB.setVisibility(View.VISIBLE);
            } else if (firstVisibleItem <= 3) {
              scrollUpFAB.setVisibility(View.GONE);
            } else {
              // do nothing
            }

            if (pagesLeft > 0) {
              if (!mLoading && (lastVisibleItem >= totalItems - 5)) {
                mLoading = true;
                // Scrolled to bottom. Do something here.
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
                        mLoading = false;
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

    @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_options, menu);
    return true;
  }

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

      Button filterButton = dialog.findViewById(R.id.filterButton);

      switch (selectedFilter) {
        case ALL:
          allRadioButton.setChecked(true);
          break;
        case VALID:
          validStateRadioButton.setChecked(true);
          break;
        case INVALID:
          invalidStateRadioButton.setChecked(true);
          break;
        case CAMEROON:
          cameroonCountryRadioButton.setChecked(true);
          break;
        case ETHIOPIA:
          ethiopiaCountryRadioButton.setChecked(true);
          break;
        case MOROCCO:
          moroccoCountryRadioButton.setChecked(true);
          break;
        case MOZAMBIQUE:
          mozambiqueCountryRadioButton.setChecked(true);
          break;
        case UGANDA:
          ugandaCountryRadioButton.setChecked(true);
          break;
        default:
          break;
      }

      filterButton.setOnClickListener(
          view -> {
            if (allRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
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

              selectedFilter = Filter.ALL;
            } else if (validStateRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.VALID,
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

              selectedFilter = Filter.VALID;
            } else if (invalidStateRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.INVALID,
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

              selectedFilter = Filter.INVALID;
            } else if (cameroonCountryRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.CAMEROON,
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

              selectedFilter = Filter.CAMEROON;
            } else if (ethiopiaCountryRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.ETHIOPIA,
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

              selectedFilter = Filter.ETHIOPIA;
            } else if (moroccoCountryRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.MOROCCO,
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

              selectedFilter = Filter.MOROCCO;
            } else if (mozambiqueCountryRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.MOZAMBIQUE,
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

              selectedFilter = Filter.MOZAMBIQUE;
            } else if (ugandaCountryRadioButton.isChecked()) {
              requestMoreCustomers(
                  1,
                  Filter.UGANDA,
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

              selectedFilter = Filter.UGANDA;
            } else {
              // do nothing
            }

            dialog.dismiss();
          });

      dialog.show();
    } else if (item.getItemId() == R.id.ipAddress){
      showIPInputDialog();
    } else {
        // do nothing
    }
    return true;
  }

  private void requestMoreCustomers(int page, DataRequestCallback dataRequestCallback) {
    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            String.format(
                Locale.US,
                "%s/%d",
                baseURL + getResources().getString(R.string.allNumbersURL),
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

  private void requestAllCustomers() {
    customerItemAdapter.clear();

    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            baseURL + getResources().getString(R.string.allNumbersURL),
            response -> {
              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.setDataSet(requestResponse.getData());
              customerItemAdapter.refreshAndAnimate();
              swipeRefreshLayout.setRefreshing(false);
            },
            error -> {
              Log.e("REST_API", "Error: " + error.toString());
              error.printStackTrace();
              Toast.makeText(
                      getApplicationContext(),
                      "Could not connect with server, please try again later.",
                      Toast.LENGTH_SHORT)
                  .show();
              swipeRefreshLayout.setRefreshing(false);
            });

    // Add the request to the RequestQueue.
    requestQueue.add(stringRequest);
  }

  private void requestCustomersByState(Filter filter) {

    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            filter == Filter.VALID
                ? baseURL + getResources().getString(R.string.validURL)
                : baseURL + getResources().getString(R.string.invalidURL),
            response -> {
              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.setDataSet(requestResponse.getData());
              customerItemAdapter.refreshAndAnimate();
              swipeRefreshLayout.setRefreshing(false);
            },
            error -> {
              Log.e("REST_API", "Error: " + error.toString());
              error.printStackTrace();
              Toast.makeText(
                      getApplicationContext(),
                      "Could not connect with server, please try again later.",
                      Toast.LENGTH_SHORT)
                  .show();
              swipeRefreshLayout.setRefreshing(false);
            });

    // Add the request to the RequestQueue.
    requestQueue.add(stringRequest);
  }

  private void requestCustomersByCountry(Filter filter) {
    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            filter == Filter.CAMEROON
                ? baseURL + getResources().getString(R.string.cameroonURL)
                : filter == Filter.ETHIOPIA
                    ? baseURL + getResources().getString(R.string.ethiopiaURL)
                    : filter == Filter.MOROCCO
                        ? baseURL + getResources().getString(R.string.moroccoURL)
                        : filter == Filter.MOZAMBIQUE
                            ? baseURL + getResources().getString(R.string.mozambiqueURL)
                            : filter == Filter.UGANDA
                                ? baseURL + getResources().getString(R.string.ugandaURL)
                                : baseURL + getResources().getString(R.string.allNumbersURL),
            response -> {
              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.setDataSet(requestResponse.getData());
              customerItemAdapter.refreshAndAnimate();
              swipeRefreshLayout.setRefreshing(false);
            },
            error -> {
              Log.e("REST_API", "Error: " + error.toString());
              error.printStackTrace();
              Toast.makeText(
                      getApplicationContext(),
                      "Could not connect with server, please try again later.",
                      Toast.LENGTH_SHORT)
                  .show();
              swipeRefreshLayout.setRefreshing(false);
            });

    // Add the request to the RequestQueue.
    requestQueue.add(stringRequest);
  }

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
