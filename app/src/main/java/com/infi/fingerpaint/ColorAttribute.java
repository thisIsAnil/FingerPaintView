package com.infi.fingerpaint;

public class ColorAttribute {
    private int color;
    private float colorLocation;
    private float brushWeight;

    public ColorAttribute(int color, float colorLocation, float brushWeight) {
        this.color = color;
        this.colorLocation = colorLocation;
        this.brushWeight = brushWeight;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getColorLocation() {
        return colorLocation;
    }

    public void setColorLocation(float colorLocation) {
        this.colorLocation = colorLocation;
    }

    public float getBrushWeight() {
        return brushWeight;
    }

    public void setBrushWeight(float brushWeight) {
        this.brushWeight = brushWeight;
    }
}
