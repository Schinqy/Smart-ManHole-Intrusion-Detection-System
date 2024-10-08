package com.luitech.smids.network;

import com.luitech.smids.ControlRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ControlInterface {
    @POST("smids/ctrl_app.php") // Update to the correct PHP script name
    Call<ResponseBody> updateAutonomy(@Body ControlRequest request);
}
