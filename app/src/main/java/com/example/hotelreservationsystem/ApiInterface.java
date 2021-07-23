package com.example.hotelreservationsystem;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedInput;


public interface ApiInterface {

    // API's endpoints - get API endpoint
    @GET("/hotellist")
    public void getHotelsLists(Callback<List<HotelListData>> callback);

    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    // Post API endpoint
    @POST("/reservation")
    public void addGuestLists(@Body TypedInput body, Callback<ConfirmationNumber> callback);

}
