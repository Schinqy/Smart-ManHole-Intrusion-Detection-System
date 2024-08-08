package com.luitech.smids.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://lui.co.zw";
    private static Retrofit retrofit = null;

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static DataInterface getDataInterface() {
        return getRetrofitInstance().create(DataInterface.class);
    }

    public static CalendarInterface getCalendarInterface() {
        return getRetrofitInstance().create(CalendarInterface.class);
    }

    public static ResultsInterface getResultsInterface() {
        return getRetrofitInstance().create(ResultsInterface.class);
    }

    public static TimetableInterface getTimetableInterface() {
        return getRetrofitInstance().create(TimetableInterface.class);
    }

    public static AnnouncementInterface getAnnouncementsInterface() {
        return getRetrofitInstance().create(AnnouncementInterface.class);
    }

    public static AttendanceInterface getAttendanceInterface() {
        return getRetrofitInstance().create(AttendanceInterface.class);
    }

    public static FinancesInterface getFinancesInterface() {
        return getRetrofitInstance().create(FinancesInterface.class);
    }

    public static BlobInterface getBlobInterface() {
        return getRetrofitInstance().create(BlobInterface.class);
    }

    public static TokensInterface getTokensInterface() {
        return getRetrofitInstance().create(TokensInterface.class);
    }
}
