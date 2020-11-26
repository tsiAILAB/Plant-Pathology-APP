package com.tsi.plantdiagnosissystem.data.model;

public class ResultFeedback {
    int id;
    String userName;
    String plantName;
    String feedBackResult;

    public ResultFeedback(String userName, String plantName, String feedBackResult) {
        this.userName = userName;
        this.plantName = plantName;
        this.feedBackResult = feedBackResult;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getFeedBackResult() {
        return feedBackResult;
    }

    public void setFeedBackResult(String feedBackResult) {
        this.feedBackResult = feedBackResult;
    }
}
