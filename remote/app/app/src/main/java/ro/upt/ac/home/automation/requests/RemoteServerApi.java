package ro.upt.ac.home.automation.requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ro.upt.ac.home.automation.requests.model.Atmosphere;
import ro.upt.ac.home.automation.requests.model.Controls;

public interface RemoteServerApi {
    @GET("atmosphere/latest")
    Call<Atmosphere> getAtmosphere();

    @GET("/atmosphere/latest10")
    Call<List<Atmosphere>> getAtmospheres();

    @GET("control/latest")
    Call<Controls> getControl();

    @GET("control/latest10")
    Call<List<Controls>> getControls();

    @POST("control")
    Call<Void> setControl(@Body Controls control);
}
