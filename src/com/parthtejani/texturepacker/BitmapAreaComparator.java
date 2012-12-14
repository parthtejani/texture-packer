package com.parthtejani.texturepacker;

import java.util.Comparator;

/**
 * Sorts bitmaps based on area (pixels) in descending order
 */
public class BitmapAreaComparator implements Comparator<Bitmap> {

    @Override
    public int compare(Bitmap b1, Bitmap b2) {
        return b2.area - b1.area;
    }

}
