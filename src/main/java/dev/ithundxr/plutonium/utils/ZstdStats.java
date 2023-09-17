package dev.ithundxr.plutonium.utils;

public class ZstdStats {
    private static double ZstdRatioPast5Minutes;

    public static void setRatio(double ratio) {
        ZstdRatioPast5Minutes = ratio;
    }

    public static double getRatio() {
        return ZstdRatioPast5Minutes;
    }
}
