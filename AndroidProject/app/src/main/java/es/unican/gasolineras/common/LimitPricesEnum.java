package es.unican.gasolineras.common;

import androidx.annotation.NonNull;

public enum LimitPricesEnum {

    MIN_PRICE("1.0f"),
    MAX_PRICE("2.5f"),
    // To set precision al converting from int to float (100 = 0.00)
    SCALING_FACTOR("100");

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
}
