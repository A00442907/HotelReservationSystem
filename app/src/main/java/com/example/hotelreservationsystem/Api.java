package com.example.hotelreservationsystem;

import retrofit.RestAdapter;

public class Api {

    public static ApiInterface getClient() {

        // change your base URL
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://a00445457hotelreservation-env.eba-ymyy8p6r.us-east-1.elasticbeanstalk.com/") //Set the Root URL
                .build(); //Finally building the adapter
        // http://Hotelreservation-env.eba-ttkjmbqa.us-east-1.elasticbeanstalk.com/hotelList

        //Creating object for our interface
        ApiInterface api = adapter.create(ApiInterface.class);
        return api; // return the APIInterface object
    }
}
