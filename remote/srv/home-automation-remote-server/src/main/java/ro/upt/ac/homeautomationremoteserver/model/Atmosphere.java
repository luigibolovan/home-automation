package ro.upt.ac.homeautomationremoteserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name="atmosphere")
public class Atmosphere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;
    private String  date;
    private String  temperature;
    private String  humidity;
    private String  methaneGasConcentration;

    public Atmosphere(Long id,
                      @JsonProperty("Date")String date,
                      @JsonProperty("Temperature")int temperature,
                      @JsonProperty("Humidity")int humidity,
                      @JsonProperty("MethaneGasConcentration")int methaneGasConcentration) {
        this.id = id;
        this.date = date;
        this.temperature = String.valueOf(temperature);
        this.humidity = String.valueOf(humidity);
        this.methaneGasConcentration = String.valueOf(methaneGasConcentration);
    }

    public Atmosphere() {
    }

    public Atmosphere(String date, int temperature, int humidity, int methaneGasConcentration) {
        this.date = date;
        this.temperature = String.valueOf(temperature);
        this.humidity = String.valueOf(humidity);
        this.methaneGasConcentration = String.valueOf(methaneGasConcentration);
    }

    @Id
    public Long getId() {
        return this.id;
    }

    public void setId(Long uid) {
        this.id = uid;
    }

    public String getDate() {
        return date;
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

    public void setDate(String date) {
        this.date = date;
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
