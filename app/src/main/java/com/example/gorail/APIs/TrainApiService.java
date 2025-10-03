package com.example.gorail.APIs;

import com.example.gorail.model.TrainRequest;
import com.example.gorail.model.TrainResponse;
import com.example.gorail.model.TrainRouteModel;
import com.example.gorail.model.TrainRouteResponse;
import com.example.gorail.model.TrainStatusItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrainApiService {

    @GET("/trains/getTrainOn")
    Call<TrainResponse> getTrainData(
            @Query("from") String from,
            @Query("to") String to,
            @Query("date") String date);

    @GET("trains/betweenStations")
    Call<TrainResponse> getTrainDataWithoutDate(
            @Query("from") String from,
            @Query("to") String to);

    @Headers("Content-Type: application/json")
    @POST("/check_availability")
    Call<TrainResponse> getTrainAvailability(@Body TrainRequest request);

        @GET("/train-route/{train_number}")
        Call<TrainRouteResponse> getTrainRoute(@Path("train_number") String trainNumber, @Query("fromStation") String fromStationCode, @Query("toStation") String toStationCode);

    @GET("/train-status")
    Call<List<TrainStatusItem>> getTrainStatus(
            @Query("train_number") String trainNumber,
            @Query("from_station") String fromStationCode,
            @Query("to_station") String toStationCode,
            @Query("date") String date
    );

}