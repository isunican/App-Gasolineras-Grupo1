package es.unican.gasolineras.common;

import androidx.annotation.NonNull;

public enum FuelTypeEnum {

    GASOLINA_95E5("Gasolina 95"),
    GASOLEO_A("Diesel");

    private final String displayName;

    FuelTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }

    public static FuelTypeEnum fromString(String displayName) {
        // Recorre todos los valores del enum
        for (FuelTypeEnum fuelType : FuelTypeEnum.values()) {
            // Compara el displayName con el string proporcionado
            if (fuelType.displayName.equalsIgnoreCase(displayName)) {
                return fuelType;
            }
        }
        return null;
    }
}
