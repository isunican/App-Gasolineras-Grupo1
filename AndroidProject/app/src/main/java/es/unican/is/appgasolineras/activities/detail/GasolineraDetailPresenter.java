package es.unican.is.appgasolineras.activities.detail;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;

public class GasolineraDetailPresenter implements IDetailContract.Presenter {
    Gasolinera g;
    private final IDetailContract.View view;
    GasolineraDatabase db;

    public GasolineraDetailPresenter(Gasolinera g, IDetailContract.View view, GasolineraDatabase db) {
        this.view = view;
        this.g = g;
        this.db = db;
    }

    @Override
    public void init() {
        String municipio = "-";
        if (!g.getMunicipio().isEmpty()) {
            municipio = g.getMunicipio();
        }
        String rotulo = "-";
        if (!g.getRotulo().isEmpty()) {
            rotulo = g.getRotulo().toLowerCase();
        }
        String horario = "-";
        if (!g.getHorario().isEmpty()) {
            horario = g.getHorario();
        }
        String precioDiesel = "-";
        if (!g.getDieselA().isEmpty()) {
            precioDiesel = g.getDieselA();
            if (precioDiesel.length() > 3) {
                precioDiesel = precioDiesel.substring(0, 4) + " €";
            } else {
                precioDiesel += " €";
            }
        }
        String precioGasolina = "-";
        if (!g.getNormal95().isEmpty()) {
            precioGasolina = g.getNormal95();
            if (precioGasolina.length() > 3) {
                precioGasolina = precioGasolina.substring(0, 4) + " €";
            } else {
                precioGasolina += " €";
            }
        }
        String direccion = "-";
        if (!g.getDireccion().isEmpty()) {
            direccion = g.getDireccion();
        }

        view.setInfo(municipio, rotulo, horario, precioGasolina,
                precioDiesel, this.calcula(), direccion);
    }

    @Override
    public String calcula() {
        if (g.getDieselA().isEmpty() || g.getNormal95().isEmpty()) {
            return "-";
        }
        double diesel = Double.parseDouble(g.getDieselA().replace(",", "."));
        double gasolina = Double.parseDouble(g.getNormal95().replace(",", "."));
        return String.valueOf((2 * gasolina + diesel) / 3).replace(".", ",").substring(0, 4) + " €";
    }

    @Override
    public void anhadeADb() {
        GasolineraDao dao = db.gasolineraDao();
        if (dao.getGasolineraById(Integer.parseInt(g.getId())) == null) {
            dao.insertAll(g);
            view.showAnhadido();
        } else {
            view.showNoAnhadido();
        }
    }
}
