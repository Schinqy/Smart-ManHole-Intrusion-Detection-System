package com.prota.tcflportalv1.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ResultsInterface {
    @GET("portal/getResults.php")
    Call<ResponseBody> getResults(@Query("student_id") String studentId);
}
