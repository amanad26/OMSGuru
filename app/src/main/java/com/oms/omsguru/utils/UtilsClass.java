package com.oms.omsguru.utils;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsClass {

    public static String channelId = "";
    public static int ErrorBeep = 2000;
    public static int SuccessBeep = 300;
    public static int SleepTime = 2000;
    public static int SleepTimeSuccess = 1300;

    public static void textBackgroundChange(EditText editText, LinearLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setBackgroundResource(R.drawable.edit_backgound);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static void alertDialog(Context context, int type, String title, String description) {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(context, type);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText(title).setContentText(description).setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .show();
    }


    public static void setUpSound(Context context, int time) {
        Handler handler = new Handler();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        SoundPool soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        int soundId = soundPool.load(context, R.raw.beep_success, 1);

        final int streamId = soundPool.play(soundId, 1, 1, 1, 0, 1.0f);

        // Stop the sound after 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.stop(streamId);
            }
        }, time);

    }

    public static String convertDateFormat(String originalDateStr) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-d");
        SimpleDateFormat outputFormat = new SimpleDateFormat("d-MM-yyyy");

        String convertedDateStr = null;
        try {
            // Parse the original date string into a Date object
            Date date = inputFormat.parse(originalDateStr);

            // Format the Date object into the new format
            convertedDateStr = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDateStr;
    }

}
