package es.unican.gasolineras.activities.main;

public class Selection {

    private String value;
    private boolean selected;

    // Constructor con todos los atributos
    public Selection(String value, boolean selected) {
        this.value = value;
        this.selected = selected;
    }

    // Getter para 'value'
    public String getValue() {
        return value;
    }

    // Setter para 'value'
    public void setValue(String value) {
        this.value = value;
    }

    // Getter para 'selected'
    public boolean isSelected() {
        return selected;
    }

    // Setter para 'selected'
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

