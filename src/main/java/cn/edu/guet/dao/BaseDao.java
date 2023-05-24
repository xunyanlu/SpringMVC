package cn.edu.guet.dao;



//T是指泛型：没有指定类型，例如save方法，可用user的对象，也可以用student的对象
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @Author liwei
 * @Date 2023/1/4 09:32
 * @Version 1.0
 */
public interface BaseDao<T> {

    int save(T t) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
    T get(int id);

}
