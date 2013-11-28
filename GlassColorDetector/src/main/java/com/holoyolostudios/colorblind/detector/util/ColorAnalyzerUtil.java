package com.holoyolostudios.colorblind.detector.util;

import android.graphics.Color;

/**
 * ColorAnalyzerUtil
 *
 * @author Martin Brabham
 * @since 11/26/13
 */
public class ColorAnalyzerUtil {

    // Instance
    private static ColorAnalyzerUtil sInstance = null;

    /**
     * Constructor
     */
    private ColorAnalyzerUtil() {

    }

    /**
     * Get static instance
     *
     * @return {@link com.holoyolostudios.colorblind.detector.util.ColorAnalyzerUtil}
     */
    public static ColorAnalyzerUtil getInstance() {
        if (sInstance == null) {
            sInstance = new ColorAnalyzerUtil();
        }
        return sInstance;
    }

    public RGBColor analyze(int r, int g, int b) {
        RGBColor color = new RGBColor(r, g, b);
        return color;
    }

    public void analyze(byte[] yuv, int w, int h) {
        int[] argb = ColorAnalyzerUtil.convertYUV420_NV21toRGB8888(yuv, w, h);


        int index = argb.length / 4;
        index = (int) ((float) (index / 2) + .5f);
        index *= 4;

        int pixel = 0xFF000000;
        pixel |= argb[index];
        pixel |= argb[index + 1];
        pixel |= argb[w + index];
        pixel |= argb[w + index + 1];

        int alpha = Color.alpha(pixel);
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);

    }

    /**
     * A color representation
     */
    public class RGBColor {

        // Members
        private int mAlpha = 0xFF000000;
        private int mRed = 0x00000000;
        private int mGreen = 0x00000000;
        private int mBlue = 0x00000000;
        private String mColorName = null;
        private String mHexCode = null;

        /**
         * Constructor
         *
         * @param r {@link java.lang.Integer}
         * @param g {@link java.lang.Integer}
         * @param b {@link java.lang.Integer}
         */
        public RGBColor(int r, int g, int b) {
            this(0xFF, r, g, b);
        }

        /**
         * Constructor
         *
         * @param a {@link java.lang.Integer}
         * @param r {@link java.lang.Integer}
         * @param g {@link java.lang.Integer}
         * @param b {@link java.lang.Integer}
         */
        public RGBColor(int a, int r, int g, int b) {
            mAlpha = a;
            mRed = r;
            mGreen = g;
            mBlue = b;
        }

        public int getPixel() {
            return mAlpha | mRed | mGreen | mBlue;
        }

        public int getAlpha() {
            return mAlpha;
        }

        public int getRed() {
            return mRed;
        }

        public int getGreen() {
            return mGreen;
        }

        public int getBlue() {
            return mBlue;
        }

        public String getName() {
            return mColorName;
        }

        public String getHexCode() {
            return mHexCode;
        }

    }

    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param data   byte array on YUV420 NV21 format.
     * @param width  pixels width
     * @param height pixels height
     * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    public static int[] convertYUV420_NV21toRGB8888(byte[] data, int width, int height) {
        int size = width * height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for (int i = 0, k = 0; i < size; i += 2, k += 2) {
            y1 = data[i] & 0xff;
            y2 = data[i + 1] & 0xff;
            y3 = data[width + i] & 0xff;
            y4 = data[width + i + 1] & 0xff;

            u = data[offset + k] & 0xff;
            v = data[offset + k + 1] & 0xff;
            u = u - 128;
            v = v - 128;

            pixels[i] = convertYUVtoRGB(y1, u, v);
            pixels[i + 1] = convertYUVtoRGB(y2, u, v);
            pixels[width + i] = convertYUVtoRGB(y3, u, v);
            pixels[width + i + 1] = convertYUVtoRGB(y4, u, v);

            if (i != 0 && (i + 2) % width == 0)
                i += width;
        }

        return pixels;
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r, g, b;

        r = y + (int) 1.402f * v;
        g = y - (int) (0.344f * u + 0.714f * v);
        b = y + (int) 1.772f * u;
        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;
        return 0xff000000 | (b << 16) | (g << 8) | r;
    }

}