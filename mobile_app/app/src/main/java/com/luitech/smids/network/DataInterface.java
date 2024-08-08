package com.luitech.smids.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataInterface {
    @GET("smids/getData.php")

    Call<ResponseBody> getData(@Query("manhole_id") String manholeId);
}
