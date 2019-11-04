package com.bussiness.conroller.backend;


import com.bussiness.common.RoleEnum;
import com.bussiness.common.ServerResponse;
import com.bussiness.service.IUserService;
import com.bussiness.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value="/manage/")
public class UserManageController {

    @Autowired
    IUserService userService;
    /*
      登录接口
       */
    @RequestMapping(value="login/{username}/{password}")
    public ServerResponse login(@PathVariable("username")String username,
                                @PathVariable("password")String password,
                                HttpSession session){
        ServerResponse serverResonse = userService.login(username, password,RoleEnum.ROLE_ADMIN.getRole());
        //判断是否登录成功
        if(serverResonse.isSuccess()){
            session.setAttribute(Const.CURRENE_USER,serverResonse.getData());
        }
        return serverResonse;
    }

}
