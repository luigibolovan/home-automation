package ro.upt.ac.homeautomationremoteserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.upt.ac.homeautomationremoteserver.dao.AtmosphereDao;
import ro.upt.ac.homeautomationremoteserver.model.Atmosphere;

import java.util.List;

@Service
public class AtmosphereService {
    private final AtmosphereDao mAtmDao;
    private static final long DEFAULT_ID_VALUE = 0;

    @Autowired
    public AtmosphereService(AtmosphereDao atmDao) {
        this.mAtmDao = atmDao;
    }

    public void addAtmosphere(Atmosphere atmosphere) {
        if (atmosphere.getHumidity() == null || atmosphere.getHumidity().equals("") ||
            atmosphere.getTemperature() == null || atmosphere.getTemperature().equals("") ||
            atmosphere.getMethaneGasConcentration() == null || atmosphere.getMethaneGasConcentration().equals("")) {
            // nok
        } else {
            atmosphere.setId(DEFAULT_ID_VALUE);
            this.mAtmDao.saveAndFlush(atmosphere);
        }
    }

    public List<Atmosphere> getAllAtmospheres() {
        return mAtmDao.findAll();
    }

    public List<Atmosphere> getLatest10Atmospheres() {
        return mAtmDao.findFirst10ByOrderByIdDesc();
    }

    public Atmosphere getCurrentAtmosphere() {
        return mAtmDao.findFirstByOrderByIdDesc();
    }

}
