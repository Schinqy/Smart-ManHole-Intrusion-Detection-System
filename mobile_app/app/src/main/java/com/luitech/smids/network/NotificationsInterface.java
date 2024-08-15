package com.luitech.smids.network;

import com.luitech.smids.NotificationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NotificationsInterface {
    @GET("smids/getNotifications.php")
    Call<List<NotificationModel>> getNotifications();
}
