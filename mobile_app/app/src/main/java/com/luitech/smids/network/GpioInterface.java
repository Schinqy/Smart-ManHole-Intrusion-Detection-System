package com.luitech.smids.network;

import com.luitech.smids.GpioRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GpioInterface {
    @POST("smids/updateState.php")
    Call<ResponseBody> updateGpioState(@Body GpioRequest request);
}
