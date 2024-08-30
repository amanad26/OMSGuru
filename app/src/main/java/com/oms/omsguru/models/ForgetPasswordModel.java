package com.oms.omsguru.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForgetPasswordModel {

    @SerializedName("message")
    @Expose
    private String message;


    @SerializedName("success")
    @Expose
    private boolean success;


    @SerializedName("results")
    @Expose
    private List<Object> results;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }

}
