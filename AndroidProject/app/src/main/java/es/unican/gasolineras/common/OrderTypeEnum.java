package es.unican.gasolineras.common;

public enum OrderTypeEnum {

    Price("Precio"),
    None("Ninguno");

    private final String displayName;

    OrderTypeEnum(String displayName) {
            this.displayName = displayName;
        }
}

