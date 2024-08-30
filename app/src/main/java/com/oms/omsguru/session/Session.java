package com.oms.omsguru.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.oms.omsguru.getStartedActivities.LoginActivity;
import com.oms.omsguru.models.VerifyOtpModel;

import java.util.List;

public class Session {


    private static final String PREF_NAME = "Rapidine_pref2";

    private final String warehouse = "warehouse";
    private Context _context;
    private SharedPreferences Rapidine_pref;
    private SharedPreferences.Editor editor;
    private final String userId = "userid";
    private final String warehouseId = "warehouseId";
    private final String companyId = "companyId";
    private final String isLogin = "isLogin ";
    private final String warehouseName = "warehouseName";
    private final String email = "email";
    private final String auth_code = "auth_code";
    private final String name = "name";
    private final String mobile = "mobile";
    private final String p1 = "p1";
    private final String p2 = "p2";
    private final String p3 = "p3";
    private final String p4 = "p4";
    private final String permission_code = "permission_code";

    public Session(Context context) {
        this._context = context;
        Rapidine_pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = Rapidine_pref.edit();
        editor.apply();
    }

    public void setAuth_code(String uid) {
        editor.putString(auth_code, uid);
        editor.commit();
    }

    public void setName(String uid) {
        editor.putString(name, uid);
        editor.commit();
    }


    public void setP1(String uid) {
        editor.putString(p1, uid);
        editor.commit();
    }


    public void setP2(String uid) {
        editor.putString(p2, uid);
        editor.commit();
    }

    public void setP3(String uid) {
        editor.putString(p3, uid);
        editor.commit();
    }

    public void setP4(String uid) {
        editor.putString(p4, uid);
        editor.commit();
    }

    public void setMobile(String uid) {
        editor.putString(mobile, uid);
        editor.commit();
    }

    public void setPermission_code(String uid) {
        editor.putString(permission_code, uid);
        editor.commit();
    }

    public void setUserId(String uid) {
        editor.putString(userId, uid);
        editor.commit();
    }

    public void setEmail(String uid) {
        editor.putString(email, uid);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return Rapidine_pref.getBoolean(isLogin, false);
    }

    public void setWarehouseId(String uid) {
        editor.putString(warehouseId, uid);
        editor.commit();
    }

    public void setWarehouseName(String uid) {
        editor.putString(warehouseName, uid);
        editor.commit();
    }

    public void setCompanyId(String uid) {
        editor.putString(companyId, uid);
        editor.commit();
    }

    public String getWarehouseId() {
        return Rapidine_pref.getString(warehouseId, "");
    }

    public String getMobile() {
        return Rapidine_pref.getString(mobile, "");
    }

    public String getP1() {
        return Rapidine_pref.getString(p1, "0");
    }

    public String getP2() {
        return Rapidine_pref.getString(p2, "0");
    }

    public String getP3() {
        return Rapidine_pref.getString(p3, "0");
    }

    public String getP4() {
        return Rapidine_pref.getString(p4, "0");
    }

    public String getName() {
        return Rapidine_pref.getString(name, "");
    }

    public String getEmail() {
        return Rapidine_pref.getString(email, "");
    }

    public String getWarehouseName() {
        return Rapidine_pref.getString(warehouseName, "");
    }

    public String getCompanyId() {
        return Rapidine_pref.getString(companyId, "");
    }

    public String getAuth_code() {
        return Rapidine_pref.getString(auth_code, "");
    }

    public String getPermission_code() {
        return Rapidine_pref.getString(permission_code, "");
    }

    public String getUserId() {
        return Rapidine_pref.getString(userId, "");
    }

    public void saveVerifyModel(VerifyOtpModel.Results user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(warehouse, userJson);
        editor.apply();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(isLogin, isLoggedIn);
        editor.commit();
    }

    public VerifyOtpModel.Results getVerifyModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = sharedPreferences.getString(warehouse, null);
        return gson.fromJson(userJson, VerifyOtpModel.Results.class);
    }

    public void logout() {
        editor.clear();
        editor.apply();
        Intent showLogin = new Intent(_context, LoginActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(showLogin);
    }

}
