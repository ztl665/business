package com.bussiness.service;

import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.Product;

public interface IProductService {

    public ServerResponse addOrUpdate(Product product);
    /**
     * 后台商品搜索
     * */
    public ServerResponse search(String productName, Integer productId, Integer pageNum, Integer pageSize);

    /**
     * 商品详情
     * */
    public ServerResponse detail(Integer productId);


    /**
     * 商品详情
     * */
    public ServerResponse<Product> findProductByProductId(Integer productId);

    /**
     *
     * 根据商品id查询商品信息(库存)
     * */
    public ServerResponse<Product> findProductById(Integer productId);


    /**
     * 扣库存
     * */
    public ServerResponse reduceSotck(Integer productId, Integer stock);

}
