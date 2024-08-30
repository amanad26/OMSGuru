package com.oms.omsguru.apis;

import com.oms.omsguru.models.DispatchListModel;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.ForgetPasswordModel;
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.models.ListShippedOrderModel;
import com.oms.omsguru.models.LoginModel;
import com.oms.omsguru.models.PresignedUrlModel;
import com.oms.omsguru.models.ReturnModel;
import com.oms.omsguru.models.VerifyOtpModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST(BaseUrls.LOGIN)
    Call<VerifyOtpModel> login(
            @Field("data[command]") String command,
            @Field("data[Client][email]") String email,
            @Field("data[Client][password]") String password

    );

    @FormUrlEncoded
    @POST(BaseUrls.VERIFY_OTP)
    Call<VerifyOtpModel> verifyOtp(
            @Field("data[command]") String command,
            @Field("data[Client][email]") String email,
            @Field("data[Client][password]") String password,
            @Field("data[Client][otp]") String otp

    );

    @FormUrlEncoded
    @POST(BaseUrls.FORGET_PASSWORD)
    Call<ForgetPasswordModel> forgetPassword(
            @Field("data[command]") String command,
            @Field("data[Client][email]") String email

    );

    @FormUrlEncoded
    @POST(BaseUrls.FETCH_DETAIL)
    Call<ReturnModel> fetchDetails(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[Client][shipment_tracker]") String shipment_tracker,
            @Field("data[Client][channel_id]") String channel_id

    );

    @FormUrlEncoded
    @POST(BaseUrls.FETCH_DETAIL)
    Call<FetchDetailsModel> fetchDetailsNew(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[Client][shipment_tracker]") String shipment_tracker,
            @Field("data[Client][channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update

    );

    @FormUrlEncoded
    @POST(BaseUrls.SHIP_DETAIL)
    Call<FetchDetailsModel> shipOrder(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[Client][shipment_tracker]") String shipment_tracker,
            @Field("data[Client][channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update
    );

    @FormUrlEncoded
    @POST(BaseUrls.SHIP_DETAIL)
    Call<DispatchListModel> getDispatchList(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[Client][channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update

    );


    @FormUrlEncoded
    @POST(BaseUrls.SHIP_DETAIL)
    Call<ListShippedOrderModel> getShippedList(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[Client][channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update

    );



    @FormUrlEncoded
    @POST(BaseUrls.SHIP_DETAIL)
    Call<DispatchListModel> dispatchOrders(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[shipment_trackers]") String shipment_trackers,
            @Field("data[channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update

    );

    @FormUrlEncoded
    @POST(BaseUrls.VALIDATE_PACK_DETAIL)
    Call<GetOrderModel> getInvoiceDetails(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[invoice_number]") String invoice_number,
            @Field("data[channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update
    );

    @FormUrlEncoded
    @POST(BaseUrls.VALIDATE_PACK_DETAIL)
    Call<GetOrderModel> savePackLoag(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[invoice_id]") String invoice_id,
            @Field("data[channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update
    );

    @FormUrlEncoded
    @POST(BaseUrls.VALIDATE_PACK_DETAIL)
    Call<GetOrderModel> processReturnAccept(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[return_type]") String return_type,
            @Field("data[Sku]") String Sku,
            @Field("data[shipment_tracker]") String shipment_tracker,
            @Field("data[invoice_id]") String invoice_id,
            @Field("data[confirm_return_type]") String confirm_return_type,
            @Field("data[channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update
    );


    @FormUrlEncoded
    @POST(BaseUrls.VALIDATE_PACK_DETAIL)
    Call<PresignedUrlModel> getPresignedUrl(
            @Field("data[command]") String command,
            @Field("data[warehouse_id]") String warehouse_id,
            @Field("data[company_id]") String company_id,
            @Field("data[invoice_id]") String invoice_id,
            @Field("data[ext]") String ext,
            @Field("data[channel_id]") String channel_id,
            @Field("data[Client][auth_user_id]") String auth_user_id,
            @Field("data[Client][auth_code]") String auth_code,
            @Field("data[Client][last_permission_update]") String last_permission_update
    );


}
