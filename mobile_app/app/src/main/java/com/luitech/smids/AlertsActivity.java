package com.luitech.smids;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luitech.smids.adapters.ManholeAdapter;
import com.luitech.smids.adapters.NotificationAdapter;
import com.luitech.smids.network.ApiClient;
import com.luitech.smids.network.DataInterface;
import com.luitech.smids.network.ManholeInterface;
import com.luitech.smids.network.NotificationsInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertsActivity extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private Handler handler;
    private Runnable updateTask;
    private DeviceStatusChecker statusChecker;
    private String mh_id = "MH_HRE0001";

    private TextView locationTextView;
    private TextView deviceStatusTextView;

    private AppCompatImageView deviceStatusIcon;
    private Button sideBarButton;
    private DrawerLayout drawerLayout;
    private TextView deviceId;


    private ManholeAdapter manholeAdapter;
    private List<Manhole> manholes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alerts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        deviceStatusTextView = findViewById(R.id.deviceStatus);
        deviceStatusIcon = findViewById(R.id.deviceStatusIcon);
        deviceId = findViewById(R.id.deviceId);

        sideBarButton = findViewById(R.id.btnShowGraphs);
        drawerLayout = findViewById(R.id.main);




        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<NotificationModel> notificationList = new ArrayList<>();
            adapter = new NotificationAdapter(notificationList);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("MainActivity", "RecyclerView is null. Check the ID in the XML layout.");
        }
        statusChecker = new DeviceStatusChecker(); // Initialize statusChecker
        fetchNotifications(); // Load notifications from API



        handler = new Handler();
        updateTask = new Runnable() {
            @Override
            public void run() {
                fetchNotifications();
                fetchData(mh_id);
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateTask);
    }

    private void fetchNotifications() {
        NotificationsInterface notificationsInterface = ApiClient.getNotificationsInterface();
        Call<List<NotificationModel>> call = notificationsInterface.getNotifications();

        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationModel> notifications = response.body();
                    // Log the notifications to check the data
                    for (NotificationModel notification : notifications) {
                        Log.d("Notification", "Heading: " + notification.getBoardId() + ", Message: " + notification.getText() + ", Timestamp: " + notification.getTimestamp());
                    }
                    adapter = new NotificationAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("NotificationsActivity", "Error fetching notifications: " + response.message());
                }
            }


            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e("NotificationsActivity", "Failure: " + t.getMessage());
            }
        });
    }



    private void fetchData(String manholeId) {
        DataInterface dataInterface = ApiClient.getDataInterface();
        Call<ResponseBody> call = dataInterface.getData(manholeId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        // Log.d("DATA RECEIVED", "Data Received" + responseBody);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String status = jsonObject.optString("status");

                        if ("success".equals(status)) {
                            JSONArray dataArray = jsonObject.optJSONArray("data");
                            if (dataArray != null && dataArray.length() > 0) {
                                JSONObject latestData = null;
                                long latestTimestamp = Long.MIN_VALUE;

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    String timestampStr = data.optString("timestamp");

                                    long timestamp;
                                    try {
                                        Date date = dateFormat.parse(timestampStr);
                                        if (date != null) {
                                            timestamp = date.getTime();
                                        } else {
                                            continue;
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        continue;
                                    }

                                    if (timestamp > latestTimestamp) {
                                        latestTimestamp = timestamp;
                                        latestData = data;
                                    }
                                }

                                if (latestData != null) {
                                    String timestamp = latestData.optString("timestamp");
                                    boolean isOnline = statusChecker.isDeviceOnline(timestamp);

                                    if (isOnline) {
                                        deviceStatusTextView.setText("Device Online");
                                        deviceStatusIcon.setImageResource(R.drawable.ic_online);
                                    } else {
                                        deviceStatusTextView.setText("Last Seen: " + timestamp);
                                        deviceStatusIcon.setImageResource(R.drawable.ic_offline);

                                    }
                                } else {
                                    locationTextView.setText("No data found.");
                                }
                            } else {
                                locationTextView.setText("No data found.");
                            }
                        } else {
                            locationTextView.setText("Error: " + status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        locationTextView.setText("Failed to parse data.");
                    }
                } else {
                    locationTextView.setText("Request failed.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                locationTextView.setText("Failed to fetch data.");
            }
        });
    }


}