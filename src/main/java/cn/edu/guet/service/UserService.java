package cn.edu.guet.service;


import cn.edu.guet.bean.Users;
import cn.edu.guet.common.ResponseData;

import java.util.List;

/**
 * @Author liwei
 * @Date 2023/5/16 20:20
 * @Version 1.0
 */
public interface UserService {

    //缺少删除、修改
//    String deleteUser(int id);
//
//    List<Users> viewUser();

    ResponseData saveUser(Users users);
    // 项目中要封装统一返回结果
    ResponseData getUserById(long id);
    ResponseData getObjectById();
    ResponseData updateUserById(long Id,Users users);
}
