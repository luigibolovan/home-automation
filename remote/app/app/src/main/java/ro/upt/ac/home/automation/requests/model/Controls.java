package ro.upt.ac.home.automation.requests.model;

public class Controls {
    public Controls(String doorLock, String lights) {
        DoorLock = doorLock;
        Lights = lights;
    }

    public String getDoorLock() {
        return DoorLock;
    }

    public String getLights() {
        return Lights;
    }

    public void setDoorLock(String doorLock) {
        DoorLock = doorLock;
    }

    public void setLights(String lights) {
        Lights = lights;
    }

    private String DoorLock;
    private String Lights;
}
