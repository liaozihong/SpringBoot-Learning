package com.dashuai.learning.alipay.api;

import com.dashuai.learning.alipay.service.AlipayService;
import com.dashuai.learning.utils.result.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AlipayApi {
    @Autowired
    private AlipayService alipayService;

    private final static Logger logger = LoggerFactory.getLogger(AlipayApi.class);

    //1.申请付款
//    @ApiOperation("申请付款")
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String alipay() {
        return alipayService.alipay();
    }

    //2.a1lipay支持同步返回地址
//    @ApiOperation("同步")
    @RequestMapping(value = "/getReturnUrlInfo", method = RequestMethod.GET)
    public ApiResult alipayReturnUrlInfo(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        return ApiResult.prepare().success(requestParams, 200, "支付成功!");
    }

    @RequestMapping("/")
    public String index() {
        return "你妹哦";
    }

    //3.alipay异步通知调用地址
//    @ApiOperation("异步通知")
    @RequestMapping(value = "/getNotifyUrlInfo", method = RequestMethod.POST)
    public void alipayNotifyUrlInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {

        boolean verifyResult = alipayService.rsaCheckV1(request);
        System.out.println(verifyResult);
        if (verifyResult) {// 验证成功
            //验证成功
            //请在这里加上商户的业务逻辑程序代码，如保存支付宝交易号
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            // ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status.equals("TRADE_FINISHED")) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                // 如果有做过处理，不执行商户的业务程序
                System.out.println("交易完成");
                // 注意：
                // 如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                // 如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                System.out.println("交易成功");
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                // 如果有做过处理，不执行商户的业务程序

                // 注意：
                // 如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (trade_status.equals("WAIT_BUYER_PAY")) {
                System.out.println("交易创建，等待付款");

            }
            // ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            System.out.println("支付成功");

        } else {// 验证失败
            System.out.println("支付失败");
        }

    }
}
