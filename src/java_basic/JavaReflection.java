package java_basic;


//import org.apache.log4j.Logger;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;


public class JavaReflection {

    /**
     * JAVA 获取一个类的若干方法
     * 并利用反射机制查看类内变量等
     * @throws Exception
     */
    public void loadClass() throws Exception {
        // 获取一个类的若干方法
        Class<?> cmd1 = utill.CMD.class;
        Class<?> cmd2 = Class.forName("utill.CMD");
        Class<?> cmd3 = this.getClass().getClassLoader().loadClass("utill.CMD");


        String className = "java.lang.Runtime";
        Class<?> runtimeClass1 = Class.forName(className);
        Class<?> runtimeClass2 = java.lang.Runtime.class;
        Class<?> runtimeClass3 = ClassLoader.getSystemClassLoader().loadClass(className); // 第三种也可以这么写，反正就是要找打一个classloader类，调用其LoadClass的方法
        System.out.println("修改前————————>");
        // 反射调用类变量
        Field[] f = runtimeClass1.getDeclaredFields();
        for (Field _f : f) {
            String _f_name = _f.getName();
            String mod = Modifier.toString(_f.getModifiers());
            System.out.println(String.format(
                    "%s %s", mod, _f_name
            ));
        }

        // 反射调用类方法

        // getMethod只能获取到当前类和父类的所有有权限的方法(如：public)， 28个
        // getDeclaredMethod能获取到当前类的所有成员方法(不包含父类)。 22个

        Method[] m = runtimeClass1.getMethods();
//        Method[] m = runtimeClass1.getDeclaredMethods();
        System.out.println(m.length);
        for (Method _m : m
        ) {
            String mod = Modifier.toString(_m.getModifiers()); // 取得访问权限 getModifiers返回的是字节码，通过异或（可能）操作，可以获取响应权限
            String metName = _m.getName(); // 取得方法名称
            String return_type = _m.getReturnType().getName(); // 获取返回的东西
            Class<?> xx[] = _m.getParameterTypes();
            StringBuilder strb = new StringBuilder();
            for (Class<?> x :
                    xx) {
                strb.append(x.getName()); // 稍微解析一下 入口参数
            }
            System.out.println(String.format(
                    "%s %s(%s) -> %s", mod, metName, strb.toString().replace(";", " , "), return_type
            ));
        }

    }


    /**
     * 利用反射机制来获取runtime，命令执行
     * @throws Exception
     */
    public void execCmd() throws Exception {
        // 直接调用 cmd
        System.out.println(IOUtils.toString(Runtime.getRuntime().exec("cmd /c dir").getInputStream(), "GBK"));

        // 获取Runtime类对象
        Class<?> runtimeClass1 = Class.forName("java.lang.Runtime");
        // 获取构造方法
        // 使用Runtime类的Class对象获取Runtime类的无参数构造方法(getDeclaredConstructor())
        // 因为Runtime的构造方法是private的我们无法直接调用，所以我们需要通过反射去修改方法的访问权限(constructor.setAccessible(true))。
        Constructor<?> constructor = runtimeClass1.getDeclaredConstructor();
        constructor.setAccessible(true);
        // 创建Runtime类示例，等价于 Runtime rt = new Runtime();
        Object runtimeInstance = constructor.newInstance();
        // 获取Runtime的exec(String cmd)方法
        Method runtimeMethod = runtimeClass1.getMethod("exec", String.class);
        String cmd = "cmd /c dir";
        // 调用exec方法，等价于 rt.exec(cmd);
        Process process = (Process) runtimeMethod.invoke(runtimeInstance, cmd);
        // 输出命令执行结果
        System.out.println(IOUtils.toString(process.getInputStream(), "GBK"));
    }
}
