package com.example.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.BuildConfig;
import com.example.restaurantapp.R;
import com.example.restaurantapp.adapter.RestaurantListAdapter;
import com.example.restaurantapp.model.Restaurant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView rvRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRestaurants = findViewById(R.id.rv_restaurants);
        rvRestaurants.setHasFixedSize(true);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        getRestaurantData();
    }

    private void getRestaurantData() {
        // TODO 5: Lengkapi fungsi getRestaurantData()
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BuildConfig.API_BASE_URL + "api/v1/restaurants";

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                Log.d(TAG, response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray arrRestaurants = jsonResponse.getJSONArray("data");
                    ArrayList<Restaurant> restaurants = new ArrayList<>();

                    for (int i = 0; i < arrRestaurants.length(); i++) {
                        JSONObject jsonRestaurant = arrRestaurants.getJSONObject(i);

                        int id = jsonRestaurant.getInt("id");
                        String name = jsonRestaurant.getString("name");
                        String description = jsonRestaurant.getString("about");
                        String coverUrl = jsonRestaurant.getString("cover_url");

                        Restaurant restaurant = new Restaurant(id, name, description, coverUrl);
                        restaurants.add(restaurant);
                    }
                    parseRestaurantsView(restaurants);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String message = String.format(Locale.getDefault(), "Error with code: %d", statusCode);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToDetailActivity(Restaurant restaurant) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_RESTAURANT_ID, restaurant.getId());
        startActivity(intent);
    }

    private void parseRestaurantsView(ArrayList<Restaurant> restaurants) {
        RestaurantListAdapter adapter = new RestaurantListAdapter(restaurants);
        adapter.setOnItemClickCallback(new RestaurantListAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Restaurant restaurant) {
                moveToDetailActivity(restaurant);
            }
        });

        rvRestaurants.setAdapter(adapter);
    }
}