package es.unican.is.appgasolineras.activities.filtroPermanente;

import es.unican.is.appgasolineras.common.prefs.IPrefs;


public class FiltroPermanentePresenter implements IPermanenteContract.Presenter{
    private IPrefs pref;
    private FiltroPermanenteMapper mapper;

    private static final String IDCOMUNIDAD = "idComunidad";
    private static final String IDCOMUNIDADNAME = "idComunidadName";
    private static final String TIPOGASOLINA = "tipoGasolina";
    private static final String UBICACION = "ubicacion";

    public FiltroPermanentePresenter(IPrefs pref){
        this.pref = pref;
        mapper = new FiltroPermanenteMapper();
    }

    @Override
    public void init() {
        //No necesita inicializarse nada
    }

    @Override
    public void guardaFiltroPermanente(int idComunidad, int tipoGasolina, boolean ubicacion) {
        pref.putString(TIPOGASOLINA, mapper.getCombustible(tipoGasolina));
        pref.putString(IDCOMUNIDAD, mapper.getCCAAID(idComunidad));
        pref.putString(IDCOMUNIDADNAME, mapper.getCCAAName(idComunidad));
        if (ubicacion) {
            pref.putString(UBICACION, "si");
        } else {
            pref.putString(UBICACION, "no");
        }
    }

    @Override
    public void reseteaFiltroPermanente(){
        pref.putString(TIPOGASOLINA, "");
        pref.putString(IDCOMUNIDAD, "");
        pref.putString(IDCOMUNIDADNAME, "");
        pref.putString(UBICACION,"No");
    }
}