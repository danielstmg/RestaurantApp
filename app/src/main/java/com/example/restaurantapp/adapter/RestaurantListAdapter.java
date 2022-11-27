package com.example.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.restaurantapp.BuildConfig;
import com.example.restaurantapp.R;
import com.example.restaurantapp.model.Restaurant;

import java.util.ArrayList;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private final ArrayList<Restaurant> restaurants;
    private OnItemClickCallback onItemClickCallback;

    public RestaurantListAdapter(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public RestaurantListAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantListAdapter.RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        String imgUrl = BuildConfig.API_BASE_URL + "api/v1/restaurants/cover/" + restaurant.getCoverUrl();

        Glide.with(holder.itemView.getContext())
                .load(imgUrl)
                .into(holder.ivRestaurantImage);

        holder.tvRestaurantName.setText(restaurant.getName());
        holder.tvRestaurantDescription.setText(restaurant.getDescription());

        holder.itemView.setOnClickListener(view -> {
            onItemClickCallback.onItemClicked(restaurant);
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRestaurantImage;
        TextView tvRestaurantName, tvRestaurantDescription;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRestaurantImage = itemView.findViewById(R.id.item_iv_restaurant_image);
            tvRestaurantName = itemView.findViewById(R.id.item_tv_restaurant_name);
            tvRestaurantDescription = itemView.findViewById(R.id.item_tv_restaurant_description);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(Restaurant restaurant);
    }
}
