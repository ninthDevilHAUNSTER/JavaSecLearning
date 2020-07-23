package java_serialize.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 接口实现
 */
public class RMIInterfaceImpl extends UnicastRemoteObject implements RMIInterface {

    private static final long serialVersionUID = 1L;

    /**
     * 继承Remote接口
     *
     * @throws RemoteException 接口出错
     */
    protected RMIInterfaceImpl() throws RemoteException {
        super();
    }

    /**
     * RMI测试方法 1
     *
     * @return 返回测试字符串
     */
    @Override
    public String test() throws RemoteException {
        return "Hello RMI~";
    }

    /**
     * RMI测试方法 2
     *
     * @return 返回测试字符串
     */
    @Override
    public String helloWorld(String name) throws RemoteException {
        return "Hello " + name;
    }

}