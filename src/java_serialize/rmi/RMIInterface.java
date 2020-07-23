package java_serialize.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI测试接口
 */
public interface RMIInterface extends Remote {

    /**
     * RMI测试方法
     *
     * @return 返回测试字符串
     */
    String test() throws RemoteException;

    String helloWorld(String name) throws RemoteException;

}