package com.oms.omsguru.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VerifyOtpModel implements Serializable {

    @SerializedName("results")
    @Expose
    private Results results = null;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("code")
    @Expose
    private Integer code;

    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }


    public class Results implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("name")
        @Expose
        private String name = null;
        @SerializedName("mobile")
        @Expose
        private String mobile = null;

        @SerializedName("auth_code")
        @Expose
        private String authCode;
        @SerializedName("channels")
        @Expose
        private List<Channel> channels;
        @SerializedName("companies")
        @Expose
        private List<Company> companies;
        @SerializedName("warehouses")
        @Expose
        private List<Warehouse> warehouses;
        @SerializedName("add_template")
        @Expose
        private Boolean addTemplate;
        @SerializedName("currency_symbol")
        @Expose
        private String currencySymbol;
        @SerializedName("claimTemplates")
        @Expose
        private List<ClaimTemplate> claimTemplates;
        @SerializedName("last_permission_update")
        @Expose
        private Integer lastPermissionUpdate;
        @SerializedName("permissions")
        @Expose
        private List<String> permissions;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public List<Channel> getChannels() {
            return channels;
        }

        public void setChannels(List<Channel> channels) {
            this.channels = channels;
        }

        public List<Company> getCompanies() {
            return companies;
        }

        public void setCompanies(List<Company> companies) {
            this.companies = companies;
        }

        public List<Warehouse> getWarehouses() {
            return warehouses;
        }

        public void setWarehouses(List<Warehouse> warehouses) {
            this.warehouses = warehouses;
        }

        public Boolean getAddTemplate() {
            return addTemplate;
        }

        public void setAddTemplate(Boolean addTemplate) {
            this.addTemplate = addTemplate;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public List<ClaimTemplate> getClaimTemplates() {
            return claimTemplates;
        }

        public void setClaimTemplates(List<ClaimTemplate> claimTemplates) {
            this.claimTemplates = claimTemplates;
        }

        public Integer getLastPermissionUpdate() {
            return lastPermissionUpdate;
        }

        public void setLastPermissionUpdate(Integer lastPermissionUpdate) {
            this.lastPermissionUpdate = lastPermissionUpdate;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }

        public class Channel implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("send_to")
            @Expose
            private String sendTo;
            @SerializedName("max_photos")
            @Expose
            private Integer maxPhotos;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSendTo() {
                return sendTo;
            }

            public void setSendTo(String sendTo) {
                this.sendTo = sendTo;
            }

            public Integer getMaxPhotos() {
                return maxPhotos;
            }

            public void setMaxPhotos(Integer maxPhotos) {
                this.maxPhotos = maxPhotos;
            }


            @Override
            public String toString() {
                return "Channel{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", sendTo='" + sendTo + '\'' +
                        ", maxPhotos=" + maxPhotos +
                        '}';
            }
        }

        public class ClaimTemplate implements Serializable {

            @SerializedName("template_name")
            @Expose
            private String templateName;
            @SerializedName("template_title")
            @Expose
            private String templateTitle;
            @SerializedName("template_details")
            @Expose
            private String templateDetails;

            public String getTemplateName() {
                return templateName;
            }

            public void setTemplateName(String templateName) {
                this.templateName = templateName;
            }

            public String getTemplateTitle() {
                return templateTitle;
            }

            public void setTemplateTitle(String templateTitle) {
                this.templateTitle = templateTitle;
            }

            public String getTemplateDetails() {
                return templateDetails;
            }

            public void setTemplateDetails(String templateDetails) {
                this.templateDetails = templateDetails;
            }

        }

        public class Warehouse implements Serializable {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }

        public class Company implements Serializable {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }


        @Override
        public String toString() {
            return "Results{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", authCode='" + authCode + '\'' +
                    ", channels=" + channels +
                    ", companies=" + companies +
                    ", warehouses=" + warehouses +
                    ", addTemplate=" + addTemplate +
                    ", currencySymbol='" + currencySymbol + '\'' +
                    ", claimTemplates=" + claimTemplates +
                    ", lastPermissionUpdate=" + lastPermissionUpdate +
                    ", permissions=" + permissions +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "VerifyOtpModel{" +
                "results=" + results +
                '}';
    }
}


