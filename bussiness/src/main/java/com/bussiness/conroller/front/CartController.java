package com.bussiness.conroller.front;

import com.bussiness.common.ResponseCode;
import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.User;
import com.bussiness.service.ICartService;
import com.bussiness.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CartController {


    @Autowired
    ICartService cartService;
    /**
     * 添加商品到购物车
     * */
    @RequestMapping("add/{productId}/{count}")
   public ServerResponse addCart(@PathVariable("productId")Integer productId,
                                 @PathVariable("count")Integer count,
                                 HttpSession session){

        User user=(User)session.getAttribute(Const.CURRENE_USER);
        if(user==null) {
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        return cartService.addProductToCart(user.getId(), productId, count);
   }


}
