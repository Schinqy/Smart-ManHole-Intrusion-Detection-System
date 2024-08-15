package com.luitech.smids.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {
    @FormUrlEncoded
    @POST("portal/login.php") // Specify your PHP script name
    Call<ResponseBody> loginUser(
            @Field("student_id") String studentId,
            @Field("password") String password
    );
}
