package ro.upt.ac.homeautomationremoteserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.upt.ac.homeautomationremoteserver.dao.ControlDao;
import ro.upt.ac.homeautomationremoteserver.model.Control;

import java.util.List;

@Service
public class ControlService {
    private static final long DEFAULT_ID_VALUE = 0;
    private final ControlDao mControlDao;

    @Autowired
    public ControlService(ControlDao mControlDao) {
        this.mControlDao = mControlDao;
    }

    public void addControl(Control control) {
        if (control.getDoorLock() == null || control.getLights() == null ||
            control.getDoorLock().equals("") || control.getLights().equals("")) {
            // nok
        } else {
            control.setId(DEFAULT_ID_VALUE);
            mControlDao.saveAndFlush(control);
        }
    }

    public List<Control> getAllControls() {
        return mControlDao.findAll();
    }

    public Control getCurrentControl() {
        return mControlDao.findFirstByOrderByIdDesc();
    }
}
