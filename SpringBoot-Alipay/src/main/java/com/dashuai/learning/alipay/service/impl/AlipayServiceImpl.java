package com.dashuai.learning.alipay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.dashuai.learning.alipay.service.AlipayService;
import com.dashuai.learning.utils.json.JSONParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {

    @Value("${alipay.ali_return_url}")
    private String ali_return_url;

    @Value("${alipay.ali_notify_url}")
    private String ali_notify_url;

    @Value("${alipay.product_no}")
    private String product_no;

    @Value("${alipay.time_express}")
    private String time_express;

    @Value("${alipay.gatewary.url}")
    private String url;

    @Value("${alipay.appid}")
    private String appid;

    @Value("${alipay.private_key}")
    private String private_key;

    @Value("${alipay.ali_public_key}")
    private String ali_public_key;

    @Value("${alipay.sign_type}")
    private String sign_type;
    @Autowired
    private AlipayClient alipayClient;

    public static final String TRADE_SUCCESS = "TRADE_SUCCESS"; //支付成功标识
    public static final String TRADE_CLOSED = "TRADE_CLOSED";//交易关闭

    private final static Logger logger = LoggerFactory.getLogger(AlipayServiceImpl.class);

    /**
     * 接口地址：https://docs.open.alipay.com/270/alipay.trade.page.pay
     * @return
     */
    @Override
    public String alipay() {
        //创建Alipay支付请求对象
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(ali_return_url); //同步通知url
        request.setNotifyUrl(ali_notify_url);//异步通知url
        // 销售产品码 必填
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(String.valueOf(UUID.randomUUID()));
        model.setBody("出售男神");
        model.setSubject("男神一枚");
        model.setTotalAmount("0.01");
        model.setTimeoutExpress(time_express);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
//        ExtendParams extendParams = new ExtendParams();
//                extendParams.setSysServiceProviderId("2088511833207846");
//        model.setExtendParams(extendParams);
        model.setPassbackParams("merchantBizType%3d3C%26merchantBizNo%3d2016010101111");
        request.setBizModel(model);
        try {
            return alipayClient.pageExecute(request).getBody(); //调用SDK生成表单
        } catch (Exception e) {
            logger.info("支付请求发送失败");
            throw new RuntimeException("支付请求发送失败,请联系我们客服协助处理");
        }

    }

    @Override
    public String synchronous(HttpServletRequest request) {
        return null;
    }

    @Override
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        //接收参数进行校验

    }
    /**
     * 统一收单交易支付接口 @备注：
     *
     * @param model
     * @return
     * @throws AlipayApiException
     */
    public String alipaPay(AlipayTradeWapPayModel model) throws AlipayApiException {
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        request.setBizModel(model);
        request.setNotifyUrl(ali_notify_url);
        request.setReturnUrl(ali_return_url);
        AlipayTradePayResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    /**
     * 校验签名
     *
     * @param request
     * @return
     */
    @Override
    public boolean rsaCheckV1(HttpServletRequest request) {
        // https://docs.open.alipay.com/54/106370
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        System.out.println(JSONParseUtils.object2JsonString(params));

        try {
            return AlipaySignature.rsaCheckV1(params, ali_public_key, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            logger.warn("verify sigin error, exception is:{}", e);
            return false;
        }
    }

}