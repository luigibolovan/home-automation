package ro.upt.ac.homeautomationremoteserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.upt.ac.homeautomationremoteserver.model.Atmosphere;
import ro.upt.ac.homeautomationremoteserver.model.Control;

import java.util.List;

public interface ControlDao extends JpaRepository<Control, Long> {
    Control findFirstByOrderByIdDesc();
    List<Control> findFirst10ByOrderByIdDesc();
}
