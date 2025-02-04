/*
 * Copyright 2016-2025 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.toolkit.fx.color;

import javafx.scene.paint.Color;

/**
 *
 * @author Pavel Castornii
 */
public final class ColorUtils {

    /**
     * Converts JavaFX color to #RRGGBBAA string.
     *
     * @param color
     * @return
     */
    public static String toHexWithAlpha(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        int alpha = (int) (color.getOpacity() * 255);
        return String.format("#%02X%02X%02X%02X", red, green, blue, alpha);
    }

    /**
     * Converts JavaFX color to #RRGGBB string.
     *
     * @param color
     * @return
     */
    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    /**
     * Converts JavaFX color to #RRGGBB string.
     *
     * @param color
     * @return
     */
    public static String toHex(int color) {
        return String.format("#%06X", color);
    }

    public static int toInt(Color color) {
        int rgb = ((int) (color.getRed() * 255) << 16)
                | ((int) (color.getGreen() * 255) << 8)
                | (int) (color.getBlue() * 255);
        return rgb;
    }

    /**
     * Returns the alpha value from an integer color value.
     * If the alpha channel is missing, returns 255.
     *
     * @param color color value in the format 0xRRGGBB or 0xRRGGBBAA
     * @return alpha value from 0 to 255
     */
    public static int getAlphaAsInt(int color) {
        if ((color >> 24) != 0) {
            // If the alpha channel is present
            return (color >> 24) & 0xFF;
        } else {
            // If the alpha channel is absent (format 0xRRGGBB), return 255
            return 255;
        }
    }

    /**
     * Returns the alpha value from an integer color value as a double.
     * If the alpha channel is missing, returns 1.0.
     *
     * @param color color value in the format 0xRRGGBB or 0xRRGGBBAA
     * @return alpha value from 0.0 to 1.0
     */
    public static double getAlphaAsDouble(int color) {
        if ((color >> 24) != 0) {
            // If the alpha channel is present, normalize the alpha value to [0.0, 1.0]
            return ((color >> 24) & 0xFF) / 255.0;
        } else {
            // If the alpha channel is absent (format 0xRRGGBB), return 1.0 (fully opaque)
            return 1.0;
        }
    }

    /**
     * Converts an integer color value in the format 0xRRGGBB or 0xRRGGBBAA to a JavaFX Color.
     *
     * @param colorValue color value in the format 0xRRGGBB or 0xRRGGBBAA
     * @return JavaFX Color
     */
    public static Color toColor(int colorValue) {
        double alpha;
        double red;
        double green;
        double blue;
        if ((colorValue >> 24) != 0) {
            // If the alpha channel is present
            alpha = ((colorValue >> 24) & 0xFF) / 255.0;
            red = ((colorValue >> 16) & 0xFF) / 255.0;
            green = ((colorValue >> 8) & 0xFF) / 255.0;
            blue = (colorValue & 0xFF) / 255.0;
        } else {
            // If the alpha channel is absent (format 0xRRGGBB)
            alpha = 1.0; // fully opaque
            red = ((colorValue >> 16) & 0xFF) / 255.0;
            green = ((colorValue >> 8) & 0xFF) / 255.0;
            blue = (colorValue & 0xFF) / 255.0;
        }
        return new Color(red, green, blue, alpha);
    }

    /**
     * Calculates intermediate color between two colors. For example, if one is red and another is blue then returned
     * color will be magenta.
     *
     * @param color1
     * @param color2
     * @return
     */
    public static int intermediate(int color1, int color2) {
        // Get HSL values for both colors
        float[] hsl1 = rgbToHsl(Color.rgb((color1 >> 16) & 0xFF, (color1 >> 8) & 0xFF, color1 & 0xFF));
        float[] hsl2 = rgbToHsl(Color.rgb((color2 >> 16) & 0xFF, (color2 >> 8) & 0xFF, color2 & 0xFF));
        // Calculate average values for the new color
        float hue = averageHue(hsl1[0], hsl2[0]);
        float saturation = (hsl1[1] + hsl2[1]) / 2.0f;
        float lightness = (hsl1[2] + hsl2[2]) / 2.0f;
        // Convert back to RGB and return
        return hslToRgb(hue, saturation, lightness);
    }

    /**
     * Converts RGB to HSL.
     *
     * @param color
     * @return
     */
    public static float[] rgbToHsl(Color color) {
        return rgbToHsl((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
    }

    /**
     * Converts RGB to HSL.
     *
     * @return
     */
    public static float[] rgbToHsl(float r, float g, float b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        float hue = 0;
        if (delta != 0) {
            if (max == r) {
                hue = ((g - b) / delta) % 6;
            } else if (max == g) {
                hue = ((b - r) / delta) + 2;
            } else {
                hue = ((r - g) / delta) + 4;
            }
        }
        hue *= 60;
        if (hue < 0) {
            hue += 360;
        }
        float lightness = (max + min) / 2;
        float saturation = 0;
        if (delta != 0) {
            saturation = delta / (1 - Math.abs(2 * lightness - 1));
        }
        return new float[]{hue, saturation, lightness};
    }

    /**
     * Converts HSL to RGB.
     *
     * @param h
     * @param s
     * @param l
     * @return
     */
    public static int hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
        float r = 0;
        float g = 0;
        float b = 0;
        if (0 <= h && h < 60) {
            r = c; g = x; b = 0;
        } else if (60 <= h && h < 120) {
            r = x; g = c; b = 0;
        } else if (120 <= h && h < 180) {
            r = 0; g = c; b = x;
        } else if (180 <= h && h < 240) {
            r = 0; g = x; b = c;
        } else if (240 <= h && h < 300) {
            r = x; g = 0; b = c;
        } else if (300 <= h && h < 360) {
            r = c; g = 0; b = x;
        }
        int red = Math.round((r + m) * 255);
        int green = Math.round((g + m) * 255);
        int blue = Math.round((b + m) * 255);
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Calculates average hue of two hues.
     *
     * @param h1
     * @param h2
     * @return
     */
    private static float averageHue(float h1, float h2) {
        float diff = Math.abs(h1 - h2);
        if (diff > 180) {
            if (h1 > h2) {
                h1 -= 360;
            } else {
                h2 -= 360;
            }
        }
        return ((h1 + h2) / 2 + 360) % 360;
    }

    /**
     * Hidden constructor.
     */
    private ColorUtils() {
        //do nothing
    }
}
