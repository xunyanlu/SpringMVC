package cn.edu.guet.controller;

import cn.edu.guet.bean.User;
import cn.edu.guet.common.ResponseData;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UserController {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/saveUser")
    public ResponseData saveUser(User user){
        return userService.saveUser(user);
    }

  /*  @RequestMapping("/saveUser")
    public String saveUser(User user){
        userService.saveUser(user);
        System.out.println("保存用户");
        return "viewUser.do";
    }
    @RequestMapping("/deleteUser")
    public String deleteUser(int id){
        userService.deleteUser(id);
        System.out.println(id);
        return "viewUser.do";
    }
    @RequestMapping("/viewUser")
    public String viewUser(HttpSession session){
        List<User> userList = userService.viewUser();
        session.setAttribute("userList",userList);
        return "redirect:/viewUser.jsp";
    }*/
}
