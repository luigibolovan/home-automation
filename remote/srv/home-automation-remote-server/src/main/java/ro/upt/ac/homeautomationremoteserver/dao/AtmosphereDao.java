package ro.upt.ac.homeautomationremoteserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.upt.ac.homeautomationremoteserver.model.Atmosphere;

public interface AtmosphereDao extends JpaRepository<Atmosphere, Long> {
    Atmosphere findFirstByOrderByIdDesc();
}
