package com.prota.tcflportalv1.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface TokensInterface {
    @FormUrlEncoded
    @POST("portal/storeTokens.php")
    Call<Void> storeToken(@Field("token") String token);
}


