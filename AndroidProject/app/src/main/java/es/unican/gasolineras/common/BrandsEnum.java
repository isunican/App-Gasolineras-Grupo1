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

    public static BrandsEnum fromString(String rotulo) {
        // Recorre todos los valores del enum
        if (rotulo == null){
            return OTROS;
        }

        for (BrandsEnum brand : BrandsEnum.values()) {
            // Compara el rótulo con el string proporcionado
            if (brand.displayName.toLowerCase().contains(rotulo.toLowerCase())) {
                return brand;
            }
        }
        return OTROS;
    }
}
