package es.unican.gasolineras.common;

import androidx.annotation.NonNull;

public enum OrderMethodsEnum {
    Ascending("Ascendente"),
    Descending("Descendente");

    private final String displayName;

    OrderMethodsEnum(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
    public static OrderMethodsEnum fromString(String displayName) {
        // Recorre todos los valores del enum
        for (OrderMethodsEnum orderMethod : OrderMethodsEnum.values()) {
            // Compara el displayName con el string proporcionado
            if (orderMethod.displayName.equalsIgnoreCase(displayName)) {
                return orderMethod;
            }
        }
        return null;
    }
}
