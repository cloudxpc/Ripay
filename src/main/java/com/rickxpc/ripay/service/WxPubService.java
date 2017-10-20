package com.rickxpc.ripay.service;


import com.rickxpc.ripay.service.dto.BillDto;
import com.rickxpc.ripay.service.dto.UnifiedOrderParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WxPubService {

    Map<String, String> unifiedOrder(UnifiedOrderParam param) throws Exception;

    void handleResultNotify(String result) throws Exception;

    Map<String, String> orderQueryByTransId(String transactionId) throws Exception;

    Map<String, String> orderQueryByOrderId(String orderId) throws Exception;

    Map<String, String> closeOrder(String orderId) throws Exception;

    String downloadBill(String dateStr) throws Exception;

    void reconcileByOrderId(String orderId) throws Exception;

    void reconcileByDate(String dateStr) throws Exception;

    void reconcileWithBill(String dateStr, BillDto billDto) throws Exception;

    BillDto queryBillDto(String dateStr) throws Exception;

}
