package ro.upt.ac.home.automation.requests.model;

public class Controls {
    private long id;

    public Controls(String doorLock, String lights) {
        DoorLock = doorLock;
        Lights = lights;
    }

    public long getId() {
        return id;
    }

    public String getDoorLock() {
        return DoorLock;
    }

    public String getLights() {
        return Lights;
    }

    private String DoorLock;
    private String Lights;
}
