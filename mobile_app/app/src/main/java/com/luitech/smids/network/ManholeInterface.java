package com.luitech.smids.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ManholeInterface {
    @GET("smids/getManhole.php")
    Call<ResponseBody> getManhole();
}
