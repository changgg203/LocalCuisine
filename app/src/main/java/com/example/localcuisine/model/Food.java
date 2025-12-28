package com.example.localcuisine.model;

import java.util.List;
import java.util.Set;

public class Food {

    private final int id;
    private final String name;
    private final String description;
    private final Region region;
    private final List<String> tags;
    private final String bestTime; // "Sáng", "Trưa", "Tối"
    private final String location; // địa phương tiêu biểu
    private final String imageUrl;
    private Set<FoodType> types;


    public Food(int id,
                String name,
                String description,
                Region region,
                Set<FoodType> types,
                List<String> tags,
                String bestTime,
                String location,
                String imageUrl) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.region = region;
        this.types = types;
        this.tags = tags;
        this.bestTime = bestTime;
        this.location = location;
        this.imageUrl = imageUrl;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Region getRegion() {
        return region;
    }

    public Set<FoodType> getTypes() {
        return types;
    }


    public List<String> getTags() {
        return tags;
    }

    public String getBestTime() {
        return bestTime;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
