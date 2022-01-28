package com.hifnawy.subtitle.jumiastaskphonenumberviewer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.adapters.CustomerItemAdapter;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.model.Response;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

  private final String serverIP = "192.168.1.120";
  private final String serverPort = "8080";
  private final String baseURL = String.format("http://%s:%s", serverIP, serverPort);

  private RequestQueue requestQueue;
  private SwipeRefreshLayout swipeRefreshLayout;
  private CustomerItemAdapter customerItemAdapter;

  private Filter selectedFilter = Filter.ALL;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestQueue = Volley.newRequestQueue(getApplicationContext());

    RecyclerView customersRecyclerView = findViewById(R.id.customersRecyclerView);

    swipeRefreshLayout = findViewById(R.id.swipeRefreshSwipeLayout);

    customerItemAdapter = new CustomerItemAdapter(getApplicationContext());

    customersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    customersRecyclerView.setAdapter(customerItemAdapter);

    requestAllCustomers();

    swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN);
    swipeRefreshLayout.setOnRefreshListener(() -> requestAllCustomers());
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
              requestAllCustomers();

              selectedFilter = Filter.ALL;
            } else if (validStateRadioButton.isChecked()) {
              requestCustomersByState(Filter.VALID);

              selectedFilter = Filter.VALID;
            } else if (invalidStateRadioButton.isChecked()) {
              requestCustomersByState(Filter.INVALID);

              selectedFilter = Filter.INVALID;
            } else if (cameroonCountryRadioButton.isChecked()) {
              requestCustomersByCountry(Filter.CAMEROON);

              selectedFilter = Filter.CAMEROON;
            } else if (ethiopiaCountryRadioButton.isChecked()) {
              requestCustomersByCountry(Filter.ETHIOPIA);

              selectedFilter = Filter.ETHIOPIA;
            } else if (moroccoCountryRadioButton.isChecked()) {
              requestCustomersByCountry(Filter.MOROCCO);

              selectedFilter = Filter.MOROCCO;
            } else if (mozambiqueCountryRadioButton.isChecked()) {
              requestCustomersByCountry(Filter.MOZAMBIQUE);

              selectedFilter = Filter.MOZAMBIQUE;
            } else if (ugandaCountryRadioButton.isChecked()) {
              requestCustomersByCountry(Filter.UGANDA);

              selectedFilter = Filter.UGANDA;
            } else {
              // do nothing
            }

            dialog.dismiss();
          });

      dialog.show();
    } else {
      //  do nothing
    }
    return true;
  }

  private void requestAllCustomers() {
    customerItemAdapter.clear();

    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            baseURL + getResources().getString(R.string.allNumbersURL),
            response -> {
              Gson gson = new Gson();

              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.setDataSet(requestResponse.getCustomers());
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
              Gson gson = new Gson();

              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.setDataSet(requestResponse.getCustomers());
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
              Gson gson = new Gson();

              Response requestResponse = gson.fromJson(response, Response.class);

              customerItemAdapter.setDataSet(requestResponse.getCustomers());
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
