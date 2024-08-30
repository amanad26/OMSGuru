package com.oms.omsguru.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FetchDetailsModel implements Serializable {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("success")
    @Expose
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }


    public class Result implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("invoice_id")
        @Expose
        private String invoiceId;
        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("sub_order_id")
        @Expose
        private String subOrderId;
        @SerializedName("order_date")
        @Expose
        private String orderDate;
        @SerializedName("customer_name")
        @Expose
        private String customerName;
        @SerializedName("qty")
        @Expose
        private String qty;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("sku")
        @Expose
        private List<Sku> sku;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("days_since_shipped")
        @Expose
        private Integer daysSinceShipped;
        @SerializedName("settlement_amount")
        @Expose
        private String settlementAmount;
        @SerializedName("product_name")
        @Expose
        private String productName;
        @SerializedName("confirm_return_type")
        @Expose
        private Integer confirmReturnType;
        @SerializedName("return_reason")
        @Expose
        private String returnReason;
        @SerializedName("return_sub_reason")
        @Expose
        private String returnSubReason;
        @SerializedName("return_comments")
        @Expose
        private String returnComments;
        @SerializedName("return_type")
        @Expose
        private String returnType;
        @SerializedName("confirm_return_msg")
        @Expose
        private String confirmReturnMsg;
        @SerializedName("extra_field")
        @Expose
        private List<ExtraField> extraField;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getSubOrderId() {
            return subOrderId;
        }

        public void setSubOrderId(String subOrderId) {
            this.subOrderId = subOrderId;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public List<Sku> getSku() {
            return sku;
        }

        public void setSku(List<Sku> sku) {
            this.sku = sku;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getDaysSinceShipped() {
            return daysSinceShipped;
        }

        public void setDaysSinceShipped(Integer daysSinceShipped) {
            this.daysSinceShipped = daysSinceShipped;
        }

        public String getSettlementAmount() {
            return settlementAmount;
        }

        public void setSettlementAmount(String settlementAmount) {
            this.settlementAmount = settlementAmount;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getConfirmReturnType() {
            return confirmReturnType;
        }

        public void setConfirmReturnType(Integer confirmReturnType) {
            this.confirmReturnType = confirmReturnType;
        }

        public String getReturnReason() {
            return returnReason;
        }

        public void setReturnReason(String returnReason) {
            this.returnReason = returnReason;
        }

        public String getReturnSubReason() {
            return returnSubReason;
        }

        public void setReturnSubReason(String returnSubReason) {
            this.returnSubReason = returnSubReason;
        }

        public String getReturnComments() {
            return returnComments;
        }

        public void setReturnComments(String returnComments) {
            this.returnComments = returnComments;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public String getConfirmReturnMsg() {
            return confirmReturnMsg;
        }

        public void setConfirmReturnMsg(String confirmReturnMsg) {
            this.confirmReturnMsg = confirmReturnMsg;
        }

        public List<ExtraField> getExtraField() {
            return extraField;
        }

        public void setExtraField(List<ExtraField> extraField) {
            this.extraField = extraField;
        }

        public class Sku implements Serializable {

            @Override
            public String toString() {
                return "Sku{" +
                        "qty1=" + qty1 +
                        ", isGood=" + isGood +
                        ", skuCode='" + skuCode + '\'' +
                        ", qty='" + qty + '\'' +
                        '}';
            }

            int qty1 = 0 , qty2 = 0 ;

            boolean isGood = false;

            public boolean isGood() {
                return isGood;
            }

            public int getQty2() {
                return qty2;
            }

            public void setQty2(int qty2) {
                this.qty2 = qty2;
            }

            public void setGood(boolean good) {
                isGood = good;
            }

            @SerializedName("sku_code")
            @Expose
            private String skuCode;
            @SerializedName("qty")
            @Expose
            private String qty;

            @SerializedName("bad_accepted_qty")
            @Expose
            private String bad_accepted_qty;

            @SerializedName("good_accepted_qty")
            @Expose
            private String good_accepted_qty;

            @SerializedName("image_url")
            @Expose
            private String image_url;

            public String getBad_accepted_qty() {
                return bad_accepted_qty;
            }

            public void setBad_accepted_qty(String bad_accepted_qty) {
                this.bad_accepted_qty = bad_accepted_qty;
            }

            public String getGood_accepted_qty() {
                return good_accepted_qty;
            }

            public void setGood_accepted_qty(String good_accepted_qty) {
                this.good_accepted_qty = good_accepted_qty;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public int getQty1() {
                return qty1;
            }

            public void setQty1(int qty1) {
                this.qty1 = qty1;
            }

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

        }

        public class ExtraField implements Serializable {

            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("value")
            @Expose
            private String value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

        }


    }

}
