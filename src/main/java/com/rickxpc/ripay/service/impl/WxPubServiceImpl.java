package com.rickxpc.ripay.service.impl;

import com.rickxpc.ripay.config.WxConfig;
import com.rickxpc.ripay.domain.Payment;
import com.rickxpc.ripay.domain.PaymentType;
import com.rickxpc.ripay.domain.PaymentWxPub;
import com.rickxpc.ripay.repository.PaymentRepository;
import com.rickxpc.ripay.repository.PaymentTypeRepository;
import com.rickxpc.ripay.repository.PaymentWxPubRepository;
import com.rickxpc.ripay.service.WxPubService;
import com.rickxpc.ripay.service.dto.BillDto;
import com.rickxpc.ripay.service.dto.BillLineDto;
import com.rickxpc.ripay.service.dto.BillSummaryDto;
import com.rickxpc.ripay.service.dto.UnifiedOrderParam;
import com.rickxpc.ripay.service.util.WxPayUtil;
import com.rickxpc.ripay.web.exceptions.WxPayException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WxPubServiceImpl implements WxPubService {
    private Log logger = LogFactory.getLog(WxPubServiceImpl.class);
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    @Autowired
    private WxConfig wxConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentWxPubRepository paymentWxPubRepository;

    private String generateTradeNo() {
        return wxConfig.getMchId() + dateFormatter.format(Calendar.getInstance().getTime()) + new Random().nextInt(999);
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

    private BillLineDto parseBillLine(String line) throws Exception {
        String[] strings = line.split(",");
        int index = 0;
        Character c = '`';
        BillLineDto dto = new BillLineDto();
        dto.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(StringUtils.trimLeadingCharacter(strings[index++], c)));
        dto.setAppId(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setMchId(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setSubMchId(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setDeviceInfo(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setTransactionId(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setTradeNo(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setOpenId(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setTradeType(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setTradeState(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setBankType(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setFeeType(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setTotalFee(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        dto.setCouponFee(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        dto.setRefundTransId(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setRefundTradeNo(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setRefundAmt(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        dto.setRefundCouponAmt(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        dto.setRefundType(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setRefundState(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setTitle(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setAttach(StringUtils.trimLeadingCharacter(strings[index++], c));
        dto.setChargeFee(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        dto.setChargeRate(StringUtils.trimLeadingCharacter(strings[index++], c));
        return dto;
    }

    private BillSummaryDto parseBillSummary(String line) {
        String[] strings = line.split(",");
        int index = 0;
        Character c = '`';
        BillSummaryDto summaryDto = new BillSummaryDto();
        summaryDto.setCount(Integer.parseInt(StringUtils.trimLeadingCharacter(strings[index++], c)));
        summaryDto.setTotalAmt(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        summaryDto.setTotalRefundAmt(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        summaryDto.setTotalCouponAmt(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        summaryDto.setTotalChargeAmt(BigDecimal.valueOf(Double.parseDouble(StringUtils.trimLeadingCharacter(strings[index++], c))));
        return summaryDto;
    }

    private void reconcile(PaymentWxPub paymentWxPub, Map<String, String> resultData) throws Exception {
        Payment payment = paymentWxPub.getPayment();
        String orderId = payment.getOrderId();

        logger.info("Reconcile order '" + orderId + "'...");

        //Compare data
        if (resultData.containsKey("trade_state")) {
            String tradeState = resultData.get("trade_state");
            switch (tradeState) {
                case "SUCCESS":
                    if (payment.getState() != 'S') {
                        logger.info("Payment status: from '" + payment.getState() + "' to 'S'");
                        payment.setState('S');
                    }
                    break;
                case "REFUND":
                    if (payment.getState() != 'R') {
                        logger.info("Payment status: from '" + payment.getState() + "' to 'R'");
                        payment.setState('R');
                    }
                    break;
                case "NOTPAY":
                    if (payment.getState() != 'N') {
                        logger.info("Payment status: from '" + payment.getState() + "' to 'N'");
                        payment.setState('N');
                    }
                    break;
                case "CLOSED":
                    if (payment.getState() != 'C') {
                        logger.info("Payment status: from '" + payment.getState() + "' to 'C'");
                        payment.setState('C');
                    }
                    break;
                case "REVOKED":
                    throw new Exception("Not Supported REVOKED");
                case "USERPAYING":
                    if (payment.getState() != 'P') {
                        logger.info("Payment status: from '" + payment.getState() + "' to 'P'");
                        payment.setState('P');
                    }
                    break;
                case "PAYERROR":
                    if (payment.getState() != 'E') {
                        logger.info("Payment status: from '" + payment.getState() + "' to 'E'");
                        payment.setState('E');
                    }
                    break;
                default:
                    throw new Exception("Unknown trade state from wechat server");
            }
        }
        if (resultData.containsKey("trade_state_desc")) {
            logger.info(resultData.get("trade_state_desc"));
        }
        if (resultData.containsKey("bank_type") && !resultData.get("bank_type").equals(paymentWxPub.getBankType())) {
            logger.info("PaymentWxPub bank type: from '" + paymentWxPub.getBankType() + "' to '" + resultData.get("bank_type") + "'");
            paymentWxPub.setBankType(resultData.get("bank_type"));
        }
        if (resultData.containsKey("total_fee") && !resultData.get("total_fee").equals(payment.getTotalAmt().multiply(BigDecimal.valueOf(100)).intValue() + "")) {
            logger.info("Payment total fee: from '" + payment.getTotalAmt() + "' to '" + BigDecimal.valueOf(Integer.parseInt(resultData.get("total_fee")) / 100d) + "'");
            payment.setTotalAmt(BigDecimal.valueOf(Integer.parseInt(resultData.get("total_fee")) / 100d));
        }
        if (resultData.containsKey("transaction_id") && !resultData.get("transaction_id").equals(payment.getTransactionId())) {
            logger.info("Payment transaction id: from '" + payment.getTransactionId() + "' to '" + resultData.get("transaction_id") + "'");
            payment.setTransactionId(resultData.get("transaction_id"));
        }
        if (resultData.containsKey("time_end") && (payment.getCompleteDate() == null || !resultData.get("time_end").equals(dateFormatter.format(payment.getCompleteDate())))) {
            logger.info("Payment complete date: from '" + payment.getCompleteDate() == null ? "null" : dateFormatter.format(payment.getCompleteDate()) + "' to '" + resultData.get("time_end") + "'");
            payment.setCompleteDate(dateFormatter.parse(resultData.get("time_end")));
        }

        logger.info("Reconcile order '" + orderId + "'...Finished");
    }

    @Override
    @Transactional
    public Map<String, String> unifiedOrder(UnifiedOrderParam param) throws Exception {
        Calendar start = Calendar.getInstance();
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.MINUTE, 10);

        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppId());
        data.put("mch_id", wxConfig.getMchId());
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
        String result = restTemplate.postForObject(wxConfig.getUnifiedOrderApi(), xml, String.class);
        Map<String, String> resultData = getAndValidateResultData(result);
        if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));

        //Save payment information into db
        Payment payment = new Payment();
        payment.setPaymentModeCode("00001");
        payment.setPaymentTypeCode(param.getPaymentTypeCode());
        payment.setOrderId(data.get("out_trade_no"));
        payment.setState('N'); //NOTPAY(订单未支付)
        payment.setTitle(data.get("body"));
        payment.setAttach(data.get("attach"));
        payment.setTotalAmt(param.getPayAmt());
        payment.setCreationDate(start.getTime());

        PaymentWxPub paymentWxPub = new PaymentWxPub();
        paymentWxPub.setPayment(payment);
        paymentWxPub.setOpenId(data.get("openid"));
        paymentWxPub.setRemoteIp(data.get("spbill_create_ip"));
        paymentWxPub.setTradeType(data.get("trade_type"));
        paymentWxPub.setDeviceInfo(data.get("device_info"));

        paymentWxPubRepository.save(paymentWxPub);

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
        jsApiData.put("totalFee", payment.getTotalAmt().toString());
        jsApiData.put("tradeNo", payment.getOrderId());

        return jsApiData;
    }

    @Override
    public Map<String, String> orderQueryByTransId(String transactionId) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppId());
        data.put("mch_id", wxConfig.getMchId());
        data.put("transaction_id", transactionId);
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getOrderQueryApi(), xml, String.class);
        return getAndValidateResultData(result);
    }

    @Override
    public Map<String, String> orderQueryByOrderId(String orderId) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppId());
        data.put("mch_id", wxConfig.getMchId());
        data.put("out_trade_no", orderId);
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getOrderQueryApi(), xml, String.class);
        return getAndValidateResultData(result);
    }

    @Override
    public Map<String, String> closeOrder(String orderId) throws Exception {
        PaymentWxPub paymentWxPub = paymentWxPubRepository.findByPaymentOrderId(orderId);
        if (paymentWxPub == null)
            throw new Exception("Cannot find payment by order id '" + orderId + "'");
        Payment payment = paymentWxPub.getPayment();
        if (payment.getState() == 'S')
            throw new Exception("Order cannot be closed as it has been paid successfully");
        if (payment.getState() == 'C')
            throw new Exception("Order has been already closed");
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppId());
        data.put("mch_id", wxConfig.getMchId());
        data.put("out_trade_no", orderId);
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getCloseOrderApi(), xml, String.class);
        Map<String, String> resultData = getAndValidateResultData(result);
        if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));

        payment.setState('C');
        paymentWxPubRepository.save(paymentWxPub);

        return resultData;
    }

    @Override
    public String downloadBill(String dateStr) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("appid", wxConfig.getAppId());
        data.put("mch_id", wxConfig.getMchId());
        data.put("nonce_str", WxPayUtil.generateNonceStr());
        data.put("sign_type", "MD5");
        data.put("bill_date", dateStr);
        data.put("bill_type", "ALL");

        String xml = WxPayUtil.generateSignedXml(data, wxConfig.getKey());
        String result = restTemplate.postForObject(wxConfig.getDownloadBillApi(), xml, String.class);
        if (result.startsWith("<xml>")) {
            Map<String, String> resultData = WxPayUtil.xmlToMap(result);
            if (!resultData.containsKey("return_code"))
                throw new WxPayException("Result does not contain key 'return_code'");
            if (!resultData.get("return_code").equals("SUCCESS"))
                throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"));
        }

        return result;
    }

    @Override
    public void handleResultNotify(String result) throws Exception {
        Map<String, String> resultData = getAndValidateResultData(result);
        if (!resultData.containsKey("out_trade_no"))
            throw new Exception("Cannot find out_trade_no");
        String orderId = resultData.get("out_trade_no");
        PaymentWxPub paymentWxPub = paymentWxPubRepository.findByPaymentOrderId(orderId);
        if (paymentWxPub == null) {
            logger.warn("Cannot find payment by order id '" + orderId + "'");
            return;
        }
        Payment payment = paymentWxPub.getPayment();
        //1. Check if the payment has already been handled or not
        if (payment.getState() != 'N') { //Handled
            logger.info("Order '" + orderId + "' has been handled before, skip the process");
            return;
        }
        //2. Check the total fee
        BigDecimal totalFee = BigDecimal.valueOf(Integer.parseInt(resultData.get("total_fee")) / 100d);
        if (payment.getTotalAmt().compareTo(totalFee) != 0)
            throw new Exception("The total fee [" + payment.getTotalAmt() + "] of order '" + orderId + "' is not equal to the total fee [" + resultData.get("total_fee") + " / " + totalFee + "] of notify result");

        //3. Update payment information
        payment.setTransactionId(resultData.get("transaction_id"));
        payment.setCompleteDate(dateFormatter.parse(resultData.get("time_end")));
        paymentWxPub.setBankType(resultData.get("bank_type"));
        paymentWxPub.setIsSubscribe(resultData.get("is_subscribe").charAt(0));
        if (resultData.getOrDefault("result_code", "").equals("SUCCESS")) {
            payment.setState('S');
        } else {
            //TODO: handle business error
            logger.error(resultData.get("return_code") + " " +
                    resultData.get("return_msg") + " " +
                    resultData.get("err_code") + " " +
                    resultData.get("err_code_des"));
            //throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));
            payment.setState('E');
        }

        paymentWxPubRepository.save(paymentWxPub);
    }

    @Override
    public void reconcileByOrderId(String orderId) throws Exception {
        if (orderId == null || orderId.isEmpty())
            throw new Exception("trade no is null or empty");

        PaymentWxPub paymentWxPub = paymentWxPubRepository.findByPaymentOrderId(orderId);
        if (paymentWxPub == null)
            throw new Exception("Cannot find payment by order id '" + orderId + "'");
        Map<String, String> resultData = orderQueryByOrderId(orderId);
        if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
            throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));

        reconcile(paymentWxPub, resultData);

        paymentWxPubRepository.save(paymentWxPub);
    }

    @Override
    public void reconcileByDate(String dateStr) throws Exception {
        Date from = dateFormatter.parse(dateStr + "000000");
        Date to = dateFormatter.parse(dateStr + "235959");

        List<PaymentWxPub> paymentWxPubs = paymentWxPubRepository.findByPaymentCreationDateBetween(from, to);
        paymentWxPubs.stream().map(p -> p.getPayment().getOrderId()).forEach(orderId -> {
            try {
                reconcileByOrderId(orderId);
            } catch (Exception e) {
                logger.error("Reconcile order id '" + orderId + "' error");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void reconcileWithBill(String dateStr, BillDto billDto) throws Exception {
        if (billDto == null || billDto.getLines().isEmpty())
            return;

        Date from = dateFormatter.parse(dateStr + "000000");
        Date to = dateFormatter.parse(dateStr + "235959");
        List<String> tradeNosToClose = new ArrayList<>();
        List<PaymentWxPub> paymentWxPubs = paymentWxPubRepository.findByPaymentCreationDateBetween(from, to);
        for (PaymentWxPub p : paymentWxPubs) {
            BillLineDto billLineDto = billDto.getLines().stream().filter(l -> l.getTradeNo().equals(p.getPayment().getOrderId())).findFirst().orElse(null);
            if (billLineDto != null) {
                Map<String, String> data = new HashMap<>();
                data.put("trade_state", billLineDto.getTradeState());
                data.put("bank_type", billLineDto.getBankType());
                data.put("total_fee", billLineDto.getTotalFee().multiply(BigDecimal.valueOf(100)).intValue() + "");
                data.put("transaction_id", billLineDto.getTransactionId());
                data.put("time_end", dateFormatter.format(billLineDto.getDate()));

                reconcile(p, data);
            } else {
                Map<String, String> resultData = orderQueryByOrderId(p.getPayment().getOrderId());
                if (!resultData.getOrDefault("result_code", "").equals("SUCCESS"))
                    throw new WxPayException(resultData.get("return_code"), resultData.get("return_msg"), resultData.get("err_code"), resultData.get("err_code_des"));

                reconcile(p, resultData);
            }

            if (p.getPayment().getState() == 'N' || p.getPayment().getState() == 'P') {
                tradeNosToClose.add(p.getPayment().getOrderId());
            }
        }

        paymentWxPubRepository.save(paymentWxPubs);

        tradeNosToClose.forEach(tn -> {
            try {
                closeOrder(tn);
                logger.info("Close order with trade no '" + tn + "'...");
            } catch (Exception e) {
                logger.error("Close order error", e);
                e.printStackTrace();
            }
        });
    }

    /**
     * 成功时，数据以文本表格的方式返回，第一行为表头，后面各行为对应的字段内容，字段内容跟查询订单或退款结果一致，具体字段说明可查阅相应接口。
     * 第一行为表头，根据请求下载的对账单类型不同而不同
     * <p>
     * 当日所有订单
     * 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,
     * 总金额,代金券或立减优惠金额,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额，退款类型，退款状态,商品名称,
     * 商户数据包,手续费,费率
     * <p>
     * 从第二行起，为数据记录，各参数以逗号分隔，参数前增加`符号，为标准键盘1左边键的字符，字段顺序与表头一致。
     * 倒数第二行为订单统计标题，最后一行为统计数据
     * 总交易单数，总交易额，总退款金额，总代金券或立减优惠退款金额，手续费总金额
     *
     * @param dateStr
     * @return
     * @throws Exception
     */
    @Override
    public BillDto queryBillDto(String dateStr) throws Exception {
        String result = downloadBill(dateStr);
        BillDto billDto = new BillDto();
        List<BillLineDto> billLineDtos = new ArrayList<>();
        String[] lines = result.split("\r\n");
        int index = 1;
        while (index < lines.length - 2) {
            billLineDtos.add(parseBillLine(lines[index++]));
        }
        billDto.setLines(billLineDtos);
        billDto.setSummary(parseBillSummary(lines[index + 1]));

        return billDto;
    }
}
