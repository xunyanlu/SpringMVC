package cn.edu.guet.dao;

import cn.edu.guet.bean.User;

import java.util.List;

/**
 * @Author liwei
 * @Date 2023/5/16 20:26
 * @Version 1.0
 */
public interface UserDao {
    int saveUser(User user);
    List<User> viewUser();
    int deleteUser(int id);

}
