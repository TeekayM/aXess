package net.teekay.axess.utilities;

import java.awt.*;

public class AxessColors {
    public static Color MAIN = new Color(0x1170FF);
    public static Color RED = new Color(0xFF0000);
    public static Color GREEN = new Color(0x00FF00);
    public static Color GREEN_LESS = new Color(0x006B00);


    public static class HSVColor {
        public float h, s, v;
        public HSVColor(float h, float s, float v) {
            this.h = h;
            this.s = s;
            this.v = v;
        }
    }

    public static Color toRGB(HSVColor c) {
        return hsvToRgb(c.h, c.s, c.v);
    }

    public static HSVColor toHsv(Color c) {
        return rgbToHsv(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }

    public static HSVColor rgbToHsv(float r, float g, float b)
    {
        float cmax = Math.max(r, Math.max(g, b));
        float cmin = Math.min(r, Math.min(g, b));
        float diff = cmax - cmin;
        float h = -1, s = -1;

        if (cmax == cmin)
            h = 0;

        else if (cmax == r)
            h = (60 * ((g - b) / diff) + 360) % 360;
        else if (cmax == g)
            h = (60 * ((b - r) / diff) + 120) % 360;
        else if (cmax == b)
            h = (60 * ((r - g) / diff) + 240) % 360;

        if (cmax == 0)
            s = 0;
        else
            s = (diff / cmax) * 100;

        float v = cmax * 100f;

        //System.out.println(r + " " + g + " " + b + " -> " + h + " " + s + " " + v);

        return new HSVColor(h, s, v);
    }

    public static Color hsvToRgb(float h, float s, float v) {
        h = h % 360f;
        s = s / 100f;
        v = v / 100f;

        float r, g, b;

        int i = (int)Math.floor(h / 60f) % 6;
        float f = (h / 60f) - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        switch (i) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            case 5 -> { r = v; g = p; b = q; }
            default -> throw new IllegalStateException("Unexpected hue sector: " + i);
        }

        //System.out.println(h + " " + s + " " + v + " -> " + r + " " + g + " " + b);

        return new Color(r, g, b);
    }


}
