package com.example.gorail.model;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class TrainRequest {
    @SerializedName("cls")
    private String cls;
    @SerializedName("trainNo")
    private String trainNo;
    @SerializedName("quotaSelectdd")
    private String quota;
    @SerializedName("fromstation")
    private String from;
    @SerializedName("tostation")
    private String to;
    @SerializedName("e")
    private String e;
    @SerializedName("lstSearch")
    private Map<String, String> lstSearch;
    @SerializedName("Searchsource")
    private String searchSource;
    @SerializedName("Searchdestination")
    private String searchDestination;

    public TrainRequest(String cls, String trainNo, String quota, String from, String to, String e, Map<String, String> lstSearch, String searchSource, String searchDestination) {
        this.cls = cls;
        this.trainNo = trainNo;
        this.quota = quota;
        this.from = from;
        this.to = to;
        this.e = e;
        this.lstSearch = lstSearch;
        this.searchSource = searchSource;
        this.searchDestination = searchDestination;
    }
}
