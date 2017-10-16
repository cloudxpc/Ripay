package com.rickxpc.ripay.service.impl;

import com.rickxpc.ripay.config.WxConfig;
import com.rickxpc.ripay.domain.Payment;
import com.rickxpc.ripay.domain.PaymentType;
import com.rickxpc.ripay.repository.PaymentRepository;
import com.rickxpc.ripay.repository.PaymentTypeRepository;
import com.rickxpc.ripay.service.PaymentService;
import com.rickxpc.ripay.service.dto.UnifiedOrderParam;
import com.rickxpc.ripay.service.util.WxPayUtil;
import com.rickxpc.ripay.web.exceptions.WxPayException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private WxConfig wxConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    private Log logger = LogFactory.getLog(PaymentServiceImpl.class);
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    private String generateTradeNo() {
        return wxConfig.getMchid() + dateFormatter.format(Calendar.getInstance().getTime()) + new Random().nextInt(999);
    }

    private Map<String, String> getAndValidateResultData(String result) throws Exception {
        if (!result.startsWith("<xml>"))
            throw new WxPayException("Result string is not starting with '<xml>'", new Exception(result));

        Map<String, String> resultData = WxPayUtil.xmlToMap(result);

        if (!resultData.containsKey("return_code"))
            throw new WxPayException("Result does not contain key 'return_code'");
        if (!resultData.get("return_code").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"));
        if (!WxPayUtil.isSignatureValid(resultData, wxConfig.getKey()))
            throw new WxPayException("Result signature is not valid");

        return resultData;
    }

    @Override
    public Map<String, String> unifiedOrder(UnifiedOrderParam param) throws Exception {
        Calendar start = Calendar.getInstance();
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.MINUTE, 10);

        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppid());
        data.put("mch_id", wxConfig.getMchid());
        data.put("device_info", "WEB");
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");
        data.put("body", param.getTitle());
        data.put("attach", param.getAttach());
        data.put("out_trade_no", generateTradeNo());
        data.put("total_fee", param.getPayAmt().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        data.put("spbill_create_ip", param.getRemoteIp());
        data.put("time_start", dateFormatter.format(start.getTime()));
        data.put("time_expire", dateFormatter.format(expire.getTime()));
        data.put("notify_url", wxConfig.getNotifyUrl());
        data.put("trade_type", "JSAPI");
        data.put("limit_pay", "no_credit");
        data.put("openid", param.getOpenId());

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getApi().getUnifiedOrder(), xml, String.class);
        Map<String, String> resultData = getAndValidateResultData(result);
        if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));

        PaymentType paymentType = paymentTypeRepository.findOne(param.getPaymentTypeCode());
        if (paymentType == null)
            throw new Exception("Cannot find payment type by '" + param.getPaymentTypeCode() + "'");

        //Save payment information into db
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setUserId("0e3661bf-dcdb-4267-8237-d94edcee98cc"); //TODO: actual user id
        payment.setPaymentTypeCode(paymentType.getPaymentTypeCode());
        payment.setTradeNo(data.get("out_trade_no"));
        payment.setTitle(data.get("body"));
        payment.setAttach(data.get("attach"));
        payment.setTotalFee(param.getPayAmt());
        payment.setRemoteIP(data.get("spbill_create_ip"));
        payment.setTradeType(data.get("trade_type"));
        payment.setDeviceInfo(data.get("device_info"));
        payment.setCreationDate(start.getTime());
        payment.setStatus('N'); //NOTPAY(订单未支付)
        paymentRepository.save(payment);

        //Make parameters for JS API call
        Map<String, String> jsApiData = new HashMap<>();
        jsApiData.put("appId", resultData.get("appid"));
        jsApiData.put("timeStamp", WxPayUtil.getCurrentTimestamp() + "");
        jsApiData.put("nonceStr", WxPayUtil.generateNonceStr());
        jsApiData.put("package", "prepay_id=" + resultData.get("prepay_id"));
        jsApiData.put("signType", "MD5");
        jsApiData.put("paySign", WxPayUtil.generateSignature(jsApiData, wxConfig.getKey()));
        jsApiData.put("title", payment.getTitle());
        jsApiData.put("attach", payment.getAttach());
        jsApiData.put("totalFee", payment.getTotalFee().toString());
        jsApiData.put("tradeNo", payment.getTradeNo());
        jsApiData.put("paymentTypeDsc", paymentType.getPaymentTypeDsc());

        return jsApiData;
    }

    @Override
    public Map<String, String> orderQueryByTransId(String transactionId) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppid());
        data.put("mch_id", wxConfig.getMchid());
        data.put("transaction_id", transactionId);
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getApi().getOrderQuery(), xml, String.class);
        return getAndValidateResultData(result);
    }

    @Override
    public Map<String, String> orderQueryByTradeNo(String tradeNo) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppid());
        data.put("mch_id", wxConfig.getMchid());
        data.put("out_trade_no", tradeNo);
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getApi().getOrderQuery(), xml, String.class);
        return getAndValidateResultData(result);
    }

    @Override
    public Map<String, String> closeOrder(String tradeNo) throws Exception {
        Payment payment = paymentRepository.findPaymentByTradeNo(tradeNo);
        if (payment == null)
            throw new Exception("Cannot find payment by trade no '" + tradeNo + "'");
        if (payment.getStatus() == 'S')
            throw new Exception("Order cannot be closed as it has been paid successfully");
        if (payment.getStatus() == 'C')
            throw new Exception("Order has been already closed");

        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppid());
        data.put("mch_id", wxConfig.getMchid());
        data.put("out_trade_no", tradeNo);
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getApi().getCloseOrder(), xml, String.class);
        Map<String, String> resultData = getAndValidateResultData(result);
        if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));

        payment.setStatus('C');
        payment.setCompleteDate(Calendar.getInstance().getTime());
        paymentRepository.save(payment);

        return resultData;
    }

    @Override
    public void handleResultNotify(String result) throws Exception {
        Map<String, String> resultData = getAndValidateResultData(result);
        if (!resultData.containsKey("out_trade_no"))
            throw new Exception("Cannot find out_trade_no");
        String tradeNo = resultData.get("out_trade_no");
        Payment payment = paymentRepository.findPaymentByTradeNo(tradeNo);
        if (payment == null) {
            logger.warn("Cannot find payment by trade no '" + tradeNo + "'");
            return;
        }
        //1. Check if the payment has already been handled or not
        if (payment.getStatus() != 'N') { //Handled
            logger.info("Trade No '" + tradeNo + "' has been handled before, skip the process");
            return;
        }
        //2. Check the total fee
        BigDecimal totalFee = BigDecimal.valueOf(Integer.parseInt(resultData.get("total_fee")) / 100d);
        if (payment.getTotalFee().compareTo(totalFee) != 0)
            throw new Exception("The total fee [" + payment.getTotalFee() + "] of trade no '" + tradeNo + "' is not equal to the total fee [" + resultData.get("total_fee") + " / " + totalFee + "] of notify result");

        //3. Update payment information
        payment.setTransactionId(resultData.get("transaction_id"));
        payment.setIsSubscribe(resultData.get("is_subscribe").charAt(0));
        payment.setBankType(resultData.get("bank_type"));
        payment.setCompleteDate(dateFormatter.parse(resultData.get("time_end")));
        if (resultData.getOrDefault("result_code", "").equals("SUCCESS")) {
            payment.setStatus('S');
        } else {
            //TODO: handle business error
            logger.error(resultData.get("return_code") + " " +
                    resultData.get("return_msg") + " " +
                    resultData.get("err_code") + " " +
                    resultData.get("err_code_des"));
            //throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));
            payment.setStatus('E');
        }
        paymentRepository.save(payment);
    }

    @Override
    public void reconcile(String tradeNo) throws Exception {
        if (tradeNo == null || tradeNo.isEmpty())
            throw new Exception("trade no is null or empty");

        logger.info("Reconcile order '" + tradeNo + "'...");

        Payment payment = paymentRepository.findPaymentByTradeNo(tradeNo);
        if (payment == null)
            throw new Exception("Cannot find payment by trade no '" + tradeNo + "'");
        Map<String, String> resultData = orderQueryByTradeNo(tradeNo);
        if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));
        //Compare data
        if (resultData.containsKey("trade_state")) {
            String tradeState = resultData.get("trade_state");
            switch (tradeState) {
                case "SUCCESS":
                    if (payment.getStatus() != 'S') {
                        logger.info("Payment status: from '" + payment.getStatus() + "' to 'S'");
                        payment.setStatus('S');
                    }
                    break;
                case "REFUND":
                    if (payment.getStatus() != 'R') {
                        logger.info("Payment status: from '" + payment.getStatus() + "' to 'R'");
                        payment.setStatus('R');
                    }
                    break;
                case "NOTPAY":
                    if (payment.getStatus() != 'N') {
                        logger.info("Payment status: from '" + payment.getStatus() + "' to 'N'");
                        payment.setStatus('N');
                    }
                    break;
                case "CLOSED":
                    if (payment.getStatus() != 'C') {
                        logger.info("Payment status: from '" + payment.getStatus() + "' to 'C'");
                        payment.setStatus('C');
                    }
                    break;
                case "REVOKED":
                    throw new Exception("Not Supported REVOKED");
                case "USERPAYING":
                    if (payment.getStatus() != 'P') {
                        logger.info("Payment status: from '" + payment.getStatus() + "' to 'P'");
                        payment.setStatus('P');
                    }
                    break;
                case "PAYERROR":
                    if (payment.getStatus() != 'E') {
                        logger.info("Payment status: from '" + payment.getStatus() + "' to 'E'");
                        payment.setStatus('E');
                    }
                    break;
                default:
                    throw new Exception("Unknown trade state from wechat server");
            }
        }
        if (resultData.containsKey("trade_state_desc")) {
            logger.info(resultData.get("trade_state_desc"));
        }
        if (resultData.containsKey("bank_type") && !resultData.get("bank_type").equals(payment.getBankType())) {
            logger.info("Payment bank type: from '" + payment.getBankType() + "' to '" + resultData.get("bank_type") + "'");
            payment.setBankType(resultData.get("bank_type"));
        }
        if (resultData.containsKey("total_fee") && !resultData.get("total_fee").equals(payment.getTotalFee().multiply(BigDecimal.valueOf(100)).intValue() + "")) {
            logger.info("Payment total fee: from '" + payment.getTotalFee() + "' to '" + BigDecimal.valueOf(Integer.parseInt(resultData.get("total_fee")) / 100d) + "'");
            payment.setTotalFee(BigDecimal.valueOf(Integer.parseInt(resultData.get("total_fee")) / 100d));
        }
        if (resultData.containsKey("transaction_id") && !resultData.get("transaction_id").equals(payment.getTransactionId())) {
            logger.info("Payment transaction id: from '" + payment.getTransactionId() + "' to '" + resultData.get("transaction_id") + "'");
            payment.setTransactionId(resultData.get("transaction_id"));
        }

        paymentRepository.save(payment);

        logger.info("Reconcile order '" + tradeNo + "'...Finished");
    }

    @Override
    public void reconcileByDate(String dateStr) throws Exception {
        Date from = dateFormatter.parse(dateStr + "000000");
        Date to = dateFormatter.parse(dateStr + "235959");

        List<Payment> payments = paymentRepository.findPaymentsByCreationDateBetween(from, to);
        payments.stream().map(p -> p.getTradeNo()).forEach(tradeNo -> {
            try {
                reconcile(tradeNo);
            } catch (Exception e) {
                logger.error("Reconcile trade no '" + tradeNo + "' error");
                e.printStackTrace();
            }
        });
    }
}
