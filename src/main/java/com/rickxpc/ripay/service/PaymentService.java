package com.rickxpc.ripay.service;


import com.rickxpc.ripay.service.dto.UnifiedOrderParam;

import java.util.Map;

public interface PaymentService {

    Map<String, String> unifiedOrder(UnifiedOrderParam param) throws Exception;

    void handleResultNotify(String result) throws Exception;

    Map<String, String> orderQueryByTransId(String transactionId) throws Exception;

    Map<String, String> orderQueryByTradeNo(String tradeNo) throws Exception;

    Map<String, String> closeOrder(String tradeNo) throws Exception;

    void reconcile(String tradeNo) throws Exception;

    void reconcileByDate(String dateStr) throws Exception;
}
