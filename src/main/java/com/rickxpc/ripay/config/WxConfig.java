package com.rickxpc.ripay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class WxConfig {
    @Value("${wxconfig.appid}")
    private String appId;
    @Value("${wxconfig.appsecret}")
    private String appSecret;
    @Value("${wxconfig.mchid}")
    private String mchId;
    @Value("${wxconfig.key}")
    private String key;
    @Value("${wxconfig.api.authorize}")
    private String authorizeApi;
    @Value("${wxconfig.api.access-token}")
    private String accessTokenApi;
    @Value("${wxconfig.api.unified-order}")
    private String unifiedOrderApi;
    @Value("${wxconfig.api.order-query}")
    private String orderQueryApi;
    @Value("${wxconfig.api.close-order}")
    private String closeOrderApi;
    @Value("${wxconfig.api.download-bill}")
    private String downloadBillApi;
    @Value("${wxconfig.url.notify}")
    private String notifyUrl;

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getMchId() {
        return mchId;
    }

    public String getKey() {
        return key;
    }

    public String getAuthorizeApi(String redirectUri, String state) throws UnsupportedEncodingException {
        return authorizeApi.replace("$REDIRECT_URI$", URLEncoder.encode(redirectUri, "UTF-8")).replace("$STATE$", state);
    }

    public String getAccessTokenApi(String code) {
        return accessTokenApi.replace("$CODE$", code);
    }

    public String getUnifiedOrderApi() {
        return unifiedOrderApi;
    }

    public String getOrderQueryApi() {
        return orderQueryApi;
    }

    public String getCloseOrderApi() {
        return closeOrderApi;
    }

    public String getDownloadBillApi() {
        return downloadBillApi;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }
}
