package com.parthtejani.texturepacker;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;


public class TexturePacker {

    private final String bitmapsSourceDirPath;
    private final String atlasesDestDirPath;
    private final FileFilter visibleFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return !file.isHidden();
        }
    };

    /**
     * Main Constructor
     * 
     * @param bitmapsSourceDirPath A directory that contains subdirectories that
     * contain bitmaps. The contents of each subdirectory will be produced into an
     * atlas texture
     * @param atlasesDestDirPath The output directory for storing the atlas textures
     * and text files containing coordinate information
     */
    public TexturePacker(String bitmapsSourceDirPath, String atlasesDestDirPath) {
        this.bitmapsSourceDirPath = bitmapsSourceDirPath;
        this.atlasesDestDirPath = atlasesDestDirPath;
    }

    /*
     * All exceptions are bubbled up since there's no point in catching/handling
     * them (all are file I/O exceptions)
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Input arguments must be a source"
                    + " and destination directory");
        }
        new TexturePacker(args[0], args[1]).run();
    }

    /**
     * Iterates over subdirectories containing bitmaps and produces atlas textures
     * 
     * @throws Exception File I/O exception
     */
    public void run() throws Exception {
        File destDir = new File(atlasesDestDirPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File sourceDir = new File(bitmapsSourceDirPath);
        File[] subDirs = sourceDir.listFiles(visibleFilter);
        for (File dir : subDirs) {
            long t1 = System.currentTimeMillis();
            process(dir);
            long t2 = System.currentTimeMillis();
            System.out.println("Time to process " + dir.getName() + " : " + (t2-t1) + " ms");
        }
    }

    private void process(File atlasTextureSubDir) throws Exception {
        String atlasName = atlasTextureSubDir.getName();

        //get a handle to input bitmap files
        File[] bitmapFiles = atlasTextureSubDir.listFiles(visibleFilter);
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        for (File bitmapFile : bitmapFiles) {
            bitmaps.add(new Bitmap(bitmapFile));
        }

        //sort them by overall area descending
        Collections.sort(bitmaps, new BitmapAreaComparator());

        //position and render the bitmaps
        int[] dimensions = estimateDimensions(bitmaps);
        positionBitmaps(dimensions, bitmaps);
        renderAtlasTexture(atlasName, dimensions, bitmaps);

        //get a handle to a file for writing coordinates
        File texFile = new File(atlasesDestDirPath + "/" + atlasName + "-texture-data.txt");
        if (texFile.exists()) {
            texFile.delete();
        }
        texFile.createNewFile();

        //write the coordinate data
        BufferedWriter writer = new BufferedWriter(new FileWriter(texFile));
        writer.write(bitmaps.size() + " " + dimensions[0] + " " + dimensions[1]);
        Collections.sort(bitmaps, new BitmapNameComparator());
        for (Bitmap bitmap : bitmaps) {
            writer.write("\n" + bitmap.toDataString());
        }
        writer.close();
    }

    /*
     * Gives an estimate size for atlas texture dimensions based
     * on the total area (pixels) the bitmaps occupy
     */
    private int[] estimateDimensions(List<Bitmap> bitmaps) {
        int[] dimensions = new int[2];

        double sumArea = 0;
        for (Bitmap bitmap : bitmaps) {
            sumArea += bitmap.area;
        }

        //estimated width is >= estimated height
        int width = nextPowerOf2(Math.sqrt(sumArea));
        int height = nextPowerOf2(sumArea/width);

        dimensions[0] = width;
        dimensions[1] = height;

        return dimensions;
    }

    /*
     * Obtains the positions for the bitmaps on the atlas texture.
     * Final position coordinates for each bitmap will be included in a
     * Rect object that is a field member of Bitmap class.
     */
    private void positionBitmaps(int[] dimensions, List<Bitmap> bitmaps) {
        //loop will continue until all bitmaps fit and at least one iteration has passed
        boolean allBitmapsFit = true;
        int count = 0;
        while (count == 0 || !allBitmapsFit) {
            allBitmapsFit = true;

            /*
             * Create bitmap tree and insert bitmaps.
             * If bitmap does not fit (result of insert function), then break,
             * resize atlas dimensions, and try again
             */
            BitmapTree bitmapTree = new BitmapTree(dimensions);
            for (Bitmap bitmap : bitmaps) {
                allBitmapsFit &= bitmapTree.insert(bitmap);
                if (!allBitmapsFit) {
                    break;
                }
            }

            if (!allBitmapsFit) {
                /*
                 * If width and height are same, increase width
                 * Else increase height since height <= width
                 */
                if (dimensions[0] == dimensions[1]) {
                    dimensions[0] *= 2;
                } else {
                    dimensions[1] *= 2;
                }
            }

            count++;
        }
    }

    private void renderAtlasTexture(String atlasName, int[] dimensions, List<Bitmap> bitmaps)
            throws Exception {
        File outputFile = new File(atlasesDestDirPath + "/" + atlasName + ".png");
        BufferedImage atlasImage = new BufferedImage(dimensions[0], dimensions[1],
                BufferedImage.TYPE_INT_ARGB);

        for (Bitmap bitmap : bitmaps) {
            Rectangle rect = bitmap.getPositionRect();
            //bulk copy the pixels
            int[] pixels = bitmap.image.getRGB(0, 0, bitmap.width, bitmap.height,
                    null, 0, bitmap.width);
            atlasImage.setRGB(rect.left, atlasImage.getHeight()-rect.top,
                    bitmap.width, bitmap.height, pixels, 0, bitmap.width);
        }

        ImageIO.write(atlasImage, "png", outputFile);
    }

    private int nextPowerOf2(double value) {
        int base = 1;
        while (base < value) {
            base *= 2;
        }
        return base;
    }

}
