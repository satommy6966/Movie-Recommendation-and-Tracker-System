package cw3;

import java.util.Scanner;

public final class Validators {
    private Validators() {}

    public static int safeParseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
    }
    public static long safeParseLong(String s, long fallback) {
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return fallback; }
    }
    public static double safeParseDouble(String s, double fallback) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return fallback; }
    }

    public static int requireIntInRange(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine();
            int v = safeParseInt(line, Integer.MIN_VALUE);
            if (v >= min && v <= max) return v;
            System.out.println("Invalid input, please enter an integer in [" + min + ", " + max + "]:");
        }
    }
}