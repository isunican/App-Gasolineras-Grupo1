package es.unican.is.appgasolineras.repository;

import android.content.Context;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import es.unican.is.appgasolineras.common.Callback;
import es.unican.is.appgasolineras.common.prefs.Prefs;
import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.model.GasolinerasResponse;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;
import es.unican.is.appgasolineras.repository.rest.GasolinerasService;

/**
 * Implementation of a gas stations repository.
 * In this case, the gas stations are retrieved from a REST API.
 * The repository also persists into a local DB the retrieved list of gas stations.
 */
public class GasolinerasRepository implements IGasolinerasRepository {

    private static final String KEY_LAST_SAVED = "KEY_LAST_SAVED";

    private final Context context;

    public GasolinerasRepository(final Context context) {
        this.context = context;
    }

    @Override
    public void requestGasolineras(Callback<List<Gasolinera>> cb) {
        GasolinerasService.requestGasolineras(new Callback<GasolinerasResponse>() {
            @Override
            public void onSuccess(GasolinerasResponse data) {
                List<Gasolinera> gasolineras = data.getStations();
                persistToDB(gasolineras);
                cb.onSuccess(gasolineras);
            }

            @Override
            public void onFailure() {
                cb.onFailure();
            }
        });
    }

    @Override
    public List<Gasolinera> getGasolineras(String idCCAA) {
        GasolinerasResponse response = GasolinerasService.getGasolineras(idCCAA);
        List<Gasolinera> gasolineras = response != null ? response.getStations() : null;
        persistToDB(gasolineras);
        return gasolineras;
    }

    public List<Gasolinera> todasGasolineras() {
        GasolinerasResponse response = GasolinerasService.todasGasolineras();
        List<Gasolinera> gasolineras = response != null ? response.getStations() : null;
        persistToDB(gasolineras);
        return gasolineras;
    }

    public List<Gasolinera> gasolinerasMunicipio(String idMun) {
        GasolinerasResponse response = GasolinerasService.gasolinerasMunicipio(idMun);
        List<Gasolinera> gasolineras = response != null ? response.getStations() : null;
        persistToDB(gasolineras);
        return gasolineras;
    }


    /**
     * Persist Gasolineras to local DB using Room
     * It also saves into the app preferences the time instant in which
     * this save occured. This information can be used to determine the age
     * of this data stored in the local DB
     * @param data
     */
    private void persistToDB(List<Gasolinera> data) {
        if (data != null) {
            final GasolineraDatabase db = GasolineraDatabase.getDB(context);
            final GasolineraDao gasolinerasDao = db.gasolineraDao();
            gasolinerasDao.deleteAll();

            Gasolinera[] gasolineras = data.toArray(new Gasolinera[data.size()]);
            gasolinerasDao.insertAll(gasolineras);

            // save the current time to the app preferences
            Prefs.from(context).putInstant(KEY_LAST_SAVED, Instant.now());
        }
    }

    /**
     * Returns true if the gas stations currently stored in the local DB is older than the specified
     * amount of minutes
     * @param minutes
     * @return true if the data currently stored in the local DB is older than the specified
     * amount of minutes
     */
    private boolean lastDownloadOlderThan(int minutes) {
        Instant lastDownloaded = Prefs.from(context).getInstant(KEY_LAST_SAVED);
        if (lastDownloaded == null) {
            return true;
        } else {
            Instant now = Instant.now();
            long sinceLastDownloaded = ChronoUnit.MINUTES.between(lastDownloaded, now);  // minutes
            return (sinceLastDownloaded > minutes) ? true : false;
        }
    }

}
