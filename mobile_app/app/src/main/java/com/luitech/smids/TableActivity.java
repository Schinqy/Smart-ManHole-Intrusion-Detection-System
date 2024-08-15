package com.luitech.smids;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.luitech.smids.network.ApiClient;
import com.luitech.smids.network.DataInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Locale;

public class TableActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private Handler handler;
    private Runnable updateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table);
        tableLayout = findViewById(R.id.tableLayout);

        Intent intent = getIntent();
        String manholeId = intent.getStringExtra("manhole_id");
        String table_type = intent.getStringExtra("table_type");
        TextView table_heading = findViewById(R.id.tableHeading);
        handler = new Handler();
        updateTask = new Runnable() {
            @Override
            public void run() {
                fetchData(manholeId, table_type);

                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateTask);

        if(table_type.equals("Motion")){

       table_heading.setText("Motion Detection Table");
        }
        else if(table_type.equals("Water"))
        {
            table_heading.setText("Water Table");
        }
        else
        {
            table_heading.setText("Manhole Cover Table");
        }
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
                                // Clear existing rows except the header
                                tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

                                for (int i = 0; i < dataArray.length(); i++) {
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

        // Add alternating background colors for better readability
        if (tableLayout.getChildCount() % 2 == 0) {
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.row_even));
        } else {
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.row_odd));
        }

        tableLayout.addView(row);
    }



}