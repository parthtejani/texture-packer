package com.parthtejani.texturepacker;

import java.util.*;

/**
 * Sorts bitmaps alphebetically (ascending order)
 */
public class BitmapNameComparator implements Comparator<Bitmap> {

    @Override
    public int compare(Bitmap b1, Bitmap b2) {
        return b1.name.compareTo(b2.name);
    }

}
