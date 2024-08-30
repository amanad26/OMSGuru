package com.oms.omsguru.utils;

public class SelectedSkuModel {
    String sku, good = "0", bad = "0";

    public SelectedSkuModel(String sku, String good, String bad) {
        this.sku = sku;
        this.good = good;
        this.bad = bad;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }


    @Override
    public String toString() {
        return "SelectedSkuModel{" +
                "sku='" + sku + '\'' +
                ", good='" + good + '\'' +
                ", bad='" + bad + '\'' +
                '}';
    }
}
