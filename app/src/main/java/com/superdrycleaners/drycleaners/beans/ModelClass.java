package com.superdrycleaners.drycleaners.beans;

public class ModelClass {
    private String name;
    private int image;

    public ModelClass(){}
    public ModelClass(String name, int image)
    {
        this.image = image;
        this.name = name;
    }

    public ModelClass(int pagerImage){
        this.image = pagerImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}
