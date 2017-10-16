package com.rickxpc.ripay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
@ConfigurationProperties(prefix = "wxconfig")
public class WxConfig {
    private String appid;
    private String appsecret;
    private String mchid;
    private String key;
    private String notifyUrl;
    private Api api;

    public static class Api{
        private String unifiedOrder;
        private String orderQuery;
        private String closeOrder;
        private String downloadBill;

        public String getUnifiedOrder() {
            return unifiedOrder;
        }

        public void setUnifiedOrder(String unifiedOrder) {
            this.unifiedOrder = unifiedOrder;
        }

        public String getOrderQuery() {
            return orderQuery;
        }

        public void setOrderQuery(String orderQuery) {
            this.orderQuery = orderQuery;
        }

        public String getCloseOrder() {
            return closeOrder;
        }

        public void setCloseOrder(String closeOrder) {
            this.closeOrder = closeOrder;
        }

        public String getDownloadBill() {
            return downloadBill;
        }

        public void setDownloadBill(String downloadBill) {
            this.downloadBill = downloadBill;
        }
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }
}
