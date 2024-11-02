package es.unican.gasolineras.common;

import androidx.annotation.NonNull;

public enum LimitPricesEnum {

    MIN_PRICE("1.0"),
    MAX_PRICE("2.5"),
    // To set precision when converting from int to float (100 = 0.00)
    SCALING_FACTOR("100"),
    // Calculated seekbar progress
    STATIC_SEEKBAR_PROGRESS(calculateSeekbarProgress());

    private final String displayLimitPrices;

    LimitPricesEnum(String displayLimitPrices) {
        this.displayLimitPrices = displayLimitPrices;
    }

    @NonNull
    @Override
    public String toString() {
        return displayLimitPrices;
    }

    public static LimitPricesEnum fromString(String displayName) {
        // Recorre todos los valores del enum
        for (LimitPricesEnum fuelType : LimitPricesEnum.values()) {
            // Compara el displayLimitPrices con el string proporcionado
            if (fuelType.displayLimitPrices.equalsIgnoreCase(displayName)) {
                return fuelType;
            }
        }
        return null;
    }

    /*
     * Actualizamos los valores máximos y mínimos del SeekBar para que sean float,
     * aunque la implementación seekbar original solo permita valores int.
     * Esto se consigue mediante la siguiente fórmula.
     */
    private static String calculateSeekbarProgress() {
        float minPriceLimit = Float.parseFloat(MIN_PRICE.displayLimitPrices);
        float maxPriceLimit = Float.parseFloat(MAX_PRICE.displayLimitPrices);
        int scalingFactor = Integer.parseInt(SCALING_FACTOR.displayLimitPrices);
        return String.valueOf((int) ((Math.ceil((1.649 - 1.237) * 100) / 100) * scalingFactor));
    }
}
