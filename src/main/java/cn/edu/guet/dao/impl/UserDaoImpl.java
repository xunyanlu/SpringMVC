package cn.edu.guet.dao.impl;

import cn.edu.guet.bean.User;
import cn.edu.guet.dao.UserDao;
import cn.edu.guet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author liwei
 * @Date 2023/5/16 20:27
 * @Version 1.0
 */
public class UserDaoImpl implements UserDao {


    @Override
    public int deleteUser(int id) {
        Connection conn = DBConnection.getConn();
        PreparedStatement pstmt;//Statement：语句，PreparedStatement：预编译语句对象
        String url="jdbc:oracle:thin:@106.55.182.14:1521:orcl";
        String sql = "DELETE FROM users WHERE id=?";
        int delete=0;
        try {

            if (conn!=null) {
                System.out.println("链接成功");
                conn.setAutoCommit(false);
                System.out.println(id);
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                delete = pstmt.executeUpdate();//正常情况下（如果修改成功，delete的值是1）
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            DBConnection.closeConn(conn);

        }
        return delete;
    }
    @Override
    public List<User> viewUser() {
        Connection conn = DBConnection.getConn();
        PreparedStatement pstmt;//Statement：语句，PreparedStatement：预编译语句对象
        ResultSet rs = null;
        String url="jdbc:oracle:thin:@106.55.182.14:1521:orcl";
        String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();
        try {

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String address = rs.getString("address");
                User user = new User(id, username, address);
                userList.add(user);
                System.out.println(userList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConn(conn);
        }
        return userList;
    }

    int i=1;
    @Override
    public int saveUser(User user) {
        Connection conn = DBConnection.getConn();
        PreparedStatement pstmt;//Statement：语句，PreparedStatement：预编译语句对象
        String url="jdbc:oracle:thin:@106.55.182.14:1521:orcl";
        String sql = "INSERT INTO users VALUES(?,?,?)";
        try {

            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(3,i);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getAddress());
            i++;

            pstmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            DBConnection.closeConn(conn);
        }
        return 0;
    }

}
