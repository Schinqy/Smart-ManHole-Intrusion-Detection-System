package com.luitech.smids;

import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.luitech.smids.network.ApiClient;
import com.luitech.smids.network.DataInterface;
import com.luitech.smids.network.ControlInterface;
import com.luitech.smids.ControlRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView waterLevelTextView;
    private TextView reedStatusTextView;
    private TextView motionDetectedTextView;
    private TextView deviceStatusTextView;

    private AppCompatImageView deviceStatusIcon;

    private Handler handler;
    private Runnable updateTask;
    private DeviceStatusChecker statusChecker;
    private SwitchMaterial controlSwitch;



    private String board_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        waterLevelTextView = findViewById(R.id.waterLevelStatus);
        reedStatusTextView = findViewById(R.id.reedStatus);
        motionDetectedTextView = findViewById(R.id.motionStatus);
        deviceStatusTextView = findViewById(R.id.deviceStatus);
        deviceStatusIcon = findViewById(R.id.deviceStatusIcon);

        statusChecker = new DeviceStatusChecker(); // Initialize statusChecker

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler();
        updateTask = new Runnable() {
            @Override
            public void run() {
                fetchData("MH_HRE0001"); // Replace with actual manhole_id
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateTask);

        controlSwitch = findViewById(R.id.switchOverride);
        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int state = isChecked ? 1 : 0;
               board_id = "MH_HRE0001";
                sendControlRequest(board_id, state); // Use actual board ID here

            }
        });
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

                                    waterLevelTextView.setText("Water Level: " + waterLevel);
                                    reedStatusTextView.setText("Reed Status: " + reedStatus);
                                    motionDetectedTextView.setText("Motion Detected: " + motionDetected);

                                    boolean isOnline = statusChecker.isDeviceOnline(timestamp);

                                    if (isOnline) {
                                        deviceStatusTextView.setText("Device Online");
                                        deviceStatusIcon.setImageResource(R.drawable.ic_online);
                                    } else {
                                        deviceStatusTextView.setText("Last Seen: " + timestamp);
                                        deviceStatusIcon.setImageResource(R.drawable.ic_offline);

                                    }
                                } else {
                                    waterLevelTextView.setText("No data found.");
                                }
                            } else {
                                waterLevelTextView.setText("No data found.");
                            }
                        } else {
                            waterLevelTextView.setText("Error: " + status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        waterLevelTextView.setText("Failed to parse data.");
                    }
                } else {
                    waterLevelTextView.setText("Request failed.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waterLevelTextView.setText("Failed to fetch data.");
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
}
