package com.parthtejani.texturepacker;


public class Rectangle {

    public final int left;
    public final int right;
    public final int bottom;
    public final int top;
    public final int width;
    public final int height;

    public Rectangle(int left, int right, int bottom, int top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        width = right - left;
        height = top - bottom;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Left: " + left + "   ");
        builder.append("Right: " + right + "   ");
        builder.append("Bottom: " + bottom + "   ");
        builder.append("Top: " + top + "   ");
        builder.append("Width: " + width + "   ");
        builder.append("Height: " + height + "   ");
        return builder.toString();
    }

}
