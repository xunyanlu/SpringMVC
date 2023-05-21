package cn.edu.guet.mvc;

import cn.edu.guet.ioc.BeanFactory;
import com.google.gson.GsonBuilder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author liwei
 * @Date 2020/9/5 15:20
 * @Version 1.0
 */
@WebServlet(value = "*.do")
public class DispatcherServlet extends HttpServlet {

    Map<String, ControllerMapping> controllerMapping;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        controllerMapping = (Map<String, ControllerMapping>) config.getServletContext().getAttribute("cn.guet.web.controller");
        System.out.println("��application�������ȡ��" + controllerMapping);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ControllerMapping mapping = null;
        String requestURI = request.getRequestURI();
        System.out.println("�����ַ��" + requestURI);
        requestURI = StringUtils.substringBetween(requestURI, request.getContextPath(), ".do");
        System.out.println("ȥ��ǰ׺�ͺ�׺��" + requestURI);
        if (controllerMapping.containsKey(requestURI)) {
            mapping = controllerMapping.get(requestURI);
        }

        Class controllerClass = mapping.getControllerClass();
        Method handleMethod = mapping.getHandleMethod();
        Class[] parameterType = handleMethod.getParameterTypes();

        /*
        �����ȡ����������
         */
        List<String> paramterList = new ArrayList<String>();
        Parameter[] params = handleMethod.getParameters();
        for (Parameter parameter : params) {
            System.out.println("�������֣�" + parameter.getName());
            paramterList.add(parameter.getName());
        }

        Object[] parameterValues = new Object[parameterType.length];
        Object obj = null;
        try {
            String controllerClassName=controllerClass.getSimpleName();
            controllerClassName=StringUtils.replaceChars(controllerClassName, controllerClassName.substring(0, 1),controllerClassName.substring(0, 1).toLowerCase());
            obj = BeanFactory.getInstance().getBean(controllerClassName);
//             obj = controllerClass.newInstance();
            for (int i = 0; i < parameterType.length; i++) {

                if (ClassUtils.isAssignable(parameterType[i], HttpServletRequest.class)) {
                    parameterValues[i] = request;
                } else if (ClassUtils.isAssignable(parameterType[i], HttpServletResponse.class)) {
                    parameterValues[i] = response;
                } else if (ClassUtils.isAssignable(parameterType[i], HttpSession.class)) {
                    parameterValues[i] = request.getSession();
                } else if (parameterType[i].isPrimitive()) {
                    if (parameterType[i].getTypeName().equals("int")) {
                        parameterValues[i] = Integer.parseInt(request.getParameter(paramterList.get(i)));
                    } else if (parameterType[i].getTypeName().equals("float")) {
                        parameterValues[i] = Float.parseFloat(request.getParameter(paramterList.get(i)));
                    }
                } else if (ClassUtils.isAssignable(parameterType[i], String.class)) {
                    System.out.println("String............" + request.getParameter(paramterList.get(i)));
                    parameterValues[i] = request.getParameter(paramterList.get(i));
                } else {
                    //Bean
                    Object pojo = parameterType[i].newInstance();
                    //�õ����������еĲ�����Map<������, value>
                    //��ȡ���������
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    //beanutils���Զ���map���key��bean�����������з��丳ֵ
                    BeanUtils.populate(pojo, parameterMap);
                    parameterValues[i] = pojo;
                }
            }
            /*
            ͨ��������÷��������ҰѲ�����ֵ��ֵ��
             */

            Object returnValue = handleMethod.invoke(obj, parameterValues);
            if (returnValue != null && returnValue instanceof String) { //�������ص���һ���ַ�����
                String path = returnValue.toString();
                if (StringUtils.startsWith(path, "redirect:")) {
                    //�ض���
                    response.sendRedirect(request.getContextPath() + StringUtils.substringAfter(path, "redirect:"));
                } else {
                    //ת��
                    request.getRequestDispatcher(path).forward(request, response);
                }
            } else if (returnValue != null && !(returnValue instanceof String)) {
                response.setContentType("application/json; charset=UTF-8");
                //���ص���һ��bean�����ͻ��˷��͵���ajax���󣬲�����beanת����json
                String json = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .setPrettyPrinting()
                        .create()
                        .toJson(returnValue);
                PrintWriter out = response.getWriter();
                out.write(json);
                out.flush();
                out.close();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}