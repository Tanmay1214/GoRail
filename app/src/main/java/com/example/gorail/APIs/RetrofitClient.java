package com.example.gorail.APIs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL_TRAIN = "https://indian-rail-api-pdil.onrender.com";
    private static final String BASE_URL_SEAT = "https://train-seat-scraper12.onrender.com";

    private static final String BASE_URL_STATUS = "https://your-train-status-api.onrender.com";
    private static Retrofit retrofitStatus = null;


    private static Retrofit retrofitTrain = null;
    private static Retrofit retrofitSeat = null;

    // Train APIs ke liye Retrofit Instance (Indian Rail API)
    public static Retrofit getClient() {
        if (retrofitTrain == null) {
            retrofitTrain = new Retrofit.Builder()
                    .baseUrl(BASE_URL_TRAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitTrain;
    }

    //  Seat Availability ke liye Retrofit (with logging, shared with route)
    public static Retrofit getSeatClient() {
        if (retrofitSeat == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .disableHtmlEscaping()
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofitSeat = new Retrofit.Builder()
                    .baseUrl(BASE_URL_SEAT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofitSeat;
    }

    // Route ke liye
    public static Retrofit getRouteClient() {
        return getSeatClient();  // Same instance reuse kar rahe
    }

    //  Train Status
    public static Retrofit getTrainStatusClient() {
        if (retrofitStatus == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .disableHtmlEscaping()
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofitStatus = new Retrofit.Builder()
                    .baseUrl(BASE_URL_STATUS)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofitStatus;
    }

}
