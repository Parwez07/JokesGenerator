package com.example.retroassigment;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClinet {


    RetrofitApi retrofitApi;

    static  RetrofitClinet retrofitClinet;
    private static String BASE_URL="https://official-joke-api.appspot.com";

    RetrofitClinet(){
       Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitApi= retrofit.create(RetrofitApi.class);
    }

    public  static  RetrofitClinet getRetrofit(){
        if(retrofitClinet==null){
            retrofitClinet = new RetrofitClinet();
        }
        return retrofitClinet;
    }
}
