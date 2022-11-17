package es.unican.is.appgasolineras.activities.listaFavoritas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;

public class ListaFavoritasPresenter implements IListaFavoritasContract.Presenter {

    private List<Gasolinera> shownGasolineras;
    private final IListaFavoritasContract.View view;
    private IGasolinerasRepository repository;
    private final GasolineraDao dao;
    private final Boolean red;
    private Boolean estaRepositorioCaido = false;


    public ListaFavoritasPresenter(IListaFavoritasContract.View view, GasolineraDatabase db, Boolean red) {
        this.view = view;
        this.red = red;
        dao = db.gasolineraDao();
    }

    @Override
    public void init() {
        if (repository == null) {
            repository = view.getGasolineraRepository();
        }
        if (repository != null) {
            if (Boolean.TRUE.equals(red)) {
                doSyncInitFavoritas();
            } else {
                view.showLoadErrorRed();
            }
        }
    }

    @Override
    public void doSyncInitFavoritas() {
        List<Gasolinera> gasolinerasFavoritasSinActualizar = dao.getAll();
        Iterator<Gasolinera> it = gasolinerasFavoritasSinActualizar.iterator();
        boolean hayMasDeUnaComunidad = false;
        String idComunidadUnica = null;
        // En este while comprobamos si hay mas de una comunidad autonoma en la lista
        while (it.hasNext() && !hayMasDeUnaComunidad) {
            String idComunidadActual = it.next().getIDCCAA();
            if (idComunidadUnica == null) {
                idComunidadUnica = idComunidadActual;
            } else if (!idComunidadUnica.equals(idComunidadActual)) {
                hayMasDeUnaComunidad = true;
            }
        }
        if (!gasolinerasFavoritasSinActualizar.isEmpty()) {
            // Conseguimos las gasolineras actualizadas
            List<Gasolinera> todasGasolineras = conseguirGasolinerasActualizadas(hayMasDeUnaComunidad, gasolinerasFavoritasSinActualizar, idComunidadUnica);
            // De las gasolineras filtradas nos quedamos solo con las que tenemos en favoritas
            todasGasolineras.retainAll(gasolinerasFavoritasSinActualizar);
            List<Gasolinera> listaFinal = new ArrayList<>();
            // Ordenamos la lista actualizada para respetar el orden anterior
            for (Gasolinera gDao : gasolinerasFavoritasSinActualizar) {
                for (Gasolinera gTodas : todasGasolineras) {
                    if (gDao.getId().equals(gTodas.getId())) {
                        listaFinal.add(gTodas);
                    }
                }
            }
            Collections.reverse(listaFinal);
            if (estaRepositorioCaido) {
                shownGasolineras = null;
                view.showLoadErrorServidor();
            } else {
                view.showGasolineras(listaFinal);
                shownGasolineras = listaFinal;
                view.showLoadCorrect(listaFinal.size());
            }
        } else {
            shownGasolineras = null;
            view.showLoadErrorDAOVacia();
        }
    }

    public List<Gasolinera> conseguirGasolinerasActualizadas(boolean masDeUnaComunidad, List<Gasolinera> lista, String idComunidad) {
        List<Gasolinera> todasGasolineras = new ArrayList<>();
        if (!masDeUnaComunidad) {
            todasGasolineras = repository.getGasolineras(idComunidad);
        } else if (!lista.isEmpty()){
            Map<String, List<String>> mapaMun = new HashMap<>();
            for (Gasolinera g : lista) {
                List<String> listaId = mapaMun.get(g.getIdMun());
                if (listaId == null) {
                    listaId = new ArrayList<>();
                }
                listaId.add(g.getId());
                mapaMun.put(g.getIdMun(), listaId);
            }
            for (String idMun : mapaMun.keySet()) {
                List<Gasolinera> listaGasolinerasMunicipio = repository.gasolinerasMunicipio(idMun);
                if (listaGasolinerasMunicipio == null) {
                    estaRepositorioCaido = true;
                } else {
                    todasGasolineras.addAll(listaGasolinerasMunicipio);
                }
            }
        }
        return todasGasolineras;
    }

    @Override
    public void onGasolineraClicked(int index) {
        if (shownGasolineras != null && index < shownGasolineras.size()) {
            Gasolinera gasolinera = shownGasolineras.get(index);
            view.openGasolineraDetails(gasolinera);
        }
    }

}
