package com.parthtejani.texturepacker;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

/**
 * Rasterizes ASCII printable characters (32-126) into separate bitmaps.
 * Primarily for use with texture packer to create atlas textures of font
 * data/bitmaps for OpenGL use
 */
public class FontRasterizer {

    /*
     * I'm not exactly sure how I want to refactor this out, so
     * I will leave it hard coded for now (not sure of what other fonts/things
     * I might want to do, Bold/Italics/etc)
     */
    private static final String FONT_NAME = "Arial";
    private static final int[] FONT_SIZES = {22, 24, 28, 30, 36};

    private final String bitmapsDestDirPath;
    private final RenderingHints renderHints;

    /**
     * Initializes rendering hints and destination path for font character bitmaps
     * 
     * @param bitmapsDestDirPath Destination directory that will contain future
     * subfolders, each of which will represent a font (arial_22, etc)
     * and inside the subfolder will be bitmaps for each character
     * (032.png, 033.png, etc)
     */
    public FontRasterizer(String bitmapsDestDirPath) {
        this.bitmapsDestDirPath = bitmapsDestDirPath;
        renderHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        renderHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        renderHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        renderHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        renderHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Input argument must be a single destination directory");
        }
        new FontRasterizer(args[0]).run();
    }

    /**
     * Iterates over each font (currently only one hard coded font) and each size
     * to create bitmaps of each character
     */
    public void run() {
        File destDir = new File(bitmapsDestDirPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        for (int i = 0; i < FONT_SIZES.length; i++) {
            int size = FONT_SIZES[i];
            String fontDirName = FONT_NAME.toLowerCase() + "_" + String.valueOf(size);
            Font font = new Font(FONT_NAME, Font.PLAIN, size);

            long t1 = System.currentTimeMillis();
            createGlyphs(font, bitmapsDestDirPath + "/" + fontDirName, size);
            long t2 = System.currentTimeMillis();
            System.out.println("Time to process " + fontDirName + " : " + (t2-t1) + " ms");

        }
    }

    private void createGlyphs(Font font, String destDirPath, int size) {
        //obtain a font metrics object, has to be done through a graphics object
        FontMetrics fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).
                getGraphics().getFontMetrics(font);

        //height is constant for all bitmaps for certain font/size
        int height = fontMetrics.getAscent() + fontMetrics.getDescent();

        //base line position represents some arbitrary value for a font where
        //characters are aligned ("j", "g", etc will extend below the base line.
        //used for drawString method, since it draws character based on the base
        //line value I think
        int baseLinePosition = height - fontMetrics.getDescent();

        File destDir = new File(destDirPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        for (int i = 32; i < 127; i++) {
            Character character = (char) i;
            BufferedImage image = new BufferedImage(fontMetrics.charWidth(i), height,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHints(renderHints);
            graphics.setFont(font);
            graphics.setColor(Color.WHITE);
            graphics.drawString(character.toString(), 0, baseLinePosition);
            graphics.dispose();

            //append 0 to two digit numbers, so can sort lexigraphically
            String extraZero;
            if (i < 100) {
                extraZero = "0";
            } else {
                extraZero = "";
            }

            File outputBitmap = new File(destDirPath + "/" + extraZero + i + ".png");

            try {
                ImageIO.write(image, "png", outputBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
