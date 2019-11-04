package com.bussiness.service;

import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.Shipping;

public interface IShippingService {

    public ServerResponse add(Shipping shipping);

    public ServerResponse findShippingById(Integer shippingid);
}
