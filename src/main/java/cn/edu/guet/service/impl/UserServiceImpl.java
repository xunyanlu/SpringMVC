package cn.edu.guet.service.impl;

import cn.edu.guet.bean.User;
import cn.edu.guet.common.ResponseData;
import cn.edu.guet.dao.UserDao;
import cn.edu.guet.dao.impl.UserDaoImpl;
import cn.edu.guet.service.UserService;

import java.util.List;

/**
 * @Author liwei
 * @Date 2023/5/16 20:25
 * @Version 1.0
 */
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String deleteUser(int id) {
        int delete = userDao.deleteUser(id);
        if (delete == 1) {
            return "删除成功！";
        } else {
            return "删除失败！";
        }

    }

    @Override
    public List<User> viewUser() {
        return userDao.viewUser();
    }

    @Override
    public ResponseData saveUser(User user) {
        int save = userDao.saveUser(user);
        if (save == 1) {
            return new ResponseData("保存成功!", 200);
        }
        return ResponseData.fail("保存失败");

    }

}
