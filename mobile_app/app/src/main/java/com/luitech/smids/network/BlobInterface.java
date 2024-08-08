package com.prota.tcflportalv1.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BlobInterface {
    @GET("portal/getDP.php")
    Call<ResponseBody> getDP(@Query("student_id") String student_id);
}
