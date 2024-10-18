package es.unican.gasolineras.common;

import androidx.annotation.NonNull;

public enum BrandsEnum {

    REPSOL("Repsol"),
    CEPSA("Cepsa"),
    PETRONOR("Petronor"),
    CAMPSA("Campsa"),
    GALP("Galp"),
    SHELL("Carrefour"),
    BALLENOIL("Ballenoil"),
    CARREFOUR("Carrefour"),
    EROSKI("Eroski"),
    AVIA("Avia"),
    OTROS("Otros");


    private final String displayName;

    BrandsEnum(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }

    public static BrandsEnum fromString(String displayName) {
        // Recorre todos los valores del enum
        for (BrandsEnum brands : BrandsEnum.values()) {
            // Compara el displayName con el string proporcionado
            if (brands.displayName.equalsIgnoreCase(displayName)) {
                return brands;
            }
        }
        return null;
    }
}
