package ru.smartcity.models;

import androidx.annotation.NonNull;

import java.util.Map;

public class ServerResponse {

    private String success;
    private Map<String, String> data;
    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("success: " + success + "\n");
        stringBuilder.append("data: " + data.toString() + "\n");
        stringBuilder.append("message: " + message);
        return "success: " + this.success +
                "data: " + this.data.toString() +
                "message: " + this.data;
    }
}
