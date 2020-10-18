package com.mahitab.ecommerce.utils;

import com.mahitab.ecommerce.models.BannerList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class OlgorClient {
    private final Retrofit retrofit;
    private static OlgorClient instance;
    private static final String BASE_URL = "https://olgor.com//Mahitab/";

    private OlgorClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized OlgorClient getInstance() {
        if (instance == null)
            instance = new OlgorClient();
        return instance;
    }

    public Api getApi(){return retrofit.create(Api.class);}

    public interface Api {
        @GET("appbanners.php")
        Call<BannerList> getBanners();
    }
}
