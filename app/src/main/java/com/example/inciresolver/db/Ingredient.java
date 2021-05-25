package com.example.inciresolver.db;

public class Ingredient {
    private String name = "";
    private String description = "";
    private String quality = "";
    //@android:drawable/btn_star

    public Ingredient(){
    }

    public Ingredient(String name, String description, String quality){
        this.name = name;
        this.description = description;
        this.quality = quality;

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

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public boolean isEmpty(){
        if(name.equals("") && description.equals("") && quality.equals("")){
            return true;
        }
        return false;
    }
}
