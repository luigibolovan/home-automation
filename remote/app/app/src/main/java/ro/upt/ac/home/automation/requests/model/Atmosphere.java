package ro.upt.ac.home.automation.requests.model;

public class Atmosphere {
    public String getTemperature() {
        return Temperature;
    }

    public String getHumidity() {
        return Humidity;
    }

    public String getMethaneGasConcentration() {
        return MethaneGasConcentration;
    }

    public long getId() {
        return id;
    }

    private String Temperature;
    private String Humidity;
    private String MethaneGasConcentration;
    private long id;

}
