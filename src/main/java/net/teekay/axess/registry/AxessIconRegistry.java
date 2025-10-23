package net.teekay.axess.registry;

import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;

import java.util.ArrayList;
import java.util.HashMap;

public class AxessIconRegistry {

    public static class AxessIcon {
        public ResourceLocation TEXTURE;
        public String ID;

        public AxessIcon(String id, ResourceLocation tex) {
            TEXTURE = tex;
            ID = id;
        }
    }
    

    private static HashMap<String, AxessIcon> ENTRIES = new HashMap<>();
    private static ArrayList<AxessIcon> ORDERED_ENTRIES = new ArrayList<>();

    private static HashMap<String, AxessIcon> META_ENTRIES = new HashMap<>();
    private static ArrayList<AxessIcon> ORDERED_META_ENTRIES = new ArrayList<>();

    // ICONS

    // META
    public static AxessIcon SCAN = registerMetaIcon("scan");
    public static AxessIcon ACCEPT = registerMetaIcon("accept");
    public static AxessIcon DENY = registerMetaIcon("deny");
    public static AxessIcon DEBUG = registerMetaIcon("debug");
    public static AxessIcon CONFIGURE = registerMetaIcon("configure");

    // NORMAL ALPHABET
    public static AxessIcon A = registerIcon("a");
    public static AxessIcon B = registerIcon("b");
    public static AxessIcon C = registerIcon("c");
    public static AxessIcon D = registerIcon("d");
    public static AxessIcon E = registerIcon("e");
    public static AxessIcon F = registerIcon("f");
    public static AxessIcon G = registerIcon("g");
    public static AxessIcon H = registerIcon("h");
    public static AxessIcon I = registerIcon("i");
    public static AxessIcon J = registerIcon("j");
    public static AxessIcon K = registerIcon("k");
    public static AxessIcon L = registerIcon("l");
    public static AxessIcon M = registerIcon("m");
    public static AxessIcon N = registerIcon("n");
    public static AxessIcon O = registerIcon("o");
    public static AxessIcon P = registerIcon("p");
    public static AxessIcon Q = registerIcon("q");
    public static AxessIcon R = registerIcon("r");
    public static AxessIcon S = registerIcon("s");
    public static AxessIcon T = registerIcon("t");
    public static AxessIcon U = registerIcon("u");
    public static AxessIcon V = registerIcon("v");
    public static AxessIcon W = registerIcon("w");
    public static AxessIcon X = registerIcon("x");
    public static AxessIcon Y = registerIcon("y");
    public static AxessIcon Z = registerIcon("z");
    
    // SQUARE ALPHABET
    public static AxessIcon SQUARE_A = registerIcon("square_a");
    public static AxessIcon SQUARE_B = registerIcon("square_b");
    public static AxessIcon SQUARE_C = registerIcon("square_c");
    public static AxessIcon SQUARE_D = registerIcon("square_d");
    public static AxessIcon SQUARE_E = registerIcon("square_e");
    public static AxessIcon SQUARE_F = registerIcon("square_f");
    public static AxessIcon SQUARE_G = registerIcon("square_g");
    public static AxessIcon SQUARE_H = registerIcon("square_h");
    public static AxessIcon SQUARE_I = registerIcon("square_i");
    public static AxessIcon SQUARE_J = registerIcon("square_j");
    public static AxessIcon SQUARE_K = registerIcon("square_k");
    public static AxessIcon SQUARE_L = registerIcon("square_l");
    public static AxessIcon SQUARE_M = registerIcon("square_m");
    public static AxessIcon SQUARE_N = registerIcon("square_n");
    public static AxessIcon SQUARE_O = registerIcon("square_o");
    public static AxessIcon SQUARE_P = registerIcon("square_p");
    public static AxessIcon SQUARE_Q = registerIcon("square_q");
    public static AxessIcon SQUARE_R = registerIcon("square_r");
    public static AxessIcon SQUARE_S = registerIcon("square_s");
    public static AxessIcon SQUARE_T = registerIcon("square_t");
    public static AxessIcon SQUARE_U = registerIcon("square_u");
    public static AxessIcon SQUARE_V = registerIcon("square_v");
    public static AxessIcon SQUARE_W = registerIcon("square_w");
    public static AxessIcon SQUARE_X = registerIcon("square_x");
    public static AxessIcon SQUARE_Y = registerIcon("square_y");
    public static AxessIcon SQUARE_Z = registerIcon("square_z");
    
