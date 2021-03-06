package com.itecknologigroupofcompanies.itecklite;

import org.w3c.dom.Comment;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @POST("DataModal")

        //on below line we are creating a method to post our data.
    Call<DataModal> createPost(@Body DataModal dataModal);

    @FormUrlEncoded
    @POST("DataModel")
    Call<Comment> createComment(@Field("device_id") String device_id, @Field("Email") String Email, @Field("Contact") String Contact, @Field("FcmToken") String FcmToken);

    @FormUrlEncoded
    @POST("DataModel")
    Call<DataModal> createComment(@FieldMap Map<String, String> fields);


    @FormUrlEncoded
    @POST("OTPResponseModel")
    Call<OTPResponseModel> checkOTP(
            @Field("login_id") String login_id,
            @Field("OTP") String OTP,
            @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("UpdatedFCMResponse")
    Call<UpdatedFCMResponse> sendUpdatedFCM(
            @Field("loginid") String loginid,
            @Field("deviceid") String deviceid,
            @Field("fcmtoken") String updatedFCM);


    @FormUrlEncoded
    @POST("ResponseModel")
    Call<ResponseModel> getCarDataList(@Field("contact") String contactNo);


    @FormUrlEncoded
    @POST("SelectedVehicleResponseModel")
    Call<SelectedVehicleResponseModel> getSingleCarData(@Field("veh_id") String selectedVehicle, @Field("object_id") String selectedVehicleObjId);

}


