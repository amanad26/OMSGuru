package com.oms.omsguru.utils;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCompressor {
    Context context;
    ProgressDialog pd;

    public VideoCompressor(Context context) {
        this.context = context;
        //pd = new ProgressDialog(context);
    }

    public boolean compressVideo(String inputPath, String outputPath) {
        //pd.show();
        String command = String.format("-i %s -vf scale=640:480 -b:v 1M -an %s", inputPath, outputPath);
//        String command = String.format("-i %s -vf scale=640:480 -b:v 1M %s", inputPath, outputPath);
//        String[] command = {"-i", inputPath, "-vcodec", "libx264", "-crf", "23", "-an", outputPath};

        int resultCode = FFmpeg.execute(command);

        if (resultCode == 0) {
            // Success
            Log.i("TAG", "Video compression successful!");
            //pd.dismiss();
            //Toast.makeText(context, "Compressing...", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // Failure
            //pd.dismiss();
            //Toast.makeText(context, "Failed..", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "Video compression failed with return code " + resultCode);
            return false;
        }
    }


    public void compressImage() {
        // Source image path
        String inputPath = "/path/to/input/image.jpg";
        // Compressed image path
        String outputPath = "/path/to/output/compressed_image.jpg";

        // FFmpeg command to compress the image without losing quality
        String[] cmd = {"-i", inputPath, "-q:v", "2", outputPath};

        // Execute the command
        int rc = FFmpeg.execute(cmd);

        if (rc == Config.RETURN_CODE_SUCCESS) {
            Log.e("TAG", "Video compression successful!");
        } else {
            Log.e("TAG", "Video compression failed!");
        }
    }
}
