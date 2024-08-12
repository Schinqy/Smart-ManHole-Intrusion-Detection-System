package com.luitech.smids;
import android.content.Context;
import android.content.res.Resources;
import org.json.JSONObject;
import java.io.InputStream;

public class ApiKeyManager {

    private static String apiKey;

    public static void initialize(Context context) {
        try {
            Resources res = context.getResources();
            InputStream inputStream = res.openRawResource(R.raw.config); // R.raw.config refers to res/raw/config.json
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            apiKey = jsonObject.getString("api_key");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getApiKey() {
        return apiKey;
    }
}
