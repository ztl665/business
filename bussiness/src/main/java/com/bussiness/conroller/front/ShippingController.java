package com.bussiness.conroller.front;

import com.bussiness.common.ResponseCode;
import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.Shipping;
import com.bussiness.pojo.User;
import com.bussiness.service.IShippingService;
import com.bussiness.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    IShippingService shippingService;

    @RequestMapping(value = "add.do")
    public ServerResponse add(Shipping shipping, HttpSession session){

        User user=(User) session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }

        shipping.setUserId(user.getId());

        return shippingService.add(shipping);
    }

}
