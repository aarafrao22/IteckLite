package com.itecknologigroupofcompanies.itecklite;

public class OTPResponseModel {
    String Success,Message;

    public OTPResponseModel(String success, String message) {
        Success = success;
        Message = message;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
