package java_jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MariadbConnection {


    /**
     * 为什么删掉了 ForName 但还是可以正常运行呢？
     *
     * 利用了 Java SPI(Service Provider Interface)的机制
     * 因为DriverManager在初始化的时候会调用java.util.ServiceLoader类提供的SPI机制，
     * Java会自动扫描jar包中的META-INF/services目录下的文件，
     * 并且还会自动的Class.forName(文件中定义的类)，
     *
     * mariadb-java-client-2.6.1.jar!\META-INF\services\java.sql.Driver::org.mariadb.jdbc.Driver(54)
     *
     * 这也就解释了为什么不需要Class.forName也能够成功连接数据库的原因了。所以说，就算没有明写这一方法，也是可以获取到的。
     *
     */
    public static void Connection2() {
        try {
            String CLASS_NAME = "org.mariadb.jdbc.Driver";
            String URL = "jdbc:mariadb://localhost:3306";
            String USERNAME = "root";
            String PASSWORD = "password";
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为什么需要 ForName,这句话看上去没啥用啊？
     *
     * forName语句实际上是反射 org.mariadb.jdbc.Driver 这个类，但是不初始化类方法。党该类被加载，初始化的时候，static内的语句也会被执行
     *
     * 该语句会触发类加载 Driver:88:static 方法
     *
     *     static {
     *         try {
     *             DriverManager.registerDriver(new Driver(), new DeRegister());
     *         } catch (SQLException var1) {
     *             throw new RuntimeException("Could not register driver", var1);
     *         }
     *     }
     * 反射某个类又不想初始化类方法有两种途径
     *
     * 一个是 class.forName()
     * 一个是 ClassLoader.load()
     *
     */
    public static void Connection1() {
        try {
            String CLASS_NAME = "org.mariadb.jdbc.Driver";
            String URL = "jdbc:mariadb://localhost:3306";
            String USERNAME = "root";
            String PASSWORD = "password";
            Class.forName(CLASS_NAME);// 注册JDBC驱动类
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        Connection1();
        Connection2();
    }
}
