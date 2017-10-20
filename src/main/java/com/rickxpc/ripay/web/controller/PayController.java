package com.rickxpc.ripay.web.controller;

import com.rickxpc.ripay.domain.Payment;
import com.rickxpc.ripay.domain.PaymentWxPub;
import com.rickxpc.ripay.repository.PaymentModeRepository;
import com.rickxpc.ripay.repository.PaymentRepository;
import com.rickxpc.ripay.repository.PaymentWxPubRepository;
import com.rickxpc.ripay.service.WxPubService;
import com.rickxpc.ripay.service.dto.UnifiedOrderParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/pay")
public class PayController {
    private Log logger = LogFactory.getLog(PayController.class);

    @Autowired
    private WxPubService wxPubService;

    @RequestMapping(value = "/unifiedorder", method = RequestMethod.POST)
    public Map<String, String> unifiedOrder(HttpServletRequest request, @RequestBody UnifiedOrderParam param) throws Exception {
        param.setRemoteIp(request.getRemoteAddr());
//        param.setRemoteIp("127.0.0.1");
        Map<String, String> resultData = wxPubService.unifiedOrder(param);
        return resultData;
    }

    @RequestMapping(value = "/orderquery", method = RequestMethod.GET)
    public Map<String, String> orderquery(
            @RequestParam(required = false) String transId,
            @RequestParam(required = false) String orderId
    ) throws Exception {
        if (transId != null && !transId.isEmpty())
            return wxPubService.orderQueryByTransId(transId);
        else
            return wxPubService.orderQueryByOrderId(orderId);
    }

    @RequestMapping(value = "/closeorder", method = RequestMethod.GET)
    public Map<String, String> closeorder(@RequestParam String orderId) throws Exception {
        return wxPubService.closeOrder(orderId);
    }

    /**
     * 支付完成后，微信会把相关支付结果和用户信息发送给商户，商户需要接收处理，并返回应答。
     * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，微信会通过一定的策略定期重新发起通知，
     * 尽可能提高通知的成功率，但微信不保证通知最终能成功。 （通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
     * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
     * 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，如果没有处理过再进行处理，
     * 如果处理过直接返回结果成功。在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 特别提醒：商户系统对于支付结果通知的内容一定要做签名验证,并校验返回的订单金额是否与商户侧的订单金额一致，
     * 防止数据泄漏导致出现“假通知”，造成资金损失。
     * 技术人员可登进微信商户后台扫描加入接口报警群。
     */
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("notify url called");

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    sb.append(charBuffer, 0, bytesRead);
                }
            } else {
                sb.append("");
            }

            logger.info("Msg: \r\n" + sb);

            synchronized (this) {
                logger.info("Handling notify...");
                wxPubService.handleResultNotify(sb.toString());
            }

            logger.info("Send back success msg to WeChat server...");
            String responseXml =
                    "<xml>" +
                        "<return_code><![CDATA[SUCCESS]]></return_code>" +
                        "<return_msg><![CDATA[OK]]></return_msg>" +
                    "</xml>";
            bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            bufferedOutputStream.write(responseXml.getBytes());
            bufferedOutputStream.flush();
            logger.info("notify end");
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
    }

    //@RequestMapping(value = "/reconcile", method = RequestMethod.GET)
    public void reconcile(@RequestParam String dateStr) throws Exception {
        Date yyyyMMdd = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
        wxPubService.reconcileByDate(dateStr);
    }
}
