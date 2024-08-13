package com.luitech.smids.network;

import com.luitech.smids.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NotificationsInterface {
    @GET("smids/getNotifications.php") // Replace with your actual path
    Call<List<Notification>> getNotifications();
}
