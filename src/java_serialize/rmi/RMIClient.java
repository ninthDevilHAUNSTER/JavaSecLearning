package java_serialize.rmi;


import javax.xml.transform.Transformer;
import java.lang.reflect.InvocationHandler;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java_serialize.rmi.RMIServer.*;

/**
 * RMI 客户端
 *
 * 有个反序列化的洞，我不是很看得明白....
 */
public class RMIClient {


    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            String[] regs = registry.list();
            for (String reg : regs) {
                System.out.println("RMI:" + reg);
            }


//            // 查找远程RMI服务
//            RMIInterface rt = (RMIInterface) Naming.lookup(RMI_NAME);
//
//            // 调用远程接口RMITestInterface类的test方法
//            String result = rt.test();
//
//            // 输出RMI方法调用结果
//            System.out.println(result);
//
//            result = rt.helloWorld("Shaobao");
//            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}