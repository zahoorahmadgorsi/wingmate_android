package com.app.wingmate.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefers {

    private static final String PREFS = "prefs_wing_mate";

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    public static void saveInteger(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInteger(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getFloat(key, defaultValue);
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getLong(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }

//    public static void saveUser(Context context, String key, User profile) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(profile);
//        editor.putString(key, json);
//        editor.apply();
//    }
//    public static User getUser(Context context, String key) {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = prefs.getString(key, null);
//        User userProfile = gson.fromJson(json, User.class);
//        return userProfile;
//    }
//
//    public static void saveCategoriesResponse(Context context, String key, CategoriesResponse data) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(data);
//        editor.putString(key, json);
//        editor.apply();
//    }
//    public static CategoriesResponse getCategoriesResponse(Context context, String key) {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = prefs.getString(key, null);
//        CategoriesResponse data = gson.fromJson(json, CategoriesResponse.class);
//        return data;
//    }

}