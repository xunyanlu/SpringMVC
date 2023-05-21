package cn.edu.guet.mvc;

import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
//import sun.misc.Request;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Author liwei
 * @Date 2020/9/5 10:33
 * @Version 1.0
 */
public class Configuration {

    public Map<String, ControllerMapping> config() throws URISyntaxException {

        Map<String, ControllerMapping> controllerMapping = new HashMap<String, ControllerMapping>();

        ResourceBundle bundle = ResourceBundle.getBundle("config");
        String controllerPackageName = bundle.getString("controller.package");
        /**
         * �ѱ���ת��Ϊ·��
         */
        URI uri = Configuration.class.getResource("/" + controllerPackageName.replace(".", "/")).toURI();
        System.out.println(uri.toString());

        File file = new File(uri);

        String controllerClassNames[] = file.list();
        for (String className : controllerClassNames) {
            if (className.endsWith(".class")) {
                String fullClassName = controllerPackageName + "." + StringUtils.substringBefore(className, ".class");
                System.out.println("ȫ������" + fullClassName);
                try {
                    Class controllerClass = Class.forName(fullClassName);
                /*
                ���clazz����Controllerע�⣬�Ž�һ������
                 */
                    if (controllerClass.isAnnotationPresent(Controller.class)) {
                        Method methods[] = MethodUtils.getMethodsWithAnnotation(controllerClass, RequestMapping.class);
                    /*
                    ��ע�����Ϣ��ʱ�洢��һ��map�У��Ա���Servlet���յ���Ӧ��������ҵ���Ӧ�Ŀ������ķ���ȥ��������
                     */
                        for (Method method : methods) {
                            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                            ControllerMapping mapping = new ControllerMapping(controllerClass, method);
                            System.out.println("ע���ֵ��" + annotation.value());
                            controllerMapping.put(annotation.value(), mapping);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return controllerMapping;
    }
}