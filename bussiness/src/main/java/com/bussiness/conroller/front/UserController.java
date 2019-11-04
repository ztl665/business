package com.bussiness.conroller.front;

import com.bussiness.common.ResponseCode;
import com.bussiness.common.RoleEnum;
import com.bussiness.common.ServerResponse;
import com.bussiness.pojo.User;
import com.bussiness.service.IUserService;
import com.bussiness.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/")

public class UserController {
  @Autowired
  IUserService userService;
  /*
  注册接口
   */

  @RequestMapping(value="register.do")
  /*涉及到对象绑定*/
    public ServerResponse register(User user) {

    return userService.register(user);

  }


  /*
  登录接口
   */
  @RequestMapping(value="login/{username}/{password}")
  public ServerResponse login(@PathVariable("username")String username,
                              @PathVariable("password")String password,
                              HttpSession session){
    ServerResponse serverResonse = userService.login(username, password,1);
    //判断是否登录成功
    if(serverResonse.isSuccess()){
      session.setAttribute(Const.CURRENE_USER,serverResonse.getData());
    }
      return serverResonse;
  }

  /*
  忘记密码
   */

       /*根据 usename 获取密保问题*/
        @RequestMapping(value="forget_get_question/{username}")
        public ServerResponse forget_get_question(@PathVariable("username") String username){
         return userService.forget_get_question(username);
         }
        /* 提交答案*/
        @RequestMapping(value="forget_check_answer.do")
        public ServerResponse forget_check_answer(String username,String question ,String answer){

          return userService.forget_check_answer(username,question,answer);
        }

        /* 重置密码*/
       @RequestMapping(value="forget_reset_password.do")
        public ServerResponse forget_reset_password(String username,String newpassword ,String forgettoken){
          return userService.forget_reset_password(username,newpassword,forgettoken);
         }



  /*
  修改用户信息
   */
@RequestMapping(value="update_information.do")
//查看用户是否已经登录
  public ServerResponse update_information(User user,HttpSession session){
    User loginUser=(User)session.getAttribute(Const.CURRENE_USER);
    if(loginUser==null){
        return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
    }
//    System.out.println(loginUser);
//    System.out.println(loginUser.getId());
//    System.out.println(loginUser.getPassword());
    user.setId(loginUser.getId());
    ServerResponse serverResponse=userService.update_information(user);
    return serverResponse;
  }

  /*
  登录状态修改用户密码
   */
  @RequestMapping(value="reset_password.do")
  public ServerResponse reset_password(String oldpassword,String newpassword,HttpSession session){
      User loginUser=(User)session.getAttribute(Const.CURRENE_USER);
      if(loginUser==null){
          return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
      }
      ServerResponse serverResponse =userService.reset_password(loginUser,oldpassword,newpassword);
      return serverResponse;
  }

}
