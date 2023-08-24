package com.example.retroassigment;

import java.util.*;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitApi {

    @GET("random_joke")
      Call<ModelClass> getModelClass();

}
