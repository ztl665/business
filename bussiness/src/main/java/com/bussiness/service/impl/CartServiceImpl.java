package com.bussiness.service.impl;

import com.bussiness.common.CheckEnum;
import com.bussiness.common.ResponseCode;
import com.bussiness.common.ServerResponse;
import com.bussiness.dao.CartMapper;
import com.bussiness.pojo.Cart;
import com.bussiness.pojo.Product;
import com.bussiness.service.ICartService;
import com.bussiness.service.IProductService;
import com.bussiness.utils.BigDecimalUtils;
import com.bussiness.vo.CartProductVO;
import com.bussiness.vo.CartVO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    IProductService productService;

    @Autowired
    CartMapper cartMapper;

    @Override
    public ServerResponse addProductToCart(Integer userId, Integer productId, Integer count) {

        //step1: 参数非空判断
        if(productId==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品id必传");
        }
        if(count==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品数量不能为0");
        }
        //step2:判断商品是否是否存在
        ServerResponse<Product> serverResponse=productService.findProductById(productId);
        if(!serverResponse.isSuccess())  {//商品不存在
            return ServerResponse.serverResponseByError(serverResponse.getStatus(),serverResponse.getMsg());
        }else{
            Product product=serverResponse.getData();
            if(product.getStock()<=0){
                return  ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品已售空");
            }
        }
        //step3: 判断商品是否在购物车中
       Cart cart= cartMapper.findCartByUseridAndProductId(userId, productId);
       if(cart==null){
           //添加
           Cart newCart=new Cart();
           newCart.setUserId(userId);
           newCart.setProductId(productId);
           newCart.setQuantity(count);
           newCart.setChecked(CheckEnum.CART_PRODUCT_CHECK.getCheck());
           int result=cartMapper.insert(newCart);
           if(result<=0){
               return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加失败");
           }
       }else{
           //更新商品在购物车中的数量
           cart.setQuantity(cart.getQuantity()+count);
           int result=cartMapper.updateByPrimaryKey(cart);
           if(result<=0){
               return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新失败");
           }
       }
        //step4: 封装购物车对象CartVO
        CartVO cartVO=getCartVO(userId);
        //step5:返回CartVO
        return ServerResponse.serverResponseBySuccess(cartVO);
    }

    @Override
    public ServerResponse<List<Cart>> findCartsByUseridAndChecked(Integer userId) {

        List<Cart> cartList= cartMapper.findCartsByUseridAndChecked(userId);
        return ServerResponse.serverResponseBySuccess(cartList);
    }

    @Override
    public ServerResponse deleteBatch(List<Cart> cartList) {

        if(cartList==null||cartList.size()==0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"要删除的购物车商品不能为空");
        }
        int result= cartMapper.deleteBatch(cartList);
        if(result!=cartList.size()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"购物车清空失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }


    private CartVO getCartVO(Integer userId){

        CartVO cartVO=new CartVO();

      //step1:根据userid查询该用户的购物信息 -->List<Cart>
        List<Cart> cartList= cartMapper.findCartsByUserid(userId);
        if(cartList==null||cartList.size()==0){
            return  cartVO;
        }
        //定义购物车商品总价格
         BigDecimal cartTotalPrice=new BigDecimal("0");

     //step2: List<Cart> --> List<CartProductVO>
         List<CartProductVO> cartProductVOList= Lists.newArrayList();
         int limit_quantity=0;
         String limitQuantity=null;
         for(Cart cart:cartList){
            //Cart-->CartProductVO
             CartProductVO cartProductVO=new CartProductVO();
             cartProductVO.setId(cart.getId());
             cartProductVO.setUserId(userId);
             cartProductVO.setProductId(cart.getProductId());
             ServerResponse<Product> serverResponse=productService.findProductById(cart.getProductId());
             if(serverResponse.isSuccess()){
                 Product product=serverResponse.getData();
                 if(product.getStock()>=cart.getQuantity()){
                     limit_quantity=cart.getQuantity();
                     limitQuantity="LIMIT_NUM_SUCCESS";
                 }else{
                     limit_quantity=product.getStock();
                     limitQuantity="LIMIT_NUM_FAIL";
                 }
                 cartProductVO.setQuantity(limit_quantity);
                 cartProductVO.setLimitQuantity(limitQuantity);
                 cartProductVO.setProductName(product.getName());
                 cartProductVO.setProductSubtitle(product.getSubtitle());
                 cartProductVO.setProductMainImage(product.getMainImage());
                 cartProductVO.setProductPrice(product.getPrice());
                 cartProductVO.setProductStatus(product.getStatus());
                 cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(), cart.getQuantity()*1.0));
                 cartProductVO.setProductStock(product.getStock());
                 cartProductVO.setProductChecked(cart.getChecked());

                 cartProductVOList.add(cartProductVO);

                 if(cart.getChecked()==CheckEnum.CART_PRODUCT_CHECK.getCheck()){
                     //商品被选中
                     cartTotalPrice=BigDecimalUtils.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                 }
             }
         }
         cartVO.setCartProductVOList(cartProductVOList);

         //step3:计算购物车总得价格
         cartVO.setCarttotalprice(cartTotalPrice);

         //step4:判断是否全选
         Integer isAllChecked=  cartMapper.isAllChecked(userId);
         if(isAllChecked==0){
            //全选
            cartVO.setIsallchecked(true);
         }else {
            cartVO.setIsallchecked(false);
         }
     //step5:构建cartvo
        return cartVO;
     }

}
