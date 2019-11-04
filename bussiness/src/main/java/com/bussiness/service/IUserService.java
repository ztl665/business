package com.bussiness.service;

import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public interface IUserService {

    /*
    注册接口
    @param user
    @return ServerResponse
     */
    public ServerResponse register(User user);


    /*
    登录接口
        type 1:普通用户
        type 0:管理员
     */

    public ServerResponse login(String username ,String password,int type);

    /*
     忘记密码
    */

    /*根据 usename 获取密保问题*/

    public ServerResponse forget_get_question(String username);

    /* 提交答案*/

    public ServerResponse forget_check_answer(String username,String question ,String answer);


    /* 重置密码*/

    public ServerResponse forget_reset_password(String username,String newpassword ,String forgettoken);

    /*
    修改用户信息
     */
    public ServerResponse update_information(User user);
    /*
    修改用户密码，登录状态
     */
    public ServerResponse reset_password( User loginUser,String oldpassword,String newpassword);
}
