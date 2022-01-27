package com.hifnawy.subtitle.jumiastaskphonenumberviewer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.adapters.CustomerItemAdapter;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.model.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

  SwipeRefreshLayout swipeRefreshLayout;
  CustomerItemAdapter customerItemAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RecyclerView customersRecyclerView = findViewById(R.id.customersRecyclerView);

    swipeRefreshLayout = findViewById(R.id.swipeRefreshSwipeLayout);

    customerItemAdapter = new CustomerItemAdapter(getApplicationContext());

    customersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    customersRecyclerView.setAdapter(customerItemAdapter);

    refreshItems();

    swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN);
    swipeRefreshLayout.setOnRefreshListener(() -> refreshItems());
  }

  private void refreshItems() {
    customerItemAdapter.clear();

    swipeRefreshLayout.setRefreshing(true);

    // Instantiate the RequestQueue.
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "http://192.168.1.120:8080/getNumbers";

    // Request a string response from the provided URL.
    StringRequest stringRequest =
        new StringRequest(
            Request.Method.GET,
            url,
            response -> {
              try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                  JSONObject jsonObj = jsonArray.getJSONObject(i);

                  Gson gson = new Gson();

                  Customer customer = gson.fromJson(jsonObj.toString(), Customer.class);

                  // Log.d("REST_API", jsonObj.toString());
                  // Log.d("REST_API", customer.getId() + "");

                  customerItemAdapter.addCustomer(customer);
                  customerItemAdapter.refreshAndAnimate();

                  swipeRefreshLayout.setRefreshing(false);
                }
              } catch (JSONException e) {
                e.printStackTrace();
                Log.e("REST_API", e.toString());
              }
            },
            error -> {
              Log.e("REST_API", "Error: " + error.toString());
              swipeRefreshLayout.setRefreshing(false);
            });

    // Add the request to the RequestQueue.
    queue.add(stringRequest);
  }
}
