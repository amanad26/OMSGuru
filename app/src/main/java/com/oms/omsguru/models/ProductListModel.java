package com.oms.omsguru.models;

import java.io.Serializable;
import java.util.List;

public class ProductListModel implements Serializable {

    String skuName, rackSpace, zone, invoice = "", date, shipment_traker;
    String code, message;
    float qty;
    FetchDetailsModel.Result data;
    List<FetchDetailsModel.Result.Sku> skuList;

    public FetchDetailsModel.Result getData() {
        return data;
    }

    public void setData(FetchDetailsModel.Result data) {
        this.data = data;
    }

    public List<FetchDetailsModel.Result.Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<FetchDetailsModel.Result.Sku> skuList) {
        this.skuList = skuList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ProductListModel(String skuName, String rackSpace, String zone, float qty) {
        this.skuName = skuName;
        this.rackSpace = rackSpace;
        this.zone = zone;
        this.qty = qty;
    }

    public ProductListModel(String skuName, String rackSpace, String zone, float qty, String invoice, String date, List<FetchDetailsModel.Result.Sku> skuList, FetchDetailsModel.Result data, String shipment_traker) {
        this.skuName = skuName;
        this.rackSpace = rackSpace;
        this.zone = zone;
        this.qty = qty;
        this.invoice = invoice;
        this.date = date;
        this.skuList = skuList;
        this.data = data;
        this.shipment_traker = shipment_traker;
    }

    public ProductListModel(String skuName, String rackSpace, String zone, float qty, String invoice, String date, String shipment_traker, String code, String message) {
        this.skuName = skuName;
        this.rackSpace = rackSpace;
        this.zone = zone;
        this.qty = qty;
        this.invoice = invoice;
        this.date = date;
        this.shipment_traker = shipment_traker;
        this.code = code;
        this.message = message;

    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getShipment_traker() {
        return shipment_traker;
    }

    public void setShipment_traker(String shipment_traker) {
        this.shipment_traker = shipment_traker;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getRackSpace() {
        return rackSpace;
    }

    public void setRackSpace(String rackSpace) {
        this.rackSpace = rackSpace;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }
}
