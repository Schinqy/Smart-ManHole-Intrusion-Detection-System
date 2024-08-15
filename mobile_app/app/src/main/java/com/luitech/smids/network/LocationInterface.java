package com.luitech.smids.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationInterface {
    @GET("smids/getLocation.php")
    Call<ResponseBody> getLocation(@Query("manhole_id") String manholeId);
}
