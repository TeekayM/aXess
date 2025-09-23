package net.teekay.axess.utilities;

public class MathUtil {
    public static int clampInt(int val, int min, int max) {
        return Math.max(Math.min(val, max), min);
    }

    public static int calcScrollHeight(int height, int maxScrollPos) {
        if (maxScrollPos <= 0) {
            return height;
        } else {
           return  (int)Math.ceil((float)(height-1) * (float)height / (float)(height + maxScrollPos));
        }
    }

    public static int calcScrollPos(int height, int scrollerHeight, int scrollPos, int maxScrollPos) {
        return (int) ((float)(height-scrollerHeight+1) * ((float)scrollPos / (float)maxScrollPos));
    }
}
