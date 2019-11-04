package com.bussiness.conroller.front;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.bussiness.common.ResponseCode;
import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.User;
import com.bussiness.service.IOrderService;
import com.bussiness.utils.Const;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    IOrderService orderService;
    /**
     * 创建订单接口
     * */

    @RequestMapping("{shippingid}")
    public ServerResponse createOrder(@PathVariable("shippingid")  Integer shippingid,
                                      HttpSession session){

        User user=(User) session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        return orderService.createOrder(user.getId(),shippingid);
    }


    /**
     * 支付接口
     *
     * */
    @RequestMapping("pay/{orderNo}")
    public  ServerResponse pay(@PathVariable("orderNo") Long orderNo,HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        return  orderService.pay(user.getId(),orderNo);
    }


    /**
     * 支付宝服务器回调商家服务器接口
     * */
    @RequestMapping("callback.do")
    public String  alipay_callback(HttpServletRequest request){
        Map<String,String[]> callbackParams=request.getParameterMap();
        Map<String,String> signParams= Maps.newHashMap();  //验证签名
        Iterator<String> iterator= callbackParams.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            String[] values=callbackParams.get(key);
            //System.out.println("key="+key+" values="+values);
            //验证签名
            StringBuffer sbuffer=new StringBuffer();
            if(values!=null&&values.length>0){
                for(int i=0;i<values.length;i++){ // 1 ,2 ,3
                    sbuffer.append(values[i]);
                    if(i!=values.length-1){
                        sbuffer.append(",");
                    }
                }
            }
            signParams.put(key,sbuffer.toString());
        }
        System.out.println(signParams);

        //验签(保证是由支付宝回调的)
        try {
            signParams.remove("sign_type");
            boolean result= AlipaySignature.rsaCheckV2(signParams, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(result){
                //验签通过
                System.out.println("通过");
                return orderService.callback(signParams);
            }else{
                return "fail";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "success";
    }

/**
 * {gmt_create=2019-05-21 16:12:08,
 * charset=utf-8,
 * seller_email=iujgal4963@sandbox.com,
 * subject=【睿乐购】平台商品支付,
 * sign=GfKkAUfTVloK1tNlOBf8qc05r1YV5+bvhiYr+saYnYPWrkU2o6Ff8gi1LIpT0cUfD1D5gv202t8tI5Nh1uIqeahd1tahElcQTxZJ+dP78axP7WQ9hvJEnIFhkk/Ax93M661JDnaOm4Rn4zUoZ2QTgg1KL69TKQoyi7/75WO4DW4jaLDr1Kon2oBOh5yC2tOaRJDvyQpjxkHsZPqdtqDmXFQvoJE/buKh1T652Cf6nRTPOSh5ZJyquE5rNxm5TSGbnQ8IhLOy9E6r6LuS97DBby0qlTpydonxnyiFZzJPib9/YYPQQdgre4mFC4bgIw+g2Zt33bkheh9+L5ppojm/dQ==,
 * body=购买商品件共99950.00元,
 * buyer_id=2088102176916953,
 * invoice_amount=99950.00,
 * notify_id=2019052100222161223016951000203990,
 * fund_bill_list=[{"amount":"99950.00","fundChannel":"ALIPAYACCOUNT"}],
 * notify_type=trade_status_sync,
 * trade_status=TRADE_SUCCESS,
 * receipt_amount=99950.00,
 * app_id=2016092100564475,
 * buyer_pay_amount=99950.00,
 * sign_type=RSA2, seller_id=2088102176656004,
 * gmt_payment=2019-05-21 16:12:22,
 * notify_time=2019-05-21 16:12:23, version=1.0,
 * out_trade_no=1558319420148, total_amount=99950.00,
 * trade_no=2019052122001416951000012374,
 * auth_app_id=2016092100564475,
 * buyer_logon_id=oej***@sandbox.com, point_amount=0.00}

 * */

}

