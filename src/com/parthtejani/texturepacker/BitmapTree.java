package com.parthtejani.texturepacker;

/**
 * Data structure for organizing which positions the bitmaps will be placed
 * in the final atlas texture
 * 
 * <p>This class is an implementation of the algorithm/data structure located here:
 * http://www.blackpawn.com/texts/lightmaps/
 * 
 * <p>The algorithm essentially tries to find a valid rectangle/position in the tree
 * that is large enough to hold the bitmap. It then splits that rectangle into
 * 3 other rectangles, of which one contains the bitmap.
 */
public class BitmapTree {

    private final BitmapNode root;

    /**
     * Initialize root node
     * 
     * @param dimensions    int[] of size 2 specifying the initial dimensions
     * (width/height) for the starting node/rectangle
     */
    public BitmapTree(int[] dimensions) {
        root = new BitmapNode(new Rectangle(0, dimensions[0], 0, dimensions[1]));
    }

    /**
     * Insert a bitmap
     * 
     * @param bitmap The bitmap to insert
     * @return boolean True if a position was found for the bitmap
     */
    public boolean insert(Bitmap bitmap) {
        return root.insert(bitmap);
    }

    private static class BitmapNode {

        BitmapNode left;
        BitmapNode right;
        Rectangle rect;
        boolean hasBitmap = false;

        BitmapNode(Rectangle rect) {
            this.rect = rect;
        }

        boolean insert(Bitmap bitmap) {
            //if not a leaf node, then search left/right child for a fit
            if (left != null && right != null) {

                boolean fitted = left.insert(bitmap);
                if (!fitted) {
                    fitted = right.insert(bitmap);
                }
                return fitted;
            } else {
                //if this leaf node has a bitmap, then exit
                if (hasBitmap) {
                    return false;
                }

                //if cannot fit in this node's rect, then exit
                if (!fits(rect, bitmap)) {
                    return false;
                }

                //if exact fit, then set the position for the bitmap equal
                //to this node's rect
                if (fitsExact(rect, bitmap)) {
                    hasBitmap = true;
                    bitmap.setPositionRect(rect);
                    return true;
                }

                //create children for node, and split corresponding rects
                //based on left over distance (there are two possible combinations
                //of rects, want to pick combination that has maximum single
                //largest rect, so if possible combos are size 10/20, and 1/29, pick 1/29)
                int deltaWidth = rect.width - bitmap.width;
                int deltaHeight = rect.height - bitmap.height;

                if (deltaWidth > deltaHeight) {
                    left = new BitmapNode(new Rectangle(rect.left, rect.left + bitmap.width, rect.bottom, rect.top));
                    right = new BitmapNode(new Rectangle(rect.left + bitmap.width, rect.right, rect.bottom, rect.top));
                } else {
                    left = new BitmapNode(new Rectangle(rect.left, rect.right, rect.top-bitmap.height, rect.top));
                    right = new BitmapNode(new Rectangle(rect.left, rect.right, rect.bottom, rect.top-bitmap.height));
                }


                return left.insert(bitmap);
            }
        }

        boolean fits(Rectangle rect, Bitmap bitmap) {
            return bitmap.width <= rect.width && bitmap.height <= rect.height;
        }

        boolean fitsExact(Rectangle rect, Bitmap bitmap) {
            return bitmap.width == rect.width && bitmap.height == rect.height;
        }
    }

}
