package com.luitech.smids;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.luitech.smids.network.ApiClient;
import com.luitech.smids.network.DataInterface;
import com.luitech.smids.network.ControlInterface;
import com.luitech.smids.ControlRequest;
import com.luitech.smids.network.GetStateInterface;
import com.luitech.smids.network.GpioInterface;
import com.luitech.smids.network.LocationInterface;
import com.luitech.smids.network.NotificationsInterface;
import com.luitech.smids.network.TokensInterface;
import com.luitech.smids.utils.ConfigUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;
import com.luitech.smids.adapters.NotificationAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView waterLevelTextView;
    private TextView reedStatusTextView;
    private TextView motionDetectedTextView;

    private TextView locationTextView;
    private TextView deviceStatusTextView;

    private AppCompatImageView deviceStatusIcon;

    private Handler handler;
    private Runnable updateTask;
    private DeviceStatusChecker statusChecker;
    private SwitchMaterial controlSwitch;
    private String board_id;


    private SwitchMaterial teargasSwitch;
    private SwitchMaterial alarmSwitch;
    private SwitchMaterial overrideSwitch;
    private boolean overrideState = false;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    private CardView motionCard;
    private CardView waterCard;
    private CardView reedCard;

    private String mh_id = "MH_HRE0001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_3);

        waterLevelTextView = findViewById(R.id.waterLevelStatus);
        reedStatusTextView = findViewById(R.id.reedStatus);
        motionDetectedTextView = findViewById(R.id.motionStatus);
        locationTextView = findViewById(R.id.location);
        deviceStatusTextView = findViewById(R.id.deviceStatus);
        deviceStatusIcon = findViewById(R.id.deviceStatusIcon);

        motionCard = findViewById(R.id.motionCard);
        waterCard = findViewById(R.id.waterCard);
        reedCard = findViewById(R.id.reedCard);

        motionCard.setOnClickListener(view -> {

            Intent intent = new Intent (MainActivity.this, TableActivity.class );
            intent.putExtra("table_type", "Motion");
            intent.putExtra("manhole_id", mh_id);
            startActivity(intent);

        });

        reedCard.setOnClickListener(view -> {
            Intent intent = new Intent (MainActivity.this, TableActivity.class );
            intent.putExtra("table_type", "Reed");
            intent.putExtra("manhole_id", mh_id);
            startActivity(intent);

        });
        waterCard.setOnClickListener(view -> {
            Intent intent = new Intent (MainActivity.this, TableActivity.class );
            intent.putExtra("table_type", "Water");
            intent.putExtra("manhole_id", mh_id);
            startActivity(intent);

        });


        statusChecker = new DeviceStatusChecker(); // Initialize statusChecker
        // Retrieve FCM token and send it to the server
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("MainActivity", "Fetching FCM registration token failed", task.getException());
                return;
            }

            String token = task.getResult();
            Log.d("TOKEN", "HERE IS THE TOKEN:" + token);
            sendTokenToServer(token);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


            // Initialize RecyclerView and Adapter
            recyclerView = findViewById(R.id.recyclerViewNotifications);


        if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<NotificationModel> notificationList = new ArrayList<>();
                adapter = new NotificationAdapter(notificationList);
                recyclerView.setAdapter(adapter);
            } else {
                Log.e("MainActivity", "RecyclerView is null. Check the ID in the XML layout.");
            }

            fetchNotifications(); // Load notifications from API


        handler = new Handler();
        updateTask = new Runnable() {
            @Override
            public void run() {
                fetchData(mh_id);
                fetchLocation("MH_HRE0001");
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateTask);

        controlSwitch = findViewById(R.id.switchOverride); //override Switch
        teargasSwitch = findViewById(R.id.switchTearGas);
        alarmSwitch = findViewById(R.id.switchAlarm);



        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int state = isChecked ? 1 : 0;
                board_id = "MH_HRE0001"; //
                sendControlRequest(board_id, state);
                overrideState = isChecked;
                if (isChecked) {
                    updateInitialSwitchStates(board_id); // Fetch and update switch states
                }
                updateSwitchState();


            }
        });


        teargasSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (overrideState) {
                updateGpioState("MH_HRE0001", "relay", isChecked ? 1 : 0);
            }
            else
            {

                teargasSwitch.setChecked(false); // Reset switch if override is off
                showToast("Override switch is off.");
            }
        });

        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked2) -> {
            if (overrideState)
            {
                updateGpioState("MH_HRE0001", "alarm", isChecked2 ? 1 : 0);
            } else {
                alarmSwitch.setChecked(false); // Reset switch if override is off
                showToast("Override switch is off.");
            }
        });
       updateSwitchState(); // Initialize switch states
    }

    private void updateSwitchState() {
        teargasSwitch.setEnabled(overrideState);
        alarmSwitch.setEnabled(overrideState);
    }

    private void sendControlRequest(String boardId, int state) {
        ControlInterface controlInterface = ApiClient.getControlInterface();
        ControlRequest request = new ControlRequest(boardId, state);
        Call<ResponseBody> call = controlInterface.updateGpioState(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Toast.makeText(MainActivity.this, responseBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Failed to read response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Request failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    String waterLevel = latestData.optString("water_level");
                                    String reedStatus = latestData.optString("reed_status");
                                    String motionDetected = latestData.optString("motion_detected");
                                    String timestamp = latestData.optString("timestamp");

                                    if (waterLevel.equals("1"))
                                    {
                                        waterLevelTextView.setText("Level Full");
                                    }
                                    else if (waterLevel.equals("0"))
                                    {
                                        waterLevelTextView.setText("Level Fine");
                                    }
                                   else
                                   {
                                       waterLevelTextView.setText("Uknown");
                                    }

                                   if(reedStatus.equals("1"))
                                   {
                                       reedStatusTextView.setText("Opened");
                                   }
                                   else if(reedStatus.equals("0"))
                                   {
                                       reedStatusTextView.setText("Closed");
                                   }
                                   else
                                   {
                                       reedStatusTextView.setText("Uknown");
                                   }

                                       if(motionDetected.equals("1"))
                                       {
                                           motionDetectedTextView.setText("Detected");
                                       }
                                    else if(motionDetected.equals("0"))
                                    {
                                        motionDetectedTextView.setText("Not Detected");
                                    }
                                    else
                                    {
                                        motionDetectedTextView.setText("Uknown");
                                    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(updateTask);
        }
    }


    private void updateGpioState(String boardId, String name, int state) {
        GpioInterface gpioInterface = ApiClient.getGpioInterface();

        try {
            // Read API key from file
            String apiKey = ConfigUtils.getApiKey(getApplicationContext());

            // Create request object
            GpioRequest request = new GpioRequest(boardId, name, state, apiKey);

            // Make the request
            Call<ResponseBody> call = gpioInterface.updateGpioState(request);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        showToast("GPIO state updated successfully.");
                    } else {
                        showToast("Failed to update GPIO state: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showToast("Request failed: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Failed to create request object.");
        }
    }

    private void showToast(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }


    private void updateInitialSwitchStates(String manholeId) {
        GetStateInterface getStateInterface = ApiClient.getStateInterface();

        Call<ResponseBody> call = getStateInterface.getState(manholeId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);
                        Log.d("GET STATES", responseBody);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            int state = jsonObject.getInt("state");

                            if ("relay".equals(name)) {
                                teargasSwitch.setChecked(state == 1);
                            } else if ("alarm".equals(name)) {
                                alarmSwitch.setChecked(state == 1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Failed to parse data.");
                    }
                } else {
                    showToast("Failed to fetch state.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Request failed: " + t.getMessage());
            }
        });
    }


    // Example usage
    private void sendTokenToServer(String token) {
        TokensInterface tokensInterface = ApiClient.getTokensInterface(); // Make sure this method returns an instance of TokensInterface
        TokensInterface.TokenRequest tokenRequest = new TokensInterface.TokenRequest(token);

        Call<Void> call = tokensInterface.storeToken(tokenRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", "Token stored successfully");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("MainActivity", "Error storing token: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "Error reading error body.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
            }
        });
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

    private void fetchLocation(String manhole_id) {
        LocationInterface locationInterface = ApiClient.getLocationInterface();
        Call<ResponseBody> call = locationInterface.getLocation(manhole_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Read the response body
                        String responseBody = response.body().string();
                        Log.d("API Response", responseBody); // Log the response for debugging

                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String loc = jsonObject.getString("location");

                        // Split location string into latitude and longitude
                        String[] coordinates = loc.split(",");
                        if (coordinates.length == 2) {
                            String latitude = coordinates[0].trim();
                            String longitude = coordinates[1].trim();
                            String mapsUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;

                            // Set the text and make it clickable
                            SpannableString spannableString = new SpannableString("Location: (" + loc + ")");
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                                    startActivity(intent);
                                }
                            };
                            // Set the color for the clickable span
                            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(clickableSpan, "Location: ".length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);



                            locationTextView.setText(spannableString);

                            locationTextView.setMovementMethod(LinkMovementMethod.getInstance());

                        } else {
                            showToast("Invalid location format.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Failed to parse data.");
                    }
                } else {
                    showToast("Failed to fetch data.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Request failed: " + t.getMessage());
            }
        });
    }





}


