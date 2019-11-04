package com.bussiness.service.impl;

import com.bussiness.common.ResponseCode;
import com.bussiness.common.ServerResponse;
import com.bussiness.dao.ShippingMapper;
import com.bussiness.pojo.Shipping;
import com.bussiness.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServerResponse add(Shipping shipping) {

        //step1:参数非空判断
        if(shipping==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        Integer shippinid=shipping.getId();
        if(shippinid==null){
            //添加
          int result=  shippingMapper.insert(shipping);
          if(result<=0){
              return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加地址失败");
          }else{
              return ServerResponse.serverResponseBySuccess(shipping.getId());
          }
        }else{
            //更新
        }
        return null;
    }

    @Override
    public ServerResponse findShippingById(Integer shippingid) {
        if(shippingid==null){
            return  ServerResponse.serverResponseByError(ResponseCode.ERROR,"shippingid必传");
        }
       Shipping shipping= shippingMapper.selectByPrimaryKey(shippingid);
        if(shipping==null){
            return  ServerResponse.serverResponseByError(ResponseCode.ERROR,"收货地址不存在");
        }
        return ServerResponse.serverResponseBySuccess(shipping);
    }
}
