package com.example.alan.ratemyplate;

public class Plate {
    private int id;
    private String name;
    private String caption;
    private byte[] image;

    public Plate(String name, String caption, byte[] image, int id){
        this.name = name;
        this.caption = caption;
        this.image = image;
        this.id = id;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
