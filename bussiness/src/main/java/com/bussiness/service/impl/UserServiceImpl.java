package com.bussiness.service.impl;

import com.bussiness.common.ResponseCode;
import com.bussiness.common.RoleEnum;
import com.bussiness.common.ServerResponse;
import com.bussiness.dao.UserMapper;
import com.bussiness.pojo.User;
import com.bussiness.service.IUserService;
import com.bussiness.utils.MD5Utils;
import com.bussiness.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.UUID;


@Service
public class UserServiceImpl  implements IUserService {


    @Autowired
    private  UserMapper userMapper;
    @Override
    public ServerResponse register(User user) {

        /*参数的校验*/
       if(user==null){
           return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"参数不能为空！");
       }
       /*用户名是否存在*/
      int result=userMapper.isexistusername(user.getUsername());
      if (result>0){
          return ServerResponse.serverResponseByError(ResponseCode.USERNAME_EXISTS,"用户名已经存在！");
      }
      /*判断邮箱是否存在*/
    int resultemail=userMapper.isexistemail(user.getEmail());
    if (resultemail>0){
        return ServerResponse.serverResponseByError(ResponseCode.EMAIL_EXISTS,"邮箱已经存在！");
    }
    /* 密码加密，设置用户角色*/
        user.setPassword(MD5Utils.getMD5Code(user.getPassword()));
        //设置角色为普通用户
        user.setRole(RoleEnum.ROLE_USER.getRole());

        /*注册*/
      int insertResult=userMapper.insert(user);
      if (insertResult==0){
         return ServerResponse.serverResponseByError(ResponseCode.ERROR,"注册失败");
      }
      /*返回*/
        return ServerResponse.serverResponseBySuccess();
    }



    @Override
    public ServerResponse login(String username, String password,int type) {
        //参数校验
        if(username==null||username.equals("")){
         return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"用户名不能为空!");
        }
        if (password==null||password.equals("")){
          return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"密码不能为空！");
        }

        //判断用户名是否存在
        int result=userMapper.isexistusername(username);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不存在！");
        }
        //密码加密
        password=MD5Utils.getMD5Code(password);
        //登录
        User user=userMapper.findUserByUsernameAndPassword(username,password);
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密码错误！");
        }
        if(type==0){  //管理员
            if(user.getRole()==RoleEnum.ROLE_USER.getRole()){  //普通用户
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"登录权限不足！");
            }
        }
        return ServerResponse.serverResponseBySuccess(user);
    }


    /*
     忘记密码
     */
    @Override
    public ServerResponse forget_get_question(String username) {
        //参数非空校验
        if(username==null||username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"用户名不能为空!");
        }
        //根据用户名查询问题
        String question=userMapper.forget_get_question(username);
        if(question==null||question.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有找到相关问问题！");
        }
        //返回结果
        return ServerResponse.serverResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
        //参数非空校验
        if(username==null||username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"用户名不能为空!");
        }
        if(question==null||question.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"密保问题不能为空!");
        }
        if(answer==null||answer.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"密保答案不能为空!");
        }
        //校验答案
        int result=userMapper.forget_check_answer(username,question,answer);
        if(result<=0){
              return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密保答案错误！");
        }
        //生成token  UUID 通用唯一标识
        String token =UUID.randomUUID().toString();
        TokenCache.set("username:"+username,token );
        return ServerResponse.serverResponseBySuccess(token);

    }
     //修改密码
    @Override
    public ServerResponse forget_reset_password(String username, String newpassword, String forgettoken) {
        if(username==null||username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"用户名不能为空!");
        }
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"新密码不能为空!");
        }
        if(forgettoken==null||forgettoken.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"token不能为空!");
        }
        //是否修改的是自己的账号：
        String token=TokenCache.get("username:"+username);

        if (token==null) {
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"不可修改他人密码，或请求过期！");
        }
        if(!token.equals(forgettoken)){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"无效的token");
        }
        int result=userMapper.forget_reset_password(username,MD5Utils.getMD5Code(newpassword));
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密码修改失败！");
        }
        return ServerResponse.serverResponseBySuccess();
    }
    /*
    更新用户信息
     */
    @Override
    public ServerResponse update_information(User user) {
        if(user==null){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"用户不能为空!");
        }
         int result=userMapper.updateUserByActive(user);
         if(result<=0){
             return ServerResponse.serverResponseByError(ResponseCode.ERROR,"修改失败");
         }
        return ServerResponse.serverResponseBySuccess();
    }
    /*
    修改用户密码 登录状态
   */

    @Override
    public ServerResponse reset_password(User loginUser, String oldpassword, String newpassword) {
        if(oldpassword==null||oldpassword.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"旧密码不能为空!");
        }
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"新密码不能为空!");
        }
        //判断是否是修改的自己的密码：
       /* System.out.println("传进来的旧密码"+oldpassword);
        System.out.println("修改前旧密码："+loginUser.getPassword());*/
        if(!(MD5Utils.getMD5Code(oldpassword).equals(loginUser.getPassword()))){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"非法修改！") ;
        }
        int result=userMapper.reset_password(loginUser.getUsername(),MD5Utils.getMD5Code(newpassword));
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"修改失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

}
