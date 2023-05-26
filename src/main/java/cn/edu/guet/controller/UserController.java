package cn.edu.guet.controller;


import cn.edu.guet.bean.Users;
import cn.edu.guet.common.ResponseData;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.service.UserService;


@Controller
public class UserController {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/saveUser")
    public ResponseData saveUser(Users users){
        return userService.saveUser(users);
    }
    @RequestMapping("/getUserById")
    public ResponseData getUserById(long id){
        return userService.getUserById(id);
    }
    @RequestMapping("/getObjectById")
    public ResponseData getObjectById(){
        return userService.getObjectById();
    }
    @RequestMapping("/updateUserById")
    public ResponseData updateUserById(long Id,Users users){
        return userService.updateUserById(Id,users);
    }
}
