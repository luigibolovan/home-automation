package ro.upt.ac.homeautomationremoteserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="control")
public class Control {
    private Long    id;
    private String  lights;
    private String  doorLock;

    public Control() { }

    public Control(@JsonProperty("DoorLock")String doorLock,
                   @JsonProperty("Lights")String lights) {
        this.doorLock   = doorLock;
        this.lights     = lights;
    }

    public Control(Long id, String doorLock, String lights) {
        this.id         = id;
        this.doorLock   = doorLock;
        this.lights     = lights;
    }

    public String getDoorLock() {
        return doorLock;
    }

    public void setDoorLock(String doorLock) {
        this.doorLock = doorLock;
    }

    public void setLights(String lights) {
        this.lights = lights;
    }

    public String getLights() {
        return lights;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}