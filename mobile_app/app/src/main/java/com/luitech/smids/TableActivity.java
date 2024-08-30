package com.luitech.smids;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luitech.smids.adapters.ManholeAdapter;
import com.luitech.smids.network.ApiClient;
import com.luitech.smids.network.DataInterface;
import com.luitech.smids.network.ManholeInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableActivity extends AppCompatActivity implements ManholeAdapter.OnManholeClickListener {
    private TableLayout tableLayout;
    private Handler handler;
    private Runnable updateTask;
    private RecyclerView recyclerView;
    private ManholeAdapter manholeAdapter;
    private List<Manhole> manholes = new ArrayList<>();
    private TextView deviceStatusTextView;
    private String mh_id;
    private String tableType;
    private AppCompatImageView deviceStatusIcon;
    private DeviceStatusChecker statusChecker;

    private TextView deviceIdTextView;

    private Button sideBarButton;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table);
        tableLayout = findViewById(R.id.tableLayout);
        deviceStatusTextView = findViewById(R.id.deviceStatus);

       deviceStatusIcon = findViewById(R.id.deviceStatusIcon);
       deviceIdTextView = findViewById(R.id.deviceId);
        statusChecker = new DeviceStatusChecker();


        sideBarButton = findViewById(R.id.btnShowGraphs);
        drawerLayout = findViewById(R.id.main);
        Intent intent = getIntent();
        mh_id = intent.getStringExtra("manhole_id");
        tableType = intent.getStringExtra("table_type");
        TextView tableHeading = findViewById(R.id.tableHeading);
        recyclerView = findViewById(R.id.recyclerViewManholes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        manholeAdapter = new ManholeAdapter(manholes, this);
        recyclerView.setAdapter(manholeAdapter);

        fetchManholes();
        handler = new Handler();
        updateTask = new Runnable() {
            @Override
            public void run() {
                fetchData(mh_id, tableType);

                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateTask);

        switch (tableType) {
            case "Motion":
                tableHeading.setText("Motion Detection Table");
                break;
            case "Water":
                tableHeading.setText("Water Table");
                break;
            default:
                tableHeading.setText("Manhole Cover Table");
                break;
        }
        sideBarButton.setOnClickListener(view ->
                drawerLayout.openDrawer(findViewById(R.id.navigationView)));
    }

    @Override
    public void onManholeClick(Manhole manhole) {
        // Handle manhole click here
       // Toast.makeText(this, "Selected: " + manhole.getName(), Toast.LENGTH_SHORT).show();
       mh_id = manhole.getName();
       deviceIdTextView.setText(mh_id);
       fetchData(mh_id, tableType);
        drawerLayout.closeDrawer(findViewById(R.id.navigationView));

    }

    private void fetchData(String manholeId, String dataType) {
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
                            TableLayout tableLayout = findViewById(R.id.tableLayout);

                            if (dataArray != null && dataArray.length() > 0) {
                                tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
                                JSONObject dataLatest = dataArray.getJSONObject((dataArray.length() - 1));
                                String timestampLatest = dataLatest.optString("timestamp");
                                Log.d("Latest Date", timestampLatest);
                                int dataLength = dataArray.length() - 1;
                                boolean isOnline = statusChecker.isDeviceOnline(timestampLatest);

                                if (isOnline) {
                                    deviceStatusTextView.setText("Device Online");
                                    deviceStatusIcon.setImageResource(R.drawable.ic_online);
                                } else {
                                    deviceStatusTextView.setText("Last Seen: " + timestampLatest);
                                    deviceStatusIcon.setImageResource(R.drawable.ic_offline);

                                }

                                for (int i = dataLength; i >= 0; i--) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    String timestampStr = data.optString("timestamp");

                                    String value = "";
                                    switch (dataType) {
                                        case "Water":
                                            value = data.optString("water_level").equals("1") ? "Level Full" : "Level Fine";
                                            break;
                                        case "Reed":
                                            value = data.optString("reed_status").equals("1") ? "Opened" : "Closed";
                                            break;
                                        case "Motion":
                                            value = data.optString("motion_detected").equals("1") ? "Detected" : "Not Detected";
                                            break;
                                    }

                                    addTableRow(timestampStr, value);
                                }
                            } else {
                                addTableRow("No data found", "");
                            }
                        } else {
                            addTableRow("Error: " + status, "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        addTableRow("Failed to parse data", "");
                    }
                } else {
                    addTableRow("Request failed", "");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                addTableRow("Failed to fetch data", "");
            }
        });
    }

    private void addTableRow(String timestamp, String status) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(8, 8, 8, 8);

        TextView timestampView = new TextView(this);
        timestampView.setText(timestamp);
        timestampView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        timestampView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        timestampView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        timestampView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_regular));

        TextView statusView = new TextView(this);
        statusView.setText(status);
        statusView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        statusView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        statusView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        statusView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_regular));

        row.addView(timestampView);
        row.addView(statusView);

        if (tableLayout.getChildCount() % 2 == 0) {
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.row_even));
        } else {
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.row_odd));
        }

        tableLayout.addView(row);
    }

    private void fetchManholes() {
        ManholeInterface manholeInterface = ApiClient.getManholeInterface();
        Call<ResponseBody> call = manholeInterface.getManhole();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String status = jsonObject.optString("status");

                        if ("success".equals(status)) {
                            JSONArray boardsArray = jsonObject.optJSONArray("boards");
                            List<Manhole> manholes = new ArrayList<>();

                            if (boardsArray != null) {
                                for (int i = 0; i < boardsArray.length(); i++) {
                                    JSONObject boardObject = boardsArray.getJSONObject(i);
                                    String id = boardObject.optString("id");
                                    String boardName = boardObject.optString("board");
                                    Manhole manhole = new Manhole(id, boardName);
                                    manholes.add(manhole);
                                }
                            }

                            // Update the RecyclerView with the new data
                            manholeAdapter.updateManholes(manholes);
                        } else {
                            Toast.makeText(TableActivity.this, "Error: " + status, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(TableActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TableActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TableActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
