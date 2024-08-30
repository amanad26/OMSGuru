package com.oms.omsguru.utils;

import static com.oms.omsguru.return_processing.UploadFileActivity.getMimeTypeFromFile;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

    public static void copyFileToDownloads(Context context, File sourceFile, String fileName) {
        // Prepare content values for the new file in Downloads
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName); // Name of the file
        values.put(MediaStore.Downloads.MIME_TYPE, getMimeTypeFromFile(sourceFile)); // MIME type, adjust as needed
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS); // Path in Downloads folder

        // Insert the new file into the Downloads directory via MediaStore
        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        }

        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                 FileInputStream inputStream = new FileInputStream(sourceFile)) {

                byte[] buffer = new byte[1024];
                int length;

                // Copy data from the source file to the new location
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                Toast.makeText(context, "File Downloaded..", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error copying file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Failed to create new MediaStore record.", Toast.LENGTH_LONG).show();
        }
    }
}