    // NORMAL NUMBERS
    public static AxessIcon ZERO = registerIcon("zero");
    public static AxessIcon ONE = registerIcon("one");
    public static AxessIcon TWO = registerIcon("two");
    public static AxessIcon THREE = registerIcon("three");
    public static AxessIcon FOUR = registerIcon("four");
    public static AxessIcon FIVE = registerIcon("five");
    public static AxessIcon SIX = registerIcon("six");
    public static AxessIcon SEVEN = registerIcon("seven");
    public static AxessIcon EIGHT = registerIcon("eight");
    public static AxessIcon NINE = registerIcon("nine");

    // ROMAN NUMBERS
    public static AxessIcon ROMAN_ONE = registerIcon("roman_one");
    public static AxessIcon ROMAN_TWO = registerIcon("roman_two");
    public static AxessIcon ROMAN_THREE = registerIcon("roman_three");
    public static AxessIcon ROMAN_FOUR = registerIcon("roman_four");
    public static AxessIcon ROMAN_FIVE = registerIcon("roman_five");
    public static AxessIcon ROMAN_SIX = registerIcon("roman_six");
    public static AxessIcon ROMAN_SEVEN = registerIcon("roman_seven");
    public static AxessIcon ROMAN_EIGHT = registerIcon("roman_eight");
    public static AxessIcon ROMAN_NINE = registerIcon("roman_nine");
    public static AxessIcon ROMAN_TEN = registerIcon("roman_ten");

    // SQUARE NUMBERS
    public static AxessIcon SQUARE_ZERO = registerIcon("square_zero");
    public static AxessIcon SQUARE_ONE = registerIcon("square_one");
    public static AxessIcon SQUARE_TWO = registerIcon("square_two");
    public static AxessIcon SQUARE_THREE = registerIcon("square_three");
    public static AxessIcon SQUARE_FOUR = registerIcon("square_four");
    public static AxessIcon SQUARE_FIVE = registerIcon("square_five");
    public static AxessIcon SQUARE_SIX = registerIcon("square_six");
    public static AxessIcon SQUARE_SEVEN = registerIcon("square_seven");
    public static AxessIcon SQUARE_EIGHT = registerIcon("square_eight");
    public static AxessIcon SQUARE_NINE = registerIcon("square_nine");

    // GREEK ALPHABET
    public static AxessIcon PI = registerIcon("pi");
    public static AxessIcon PHI = registerIcon("phi");
    public static AxessIcon LAMBDA = registerIcon("lambda");
    public static AxessIcon OMEGA = registerIcon("omega");
    public static AxessIcon PSI = registerIcon("psi");
    public static AxessIcon THETA = registerIcon("theta");
    public static AxessIcon DELTA = registerIcon("delta");

    // SHAPES
    public static AxessIcon SQUARE = registerIcon("square");
    public static AxessIcon SQUARE_FILLED = registerIcon("square_filled");
    public static AxessIcon CIRCLE = registerIcon("circle");
    public static AxessIcon CIRCLE_FILLED = registerIcon("circle_filled");
    public static AxessIcon TRIANGLE = registerIcon("triangle");
    public static AxessIcon TRIANGLE_FILLED = registerIcon("triangle_filled");
    public static AxessIcon HEART = registerIcon("heart");
    public static AxessIcon HEART_FILLED = registerIcon("heart_filled");

    // MISCELLANEOUS
    public static AxessIcon SIX_NINE = registerIcon("six_nine");
    public static AxessIcon SIX_SEVEN = registerIcon("six_seven");
    public static AxessIcon HOME = registerIcon("home");
    public static AxessIcon BLACK_HOLE = registerIcon("black_hole");
    public static AxessIcon NONE = registerIcon("none");

    public static AxessIcon getIcon(String iconID) {
        AxessIcon icon = ENTRIES.get(iconID);
        if (icon != null) return icon;

        icon = META_ENTRIES.get(iconID);
        if (icon != null) return icon;

        return NONE;
    }

    public static AxessIcon registerIcon(String iconID) {
        ResourceLocation iconTex = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/icon/" + iconID + ".png");
        AxessIcon icon = new AxessIcon(iconID, iconTex);
        ENTRIES.put(icon.ID, icon);
        ORDERED_ENTRIES.add(icon);

        return icon;
    }

    public static AxessIcon registerMetaIcon(String iconID) {
        ResourceLocation iconTex = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/icon/" + iconID + ".png");
        AxessIcon icon = new AxessIcon(iconID, iconTex);
        META_ENTRIES.put(icon.ID, icon);
        ORDERED_META_ENTRIES.add(icon);

        return icon;
    }

    public static ArrayList<AxessIcon> getAllEntries() {
        return ORDERED_ENTRIES;
    }

    public static ArrayList<AxessIcon> getAllMetaEntries() {
        return ORDERED_META_ENTRIES;
    }

}