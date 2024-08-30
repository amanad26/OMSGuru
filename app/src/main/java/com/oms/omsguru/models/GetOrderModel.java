package com.oms.omsguru.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public  class GetOrderModel implements Serializable {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("results")
    @Expose
    private Results results;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public class Results implements Serializable {

        @SerializedName("Invoice")
        @Expose
        private Invoice invoice;
        @SerializedName("Sku")
        @Expose
        private List<Sku> sku;

        public Invoice getInvoice() {
            return invoice;
        }

        public void setInvoice(Invoice invoice) {
            this.invoice = invoice;
        }

        public List<Sku> getSku() {
            return sku;
        }

        public void setSku(List<Sku> sku) {
            this.sku = sku;
        }

        public  class Sku implements Serializable {

            @Override
            public String toString() {
                return "Sku{" +
                        "isScanned=" + isScanned +
                        ", skuCode='" + skuCode + '\'' +
                        ", qty='" + qty + '\'' +
                        '}';
            }

            boolean isScanned = false;
            @SerializedName("sku_code")
            @Expose
            private String skuCode;
            @SerializedName("qty")
            @Expose
            private String qty;

            public String getSkuCode() {
                return skuCode;
            }

            public void setSkuCode(String skuCode) {
                this.skuCode = skuCode;
            }

            public String getQty() {
                return qty;
            }

            public void setQty(String qty) {
                this.qty = qty;
            }

            public boolean isScanned() {
                return isScanned;
            }

            public void setScanned(boolean scanned) {
                isScanned = scanned;
            }
        }

        public class Invoice implements Serializable {

            @SerializedName("channel_order_id")
            @Expose
            private String channelOrderId;
            @SerializedName("channel_sub_order_id")
            @Expose
            private String channelSubOrderId;
            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("shipment_tracker")
            @Expose
            private String shipment_tracker;

            public String getShipment_tracker() {
                return shipment_tracker;
            }

            public void setShipment_tracker(String shipment_tracker) {
                this.shipment_tracker = shipment_tracker;
            }

            public String getChannelOrderId() {
                return channelOrderId;
            }

            public void setChannelOrderId(String channelOrderId) {
                this.channelOrderId = channelOrderId;
            }

            public String getChannelSubOrderId() {
                return channelSubOrderId;
            }

            public void setChannelSubOrderId(String channelSubOrderId) {
                this.channelSubOrderId = channelSubOrderId;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

        }

    }


}
