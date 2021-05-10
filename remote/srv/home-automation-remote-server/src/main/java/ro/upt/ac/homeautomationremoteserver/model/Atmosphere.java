package ro.upt.ac.homeautomationremoteserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name="atmosphere")
public class Atmosphere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;
    private String  temperature;
    private String  humidity;
    private String  methaneGasConcentration;

    public Atmosphere(Long id,
                      @JsonProperty("Temperature")String temperature,
                      @JsonProperty("Humidity")String humidity,
                      @JsonProperty("MethaneGasConcentration")String methaneGasConcentration) {
        this.id                         = id;
        this.temperature                = temperature;
        this.humidity                   = humidity;
        this.methaneGasConcentration    = methaneGasConcentration;
    }

    public Atmosphere() {
    }

    public Atmosphere(String temperature, String humidity, String methaneGasConcentration) {
        this.temperature             = temperature;
        this.humidity                = humidity;
        this.methaneGasConcentration = methaneGasConcentration;
    }

    @Id
    public Long getId() {
        return this.id;
    }

    public void setId(Long uid) {
        this.id = uid;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getMethaneGasConcentration() {
        return methaneGasConcentration;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setMethaneGasConcentration(String methaneGasConcentration) {
        this.methaneGasConcentration = methaneGasConcentration;
    }
}
