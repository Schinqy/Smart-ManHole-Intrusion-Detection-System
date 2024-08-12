package com.luitech.smids.network;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetStateInterface {
    @GET("smids/getState.php")

    Call<ResponseBody> getState(@Query("board") String manholeId);
}
