package com.example.locationforefroundfinal.model;

public class StatListItem {
    private String img;
    private String label;
    private String description;

    public StatListItem(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
