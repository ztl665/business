package com.bussiness.service;

import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.Category;


public interface ICategoryService {
      /*
      添加商品类别
       */
         public ServerResponse addCategory(Category category);
/*
     修改类别  categoryID
               categoryName
               categoryUrl
     */
     public ServerResponse updateCategory(Category category);

    /*
   查看平级类别
       categoryID
   */
    public ServerResponse getCategoryById( Integer categoryId);

    /*
   递归获取
       categoryID
   */
    public ServerResponse deepCategory(Integer categoryId);

    /*
        根据id查询类别
     */
    public ServerResponse<Category> selectCategory(Integer categoryId);
}







