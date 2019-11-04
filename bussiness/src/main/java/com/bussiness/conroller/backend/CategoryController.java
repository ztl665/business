package com.bussiness.conroller.backend;


import com.bussiness.common.ResponseCode;
import com.bussiness.common.RoleEnum;
import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.Category;
import com.bussiness.pojo.User;
import com.bussiness.service.ICategoryService;
import com.bussiness.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController   //json
@RequestMapping("/manage/category/")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;
    /*
    添加类别
     */
    @RequestMapping(value="add_category.do")
    public ServerResponse addCateGory(Category category, HttpSession session){

        User user=(User)session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        int role=user.getRole();
        if(role== RoleEnum.ROLE_USER.getRole()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"权限不足");
        }
        return categoryService.addCategory(category);
    }

    /*
     修改类别  categoryID
               categoryName
               categoryUrl
     */
    @RequestMapping(value="set_category.do")
    public ServerResponse updateCategory(Category category,HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        int role=user.getRole();
        if(role== RoleEnum.ROLE_USER.getRole()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"权限不足");
        }
        return categoryService.updateCategory(category);
    }

    /*
    查看平级类别
        categoryID
    */
    @RequestMapping(value="{categoryId}")
    public ServerResponse getCategoryById(@PathVariable("categoryId") Integer categoryId,HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        int role=user.getRole();
        if(role== RoleEnum.ROLE_USER.getRole()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"权限不足");
        }
        return categoryService.getCategoryById(categoryId);
    }

    /*
   递归获取
       categoryID
   */
    @RequestMapping(value="deep/{categoryId}")
    public ServerResponse deepCategory(@PathVariable("categoryId") Integer categoryId,HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENE_USER);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        int role=user.getRole();
        if(role== RoleEnum.ROLE_USER.getRole()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"权限不足");
        }
        return categoryService.deepCategory(categoryId);
    }

}
