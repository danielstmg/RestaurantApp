package com.example.restaurantapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.restaurantapp.BuildConfig;
import com.example.restaurantapp.R;
import com.example.restaurantapp.adapter.ReviewListAdapter;
import com.example.restaurantapp.model.Restaurant;
import com.example.restaurantapp.model.Review;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.io.DefaultHttpRequestWriter;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static String KEY_RESTAURANT_ID = "key_restaurant";
    private int restaurantId;

    private ImageView ivRestaurantCover;
    private TextView tvRestaurantName, tvRestaurantDescription;
    private RecyclerView rvReviews;
    private EditText etReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivRestaurantCover = findViewById(R.id.iv_restaurant_image);
        tvRestaurantName = findViewById(R.id.tv_restaurant_name);
        tvRestaurantDescription = findViewById(R.id.tv_restaurant_description);
        etReview = findViewById(R.id.et_restaurant_review);

        rvReviews = findViewById(R.id.rv_restaurant_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
        rvReviews.setHasFixedSize(true);

        MaterialButton btnSendReview = findViewById(R.id.btn_send_review);
        btnSendReview.setOnClickListener(this);

        int restaurantId = getIntent().getIntExtra(KEY_RESTAURANT_ID, 0);
        this.restaurantId = restaurantId;

        getRestaurantDetail(restaurantId);
    }

    private void parseRestaurantView(Restaurant restaurant) {
        tvRestaurantName.setText(restaurant.getName());
        tvRestaurantDescription.setText(restaurant.getDescription());

        String imgUrl = BuildConfig.API_BASE_URL + "api/v1/restaurants/cover/" + restaurant.getCoverUrl();
        Glide.with(DetailActivity.this)
                .load(imgUrl)
                .into(ivRestaurantCover);
    }

    private void getRestaurantDetail(int restaurantId) {
        // TODO 6: Lengkapi fungsi getRestaurantDetail
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BuildConfig.API_BASE_URL + "api/v1/restaurants/" + restaurantId;

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonData = jsonResponse.getJSONObject("data");
                    JSONArray arrReviews = jsonData.getJSONArray("reviews");

                    int id= jsonData.getInt("id");
                    String name = jsonData.getString("name");
                    String description = jsonData.getString("about");
                    String coverUrl = jsonData.getString("cover_url");

                    ArrayList<Review> reviews = new ArrayList<>();

                    for (int i = 0; i < arrReviews.length(); i++) {
                        JSONObject jsonReview = arrReviews.getJSONObject(i);

                        int reviewId = jsonReview.getInt("id");
                        String writer = jsonReview.getString("writer");
                        String content = jsonReview.getString("content");
                        String createdAt = jsonReview.getString("created_at");

                        Review review = new Review(reviewId, writer, content, createdAt);
                        reviews.add(review);
                    }

                    Restaurant restaurant = new Restaurant(id, name, description, coverUrl);
                    parseRestaurantView(restaurant);
                    parseRestaurantReviewView(reviews);

                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String message = String.format(Locale.getDefault(), "Error with code: %d", statusCode);
                Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseRestaurantReviewView(ArrayList<Review> reviews) {
        ReviewListAdapter adapter = new ReviewListAdapter(reviews);
        rvReviews.setAdapter(adapter);
    }

    private void postRestaurantReview(int restaurantId) {
        // TODO 7: Lengkapi fungsi postRestaurantReview
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String reviewerName = "Daniel";
        String reviewerContent = etReview.getText().toString().trim();

        params.put("writer", reviewerName);
        params.put("content", reviewerContent);

        String url = BuildConfig.API_BASE_URL + "api/v1/restaurants/" + restaurantId + "/reviews";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray arrReviews = jsonResponse.getJSONArray("data");

                    ArrayList<Review> reviews = new ArrayList<>();

                    for (int i = 0; i < arrReviews.length(); i++) {
                        JSONObject jsonReview = arrReviews.getJSONObject(i);

                        int id = jsonReview.getInt("id");
                        String writer = jsonReview.getString("writer");
                        String content = jsonReview.getString("content");
                        String createdAt = jsonReview.getString("created_at");

                        Review review = new Review(id, writer, content, createdAt);
                        reviews.add(review);
                    }

                    parseRestaurantReviewView(reviews);
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String message = String.format(Locale.getDefault(), "Error with code: %d", statusCode);
                Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send_review) {
            postRestaurantReview(restaurantId);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Toast.makeText(DetailActivity.this, "Writing review...", Toast.LENGTH_SHORT).show();
        }
    }
}