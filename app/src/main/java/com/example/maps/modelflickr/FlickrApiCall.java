package com.example.maps.modelflickr;

import com.example.maps.model.ModelApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApiCall {
    @GET("?method=flickr.photos.search&api_key=79d466885188b99d6762980d64029892&format=json&nojsoncallback=1")
    Call<modelFlickr> getData(@Query("lat") String lat, @Query("lon") String lon);



}
