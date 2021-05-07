package ro.upt.ac.homeautomationremoteserver.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.upt.ac.homeautomationremoteserver.model.Atmosphere;
import ro.upt.ac.homeautomationremoteserver.service.AtmosphereService;

import java.util.List;

@RestController
public class AtmosphereController {
    private final AtmosphereService mAtmService;

    @Autowired
    public AtmosphereController(AtmosphereService atmService) {
        mAtmService = atmService;
    }

    @PostMapping("/atmosphere")
    public void addAtmosphere(@RequestBody Atmosphere atmosphere) {
        mAtmService.addAtmosphere(atmosphere);
    }

    @GetMapping("/atmosphere")
    public List<Atmosphere> getAllAtmospheres() {
        return mAtmService.getAllAtmospheres();
    }

    @GetMapping("/atmosphere/latest")
    public Atmosphere getCurrentAtmosphere() {
        return mAtmService.getCurrentAtmosphere();
    }
}
