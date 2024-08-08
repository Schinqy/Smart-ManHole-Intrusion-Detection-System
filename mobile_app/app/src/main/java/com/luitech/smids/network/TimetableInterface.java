package com.prota.tcflportalv1.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TimetableInterface {
    @GET("portal/getTimetable.php")
    Call<ResponseBody> getTimetable(@Query("department") String department);
}
