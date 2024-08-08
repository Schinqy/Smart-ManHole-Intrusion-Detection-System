package com.prota.tcflportalv1.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AnnouncementInterface {
    @GET("portal/getAnnouncements.php") // Specify your PHP script name for retrieving calendar data
    Call<ResponseBody> getAnnouncements();
}
