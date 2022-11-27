package com.example.restaurantapp.model;

public class Restaurant {
    private int id;
    private String name;
    private String description;
    private String coverUrl;

    public Restaurant(int id, String name, String description, String coverUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coverUrl = coverUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
