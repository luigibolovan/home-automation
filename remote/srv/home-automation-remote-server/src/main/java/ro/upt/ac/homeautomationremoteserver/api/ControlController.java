package ro.upt.ac.homeautomationremoteserver.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.upt.ac.homeautomationremoteserver.model.Control;
import ro.upt.ac.homeautomationremoteserver.service.ControlService;

import java.util.List;

@RestController
public class ControlController {
    private final ControlService mCtrlService;

    @Autowired
    public ControlController(ControlService ctrlService) {
        this.mCtrlService = ctrlService;
    }

    @PostMapping("/control")
    public void addControl(@RequestBody Control ctrl) {
        mCtrlService.addControl(ctrl);
    }

    @GetMapping("/control")
    public List<Control> getAllControls() {
        return mCtrlService.getAllControls();
    }

    @GetMapping("/control/latest")
    public Control getCurrentControl() {
        return mCtrlService.getCurrentControl();
    }
}
