package com.example.demoapplication;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    // API's endpoints
    @GET("singlesearch/shows")
    Call<ShowsModel>
    getEpisodeList(@Query("q")String q ,@Query("embed")String embed);



}