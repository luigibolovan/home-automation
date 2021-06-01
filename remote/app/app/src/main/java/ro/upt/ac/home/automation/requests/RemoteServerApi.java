package ro.upt.ac.home.automation.requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ro.upt.ac.home.automation.requests.model.Atmosphere;
import ro.upt.ac.home.automation.requests.model.Controls;

public interface RemoteServerApi {
    @GET("/atmosphere/latest")
    Call<Atmosphere> getAtmosphere();

    @GET("/atmosphere")
    Call<List<Atmosphere>>getAllAtmospheres();

    @GET("/atmosphere/latest10")
    Call<List<Atmosphere>> getAtmospheres();

    @GET("/control/latest")
    Call<Controls> getControl();

    @GET("/control/latest10")
    Call<List<Controls>> getControls();

    @GET("/control")
    Call<List<Controls>> getAllControls();

    @POST("/control")
    Call<Controls> setControl(@Body Controls control);
}
