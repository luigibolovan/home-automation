package ro.upt.ac.home.automation.requests;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {
    private static ServiceBuilder instance;
    private final String baseURL = "http://35.158.221.217:1310/";

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ServiceBuilder() { }

    public static ServiceBuilder getInstance() {
        if (instance == null) {
            instance = new ServiceBuilder();
        }
        return instance;
    }

    public <T> T buildService(Class<T> service) {
        return retrofit.create(service);
    }
}
