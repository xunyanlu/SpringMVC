package cn.edu.guet.dao.impl;

import cn.edu.guet.bean.Users;
import cn.edu.guet.dao.BaseDao;
import cn.edu.guet.util.DBConnection;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


public class BaseDaoImpl<T> implements BaseDao<T> {


    PreparedStatement pstmt;
    Connection conn;
    private Class<T> persistentClass;

    public BaseDaoImpl() {
        conn = DBConnection.getConn();
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        persistentClass = (Class<T>) type.getActualTypeArguments()[0];
    }

    @Override
    public int save(T t) {
        String sql = "INSERT INTO " + t.getClass().getSimpleName().toLowerCase() + " (";
        List<Method> list = this.matchPojoMethods(t, "get");
        Iterator<Method> iter = list.iterator();
        Object obj[] = new Object[list.size()];
        int i = 0;
        try {
            while (iter.hasNext()) {
                Method method = iter.next();
                sql += method.getName().substring(3).toLowerCase() + ",";
                if (method.getReturnType().getSimpleName().indexOf("Timestamp") != -1) {
                    obj[i] = Timestamp.valueOf(method.invoke(t, new Object[]{}).toString());
                } else {
                    obj[i] = method.invoke(t, new Object[]{});
                }
                i++;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // 去掉最后一个,符号insert insert into table name(id,name,email) values(
        sql = sql.substring(0, sql.lastIndexOf(",")) + ") values(";
        // 拼装预编译SQL语句insert insert into table name(id,name,email) values(?,?,?,
        for (int j = 0; j < list.size(); j++) {
            sql += "?,";
        }
        // 去掉SQL语句最后一个,符号insert insert into table name(id,name,email) values(?,?,?);
        sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
        // 到此SQL语句拼接完成,打印SQL语句
        System.out.println("自动生成的SQL语句：" + sql);
        int affectRow = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            uuid = uuid.replaceAll("[a-z]", "").substring(0, 15);
            Long id = Long.parseLong(uuid);
            for (int j = 0; j < obj.length; j++) {
                if (list.get(j).getName().equals("getId")) {
                    pstmt.setObject(j + 1, id);
                } else {
                    pstmt.setObject(j + 1, obj[j]);
                }
            }
            affectRow = pstmt.executeUpdate();
            return affectRow;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConn(conn);
        }
        return 0;
    }

    @Override
    public int update(long Id,T t) {

        //persistentClass.getSimpleName().toLowerCase():User的类
        String sql = "UPDATE "+persistentClass.getSimpleName().toLowerCase()+" SET ";
        List<Method> list = this.matchPojoMethods(t, "get");
        Iterator<Method> iter = list.iterator();
        Object obj[] = new Object[list.size()];
        int i = 0;
        try {
            while (iter.hasNext()) {
                Method method = iter.next();
                sql += method.getName().substring(3).toLowerCase() + "=?,";
                if (method.getReturnType().getSimpleName().indexOf("Timestamp") != -1) {
                    obj[i] = Timestamp.valueOf(method.invoke(t, new Object[]{}).toString());
                } else {
                    obj[i] = method.invoke(t, new Object[]{});
                }
                i++;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // 去掉最后一个,符号insert insert into table name(id,name,email) values(
        sql = sql.substring(0, sql.lastIndexOf(",")) +" WHERE Id=?";
        // 拼装预编译SQL语句insert insert into table name(id,name,email) values(?,?,?,
        // 去掉SQL语句最后一个,符号insert insert into table name(id,name,email) values(?,?,?);
        // 到此SQL语句拼接完成,打印SQL语句
        System.out.println("自动生成的SQL语句：" + sql);
        int affectRow = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(4, Id);

            Long id = Id;

            for (int j = 0; j < obj.length; j++) {
                if (list.get(j).getName().equals("getId")) {
                    pstmt.setObject(j + 1, id);
                } else {
                    pstmt.setObject(j + 1, obj[j]);
                }
            }
            affectRow = pstmt.executeUpdate();
            return affectRow;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return 0;
    }

    private List<Method> matchPojoMethods(T entity, String methodName) {
        // 获得当前Pojo所有方法对象
        Method[] methods = entity.getClass().getDeclaredMethods();
        // List容器存放所有带get字符串的Method对象
        List<Method> list = new ArrayList<Method>();
        // 过滤当前Pojo类所有带get字符串的Method对象,存入List容器
        for (int index = 0; index < methods.length; index++) {
            if (methods[index].getName().indexOf(methodName) != -1) {
                list.add(methods[index]);
            }
        }
        return list;
    }

    @Override
    public T getObjectById(long id) {
        T t = null;
        String sql = "SELECT * FROM " + persistentClass.getSimpleName().toLowerCase() + " WHERE id=?";
        System.out.println("查找的SQL：" + sql);
        try {

            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                t = persistentClass.newInstance();// 反射动态创建对象
                List<Method> list = this.matchPojoMethods(t, "set");
                Iterator<Method> iter = list.iterator();
                while (iter.hasNext()) {
                    Method method = iter.next();
                    if (method.getParameterTypes()[0].getName().indexOf("String") != -1) {
                        method.invoke(t, rs.getString(method.getName().substring(3).toLowerCase()));
                    } else if (method.getParameterTypes()[0].getName().indexOf("int") != -1) {
                        method.invoke(t, rs.getInt(method.getName().substring(3).toLowerCase()));
                    } else if (method.getParameterTypes()[0].getName().indexOf("Long") != -1) {
                        method.invoke(t, rs.getLong(method.getName().substring(3).toLowerCase()));
                    }
                }
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> getObjectById() {

        T t = null;
        String sql = "SELECT * FROM " + persistentClass.getSimpleName().toLowerCase();
        List<T> objectList=new ArrayList<>();
        System.out.println("查找的SQL：" + sql);
        try {

            pstmt = conn.prepareStatement(sql);
//            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                t = persistentClass.newInstance();// 反射动态创建对象
                objectList.add(t);
                List<Method> list = this.matchPojoMethods(t, "set");
                Iterator<Method> iter = list.iterator();
                while (iter.hasNext()) {
                    Method method = iter.next();
                    if (method.getParameterTypes()[0].getName().indexOf("String") != -1) {
                        method.invoke(t, rs.getString(method.getName().substring(3).toLowerCase()));
                    } else if (method.getParameterTypes()[0].getName().indexOf("int") != -1) {
                        method.invoke(t, rs.getInt(method.getName().substring(3).toLowerCase()));
                    } else if (method.getParameterTypes()[0].getName().indexOf("Long") != -1) {
                        method.invoke(t, rs.getLong(method.getName().substring(3).toLowerCase()));
                    }
                }

            }
            return objectList;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
