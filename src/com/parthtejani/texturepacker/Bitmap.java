package com.parthtejani.texturepacker;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;

public class Bitmap {

    public final String name;
    public final BufferedImage image;
    public final int width;
    public final int height;
    public final int area;
    public final int perimeter;

    //positionRect holds the coordinates for where the bitmap
    //should be placed onto an atlas texture
    private Rectangle positionRect;

    public Bitmap(File bitmapFile) throws Exception {
        //exclude file extension in name
        name = bitmapFile.getName().split("\\.")[0];
        image = ImageIO.read(bitmapFile);
        width = image.getWidth();
        height = image.getHeight();
        area = width*height;
        perimeter = width*2 + height*2;
    }

    public Rectangle getPositionRect() {
        return positionRect;
    }

    public void setPositionRect(Rectangle positionRect) {
        this.positionRect = positionRect;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: " + name + "   ");
        builder.append("Width: " + width + "   ");
        builder.append("Height: " + height + "   ");
        builder.append("Area: " + area + "   ");
        builder.append("Perimeter: " + perimeter + "   ");
        builder.append("Position Rect: " + positionRect);
        return builder.toString();
    }

    /*
     * Data string is written to texture coordinates file
     * only containing the name, and boundaries
     */
    public String toDataString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name + " ");
        builder.append(positionRect.left + " ");
        builder.append(positionRect.right + " ");
        builder.append(positionRect.bottom + " ");
        builder.append(positionRect.top + " ");
        return builder.toString();
    }

}
