package com.superdrycleaners.drycleaners.beans;

public class SubCategoryModel {
    private String Name;
    private String id;
   String image;

    public SubCategoryModel(String category_id, String category_name, String image) {
        this.id=category_id;
        this.Name=category_name;
        this.image=image;

    }

    public String getItemName() {
        return Name;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}


