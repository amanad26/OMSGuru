package com.oms.omsguru.models;

import java.io.File;

public class FileModel {

    String key;
    String name;
    File file;
    boolean isDeleted = false;
    boolean isVideo = false;

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public FileModel(String key, String name, File file) {
        this.key = key;
        this.name = name;
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    @Override
    public String toString() {
        return "FileModel{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", file=" + file +
                ", isDeleted=" + isDeleted +
                ", isVideo=" + isVideo +
                '}';
    }
}
