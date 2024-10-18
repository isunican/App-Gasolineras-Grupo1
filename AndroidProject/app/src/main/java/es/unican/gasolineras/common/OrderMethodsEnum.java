package es.unican.gasolineras.common;

public enum OrderMethodsEnum {
    Ascending("Ascendente"),
    Descending("Descendente");

    private final String displayName;

    OrderMethodsEnum(String displayName) {
        this.displayName = displayName;
    }
}
