package ro.upt.ac.homeautomationremoteserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.upt.ac.homeautomationremoteserver.model.Control;

public interface ControlDao extends JpaRepository<Control, Long> {
    Control findFirstByOrderByIdDesc();
}
